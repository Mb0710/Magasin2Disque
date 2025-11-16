package com.saf.magasin.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entité JPA représentant une commande passée par un client.
 * Gère l'ensemble des informations concernant une commande de disques.
 * 
 * @author FCCramptés
 * @version 1.0
 */
@Entity
@Table(name = "commandes")
public class Commande {
    
    /** Identifiant unique de la commande (clé primaire auto-générée) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Utilisateur qui a effectué la commande */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acheteur_id", nullable = false)
    private User acheteur;
    
    /** Numéro unique de la commande (format UUID) */
    @Column(nullable = false, unique = true, length = 50)
    private String numeroCommande;
    
    /** Liste des disques commandés */
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignesCommande = new ArrayList<>();
    
    /** Montant total de la commande */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    /** Statut de la commande (EN_ATTENTE, CONFIRMEE, EXPEDIEE, LIVREE, ANNULEE) */
    @Column(nullable = false, length = 20)
    private String statut = "EN_ATTENTE";
    
    /** Date et heure de création de la commande */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /** Date et heure de la dernière modification */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ============ CONSTRUCTEURS ============
    
    /**
     * Constructeur par défaut.
     * Initialise une commande avec les valeurs par défaut.
     */
    public Commande() {}

    // ============ GETTERS ET SETTERS ============
    
    /**
     * Récupère l'identifiant unique de la commande.
     * @return L'ID de la commande
     */
    public Long getId() { return id; }
    
    /**
     * Définit l'identifiant unique de la commande.
     * @param id L'ID de la commande
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * Récupère l'acheteur (client) de la commande.
     * @return L'utilisateur acheteur
     */
    public User getAcheteur() { return acheteur; }
    
    /**
     * Définit l'acheteur de la commande.
     * @param acheteur L'utilisateur acheteur
     */
    public void setAcheteur(User acheteur) { this.acheteur = acheteur; }
    
    /**
     * Récupère le numéro unique de la commande.
     * @return Le numéro de commande
     */
    public String getNumeroCommande() { return numeroCommande; }
    
    /**
     * Définit le numéro unique de la commande.
     * @param numeroCommande Le nouveau numéro de commande
     */
    public void setNumeroCommande(String numeroCommande) { this.numeroCommande = numeroCommande; }
    
    /**
     * Récupère les lignes de commande (items commandés).
     * @return La liste des lignes de commande
     */
    public List<LigneCommande> getLignesCommande() { return lignesCommande; }
    
    /**
     * Définit les lignes de commande.
     * @param lignesCommande La nouvelle liste de lignes de commande
     */
    public void setLignesCommande(List<LigneCommande> lignesCommande) { 
        this.lignesCommande = lignesCommande; 
    }
    
    /**
     * Récupère le montant total de la commande.
     * @return Le total en BigDecimal
     */
    public BigDecimal getTotal() { return total; }
    
    /**
     * Définit le montant total de la commande.
     * @param total Le nouveau total
     */
    public void setTotal(BigDecimal total) { this.total = total; }
    
    /**
     * Récupère le statut de la commande.
     * @return Le statut actuel
     */
    public String getStatut() { return statut; }
    
    /**
     * Définit le statut de la commande.
     * @param statut Le nouveau statut
     */
    public void setStatut(String statut) { this.statut = statut; }
    
    /**
     * Récupère la date de création de la commande.
     * @return La date et heure de création
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * Définit la date de création de la commande.
     * @param createdAt La nouvelle date de création
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * Récupère la date de dernière modification.
     * @return La date et heure de mise à jour
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    /**
     * Définit la date de dernière modification.
     * @param updatedAt La nouvelle date de mise à jour
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
