package com.saf.transactionservice.dto;

/**
 * DTO représentant les informations d'une annonce depuis user-service
 */
public class AnnonceDTO {
    private Long id;
    private String titre;
    private String artiste;
    private java.math.BigDecimal prix;
    private Long vendeurId;
    private String vendeurUsername;
    private boolean disponible;

    // Constructeurs
    public AnnonceDTO() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public java.math.BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(java.math.BigDecimal prix) {
        this.prix = prix;
    }

    public Long getVendeurId() {
        return vendeurId;
    }

    public void setVendeurId(Long vendeurId) {
        this.vendeurId = vendeurId;
    }

    public String getVendeurUsername() {
        return vendeurUsername;
    }

    public void setVendeurUsername(String vendeurUsername) {
        this.vendeurUsername = vendeurUsername;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
