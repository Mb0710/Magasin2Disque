package com.saf.transactionservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offres")
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annonce_id", nullable = false)
    private Long annonceId;

    @Column(name = "annonce_titre")
    private String annonceTitre;

    @Column(name = "acheteur_id", nullable = false)
    private Long acheteurId;

    @Column(name = "acheteur_username")
    private String acheteurUsername;

    @Column(name = "acheteur_email")
    private String acheteurEmail;

    @Column(name = "vendeur_id", nullable = false)
    private Long vendeurId;

    @Column(name = "vendeur_username")
    private String vendeurUsername;

    @Column(name = "vendeur_email")
    private String vendeurEmail;

    @Column(name = "prix_propose", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixPropose;

    @Column(name = "prix_initial", precision = 10, scale = 2)
    private BigDecimal prixInitial;

    @Column(length = 20)
    private String statut = "PENDING"; // PENDING, ACCEPTED, REFUSED

    @Column(length = 500)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // Constructeurs
    public Offre() {
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnnonceId() {
        return annonceId;
    }

    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }

    public String getAnnonceTitre() {
        return annonceTitre;
    }

    public void setAnnonceTitre(String annonceTitre) {
        this.annonceTitre = annonceTitre;
    }

    public Long getAcheteurId() {
        return acheteurId;
    }

    public void setAcheteurId(Long acheteurId) {
        this.acheteurId = acheteurId;
    }

    public String getAcheteurUsername() {
        return acheteurUsername;
    }

    public void setAcheteurUsername(String acheteurUsername) {
        this.acheteurUsername = acheteurUsername;
    }

    public String getAcheteurEmail() {
        return acheteurEmail;
    }

    public void setAcheteurEmail(String acheteurEmail) {
        this.acheteurEmail = acheteurEmail;
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

    public String getVendeurEmail() {
        return vendeurEmail;
    }

    public void setVendeurEmail(String vendeurEmail) {
        this.vendeurEmail = vendeurEmail;
    }

    public BigDecimal getPrixPropose() {
        return prixPropose;
    }

    public void setPrixPropose(BigDecimal prixPropose) {
        this.prixPropose = prixPropose;
    }

    public BigDecimal getPrixInitial() {
        return prixInitial;
    }

    public void setPrixInitial(BigDecimal prixInitial) {
        this.prixInitial = prixInitial;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
