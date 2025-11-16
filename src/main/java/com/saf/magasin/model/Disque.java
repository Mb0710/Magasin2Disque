package com.saf.magasin.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entité JPA représentant un disque dans le catalogue du magasin.
 * Cette classe gère les informations concernant les disques disponibles à la vente.
 * 
 * @author FCCramptés
 * @version 1.0
 */
@Entity
@Table(name = "disques")
public class Disque {
    
    /** Identifiant unique du disque (clé primaire auto-générée) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** Titre du disque (obligatoire, max 200 caractères) */
    @Column(nullable = false, length = 200)
    private String titre;
    
    /** Nom de l'artiste ou du groupe (obligatoire, max 100 caractères) */
    @Column(nullable = false, length = 100)
    private String artiste;
    
    /** Genre musical du disque (max 50 caractères) */
    @Column(length = 50)
    private String genre;
    
    /** Année de sortie du disque */
    @Column(name = "annee_sortie")
    private Integer anneeSortie;
    
    /** Prix du disque (obligatoire, précision: 10 chiffres, 2 décimales) */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
    
    /** Nombre d'exemplaires en stock (obligatoire, valeur par défaut: 0) */
    @Column(nullable = false)
    private Integer stock = 0;
    
    /** Description détaillée du disque (max 500 caractères) */
    @Column(length = 500)
    private String description;
    
    /** URL de l'image du disque (max 500 caractères) */
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    /** État du disque: NEUF, OCCASION, ou COLLECTOR */
    @Column(length = 20)
    private String etat = "NEUF";
    
    /** Référence au vendeur du disque (relation ManyToOne) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendeur_id", nullable = false)
    private User vendeur;
    
    /** Date de création du disque dans la base de données */
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /** Indicateur de disponibilité du disque (valeur par défaut: true) */
    @Column(nullable = false)
    private boolean disponible = true;

    // ============ CONSTRUCTEURS ============
    
    /**
     * Constructeur par défaut.
     * Initialise le disque avec les valeurs par défaut.
     */
    public Disque() {}

    // ============ GETTERS ET SETTERS ============
    
    /**
     * Récupère l'identifiant unique du disque.
     * @return L'ID du disque
     */
    public Long getId() { return id; }
    
    /**
     * Définit l'identifiant unique du disque.
     * @param id L'ID du disque
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * Récupère le titre du disque.
     * @return Le titre
     */
    public String getTitre() { return titre; }
    
    /**
     * Définit le titre du disque.
     * @param titre Le nouveau titre
     */
    public void setTitre(String titre) { this.titre = titre; }
    
    /**
     * Récupère le nom de l'artiste.
     * @return Le nom de l'artiste
     */
    public String getArtiste() { return artiste; }
    
    /**
     * Définit le nom de l'artiste.
     * @param artiste Le nouveau nom d'artiste
     */
    public void setArtiste(String artiste) { this.artiste = artiste; }
    
    /**
     * Récupère le genre musical.
     * @return Le genre
     */
    public String getGenre() { return genre; }
    
    /**
     * Définit le genre musical.
     * @param genre Le nouveau genre
     */
    public void setGenre(String genre) { this.genre = genre; }
    
    /**
     * Récupère l'année de sortie.
     * @return L'année de sortie
     */
    public Integer getAnneeSortie() { return anneeSortie; }
    
    /**
     * Définit l'année de sortie.
     * @param anneeSortie La nouvelle année de sortie
     */
    public void setAnneeSortie(Integer anneeSortie) { this.anneeSortie = anneeSortie; }
    
    /**
     * Récupère le prix du disque.
     * @return Le prix
     */
    public BigDecimal getPrix() { return prix; }
    
    /**
     * Définit le prix du disque.
     * @param prix Le nouveau prix
     */
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    
    /**
     * Récupère la quantité en stock.
     * @return Le nombre d'exemplaires en stock
     */
    public Integer getStock() { return stock; }
    
    /**
     * Définit la quantité en stock.
     * @param stock Le nouveau nombre d'exemplaires
     */
    public void setStock(Integer stock) { this.stock = stock; }
    
    /**
     * Récupère la description du disque.
     * @return La description
     */
    public String getDescription() { return description; }
    
    /**
     * Définit la description du disque.
     * @param description La nouvelle description
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Récupère l'URL de l'image.
     * @return L'URL de l'image
     */
    public String getImageUrl() { return imageUrl; }
    
    /**
     * Définit l'URL de l'image.
     * @param imageUrl La nouvelle URL d'image
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    /**
     * Récupère l'état du disque.
     * @return L'état (NEUF, OCCASION, COLLECTOR)
     */
    public String getEtat() { return etat; }
    
    /**
     * Définit l'état du disque.
     * @param etat Le nouvel état du disque
     */
    public void setEtat(String etat) { this.etat = etat; }
    
    /**
     * Récupère le vendeur du disque.
     * @return L'utilisateur vendeur
     */
    public User getVendeur() { return vendeur; }
    
    /**
     * Définit le vendeur du disque.
     * @param vendeur Le nouvel utilisateur vendeur
     */
    public void setVendeur(User vendeur) { this.vendeur = vendeur; }
    
    /**
     * Récupère la date de création du disque.
     * @return La date et heure de création
     */
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    /**
     * Définit la date de création du disque.
     * @param createdAt La nouvelle date de création
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * Vérifie si le disque est disponible.
     * @return true si le disque est disponible, false sinon
     */
    public boolean isDisponible() { return disponible; }
    
    /**
     * Définit la disponibilité du disque.
     * @param disponible true pour rendre le disque disponible, false sinon
     */
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
