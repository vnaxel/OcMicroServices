package com.ecommerce.microcommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

//@JsonFilter("monFiltreDynamique")
@Entity
public class Product {
    @Id
    @GeneratedValue
    @ApiModelProperty(hidden = true)
    private Long id;
    @Size(min = 3, max = 25)
    private String nom;
    @Min(1)
    private int prix;
    private int prixAchat;

    public Product() {
    }

    public Product(String nom, int prix, int prixAchat) {
        this.nom = nom;
        this.prix = prix;
        this.prixAchat = prixAchat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getPrixAchat() {
        return prixAchat;
    }

    public void setPrixAchat(int prixAchat) {
        this.prixAchat = prixAchat;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                '}';
    }
}
