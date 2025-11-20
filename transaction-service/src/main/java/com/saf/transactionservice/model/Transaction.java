package com.saf.transactionservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

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

    @Column(name = "vendeur_id", nullable = false)
    private Long vendeurId;

    @Column(name = "vendeur_username")
    private String vendeurUsername;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(length = 20)
    private String statut = "PENDING"; // PENDING, COMPLETED, CANCELLED

    @Column(name = "type_achat", length = 20)
    private String typeAchat = "DIRECT"; // DIRECT, OFFRE_ACCEPTEE

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Constructeurs
    public Transaction() {
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

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getTypeAchat() {
        return typeAchat;
    }

    public void setTypeAchat(String typeAchat) {
        this.typeAchat = typeAchat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
