package com.saf.transactionservice.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entité représentant une transaction d'achat dans le système.
 * 
 * Une transaction enregistre l'achat d'un disque par un utilisateur.
 * Elle stocke les informations essentielles : l'acheteur, le disque, le prix, la date et le statut.
 * 
 * Attributs :
 * - id : identifiant unique auto-généré
 * - buyerId : ID de l'utilisateur acheteur (référence vers User)
 * - disqueId : ID du disque acheté (référence vers Disque)
 * - amount : montant total de la transaction (prix du disque)
 * - status : statut de la transaction (PENDING, COMPLETED, CANCELLED)
 * - transactionDate : date et heure de création de la transaction
 * 
 * @author SAF - Team
 * @version 1.0
 * @since 2025-11-19
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    
    /**
     * Identifiant unique de la transaction (clé primaire auto-générée)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Identifiant de l'utilisateur acheteur
     * Référence vers la table users dans MagasinDisque
     */
    @Column(nullable = false, name = "buyer_id")
    private Long buyerId;
    
    /**
     * Identifiant du disque acheté
     * Référence vers la table disques dans MagasinDisque
     */
    @Column(nullable = false, name = "disque_id")
    private Long disqueId;
    
    /**
     * Montant total de la transaction (généralement le prix du disque)
     * Précision : 10 chiffres, 2 décimales
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * Statut de la transaction
     * Valeurs possibles : PENDING (en attente), COMPLETED (complétée), CANCELLED (annulée)
     * Valeur par défaut : PENDING
     */
    @Column(nullable = false, length = 20)
    private String status = "PENDING";
    
    /**
     * Date et heure de création de la transaction
     * Définie automatiquement lors de la création
     */
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
    
    /**
     * Description optionnelle de la transaction
     */
    @Column(length = 255)
    private String description;
    
    // ============ CONSTRUCTEURS ============
    
    /**
     * Constructeur par défaut (utilisé par JPA)
     */
    public Transaction() {}
    
    /**
     * Constructeur avec paramètres pour créer une transaction d'achat
     * 
     * @param buyerId ID de l'acheteur
     * @param disqueId ID du disque acheté
     * @param amount Montant de la transaction
     * @param description Description optionnelle
     */
    public Transaction(Long buyerId, Long disqueId, BigDecimal amount, String description) {
        this.buyerId = buyerId;
        this.disqueId = disqueId;
        this.amount = amount;
        this.description = description;
    }
    
    // ============ GETTERS ET SETTERS ============
    
    /**
     * Récupère l'ID unique de la transaction
     * @return l'ID de la transaction
     */
    public Long getId() { return id; }
    
    /**
     * Définit l'ID de la transaction (généralement défini par JPA)
     * @param id l'ID à assigner
     */
    public void setId(Long id) { this.id = id; }
    
    /**
     * Récupère l'ID de l'acheteur
     * @return l'ID de l'utilisateur qui a acheté
     */
    public Long getBuyerId() { return buyerId; }
    
    /**
     * Définit l'ID de l'acheteur
     * @param buyerId l'ID de l'utilisateur acheteur
     */
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    
    /**
     * Récupère l'ID du disque acheté
     * @return l'ID du disque
     */
    public Long getDisqueId() { return disqueId; }
    
    /**
     * Définit l'ID du disque acheté
     * @param disqueId l'ID du disque
     */
    public void setDisqueId(Long disqueId) { this.disqueId = disqueId; }
    
    /**
     * Récupère le montant de la transaction
     * @return le montant en BigDecimal
     */
    public BigDecimal getAmount() { return amount; }
    
    /**
     * Définit le montant de la transaction
     * @param amount le montant à assigner
     */
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    /**
     * Récupère le statut de la transaction
     * @return le statut (PENDING, COMPLETED, CANCELLED)
     */
    public String getStatus() { return status; }
    
    /**
     * Définit le statut de la transaction
     * @param status le statut à assigner
     */
    public void setStatus(String status) { this.status = status; }
    
    /**
     * Récupère la date de création de la transaction
     * @return la date et heure de la transaction
     */
    public LocalDateTime getTransactionDate() { return transactionDate; }
    
    /**
     * Définit la date de création de la transaction
     * @param transactionDate la date à assigner
     */
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    /**
     * Récupère la description de la transaction
     * @return la description
     */
    public String getDescription() { return description; }
    
    /**
     * Définit la description de la transaction
     * @param description la description à assigner
     */
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Retourne une représentation textuelle de la transaction
     * @return String au format "Transaction{id=..., buyerId=..., disqueId=..., status=...}"
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", buyerId=" + buyerId +
                ", disqueId=" + disqueId +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
