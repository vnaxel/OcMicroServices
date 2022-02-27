package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.dao.ProductDao;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ProductController {

    private final ProductDao productDao;

    public ProductController(ProductDao productDao) {
        this.productDao = productDao;
    }

    @GetMapping("/Produits")
    public MappingJacksonValue listeProduits() {
        List<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);
        return produitsFiltres;
    }

    @GetMapping("/Produits/{id}")
    public Optional<Product> afficherUnProduit(@PathVariable Long id) {
        return productDao.findById(id);
    }

    @GetMapping("test/produits/{prixLimit}")
    public List<Product> testDeRequetes(@PathVariable int prixLimit) {
        return productDao.findByPrixGreaterThan(prixLimit);
    }

    @PostMapping("/Produits")
    public ResponseEntity<Product> ajouterProduit(@RequestBody Product product) {
        Product productAdded = productDao.save(product);
        if (Objects.isNull(productAdded)) {
            return ResponseEntity.noContent().build();
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping (value = "/Produits/{id}")
    public void supprimerProduit(@PathVariable Long id) {
        productDao.deleteById(id);
    }


    @PutMapping (value = "/Produits/{id}")
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
                        .toUri();                                       //return the real id in DB
                return ResponseEntity.created(location).build();
            });
    }
}
