package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.dao.ProductDao;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Api("API pour les opérations CRUD sur les produits.")
@RestController
public class ProductController {

    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @ApiOperation("Récupère la liste des produits")
    @GetMapping("/Produits")
    public MappingJacksonValue listeProduits() {
        List<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }

    @ApiOperation("Récupère un produit grâce à son ID à condition que celui-ci soit en stock")
    @GetMapping("/Produits/{id}")
    public Optional<Product> afficherUnProduit(@PathVariable Long id) {
        Optional<Product> produit = productDao.findById(id);
            if(produit.isEmpty()) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est introuvable.");
        return produit;
    }

    @GetMapping("test/produits/{prixLimit}")
    public List<Product> testDeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(prixLimit);
    }

    @ApiOperation("Ajout un produit à la liste des produits en stocks.")
    @PostMapping("/Produits")
    public ResponseEntity<Product> ajouterProduit(@Valid @RequestBody Product product) {
        Product productAdded = productDao.save(product);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @ApiOperation("Supprime un produit de la liste des produits en stock a partir de son ID")
    @DeleteMapping("/Produits/{id}")
    public void supprimerProduit(@PathVariable Long id) {
        productDao.deleteById(id);
    }


    @ApiOperation("Prend une ID en paramètre, si un produit existe à cette ID, le modifie, sinon le crée à la première ID disponible")
    @PutMapping("/Produits/{id}")
    public ResponseEntity<Product> updateProduit(@RequestBody Product product, @PathVariable long id) {
        return productDao.findById(id)                                  //Optional
            .map(p -> {                                                 //décomposition nécessaire pour éditer un produit en gardant l'id
                p.setNom(product.getNom());
                p.setPrix(product.getPrix());
                p.setPrixAchat(product.getPrixAchat());
                productDao.save(p);
                return ResponseEntity.ok(p);
            })
            .orElseGet(() -> {                                          //solve Optional
                Product productAdded = productDao.save(product);
                URI location = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/{id}")
                        .buildAndExpand(productAdded.getId())
                        .toUri();                                       //return the real generated id in DB
                return ResponseEntity.created(location).build();
            });
    }
}
