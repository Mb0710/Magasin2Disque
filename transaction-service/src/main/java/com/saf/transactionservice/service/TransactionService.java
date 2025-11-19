package com.saf.transactionservice.service;

import com.saf.transactionservice.model.Transaction;
import com.saf.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour gérer les transactions d'achat.
 * 
 * Encapsule la logique applicative pour :
 * - Créer une nouvelle transaction
 * - Récupérer les transactions existantes
 * - Mettre à jour le statut d'une transaction
 * - Valider les données
 * 
 * Ce service fait le lien entre le contrôleur (requêtes HTTP) 
 * et le repository (accès à la base de données).
 * 
 * @author SAF - Team
 * @version 1.0
 * @since 2025-11-19
 */
@Service
public class TransactionService {
    
    /**
     * Repository injecté pour accéder à la base de données
     */
    @Autowired
    private TransactionRepository transactionRepository;
    
    // ============ OPÉRATIONS DE CRÉATION ============
    
    /**
     * Crée une nouvelle transaction d'achat.
     * 
     * Valide les paramètres, crée une Transaction avec le statut PENDING,
     * et la persiste en base de données.
     * 
     * @param buyerId ID de l'acheteur (doit être positif)
     * @param disqueId ID du disque acheté (doit être positif)
     * @param amount Montant de l'achat (doit être > 0)
     * @param description Description optionnelle de l'achat
     * @return la Transaction créée et enregistrée en base
     * @throws IllegalArgumentException si buyerId, disqueId ou amount est invalide
     */
    public Transaction createTransaction(Long buyerId, Long disqueId, BigDecimal amount, String description) {
        // Validation des paramètres
        if (buyerId == null || buyerId <= 0) {
            throw new IllegalArgumentException("L'ID de l'acheteur doit être valide (> 0)");
        }
        if (disqueId == null || disqueId <= 0) {
            throw new IllegalArgumentException("L'ID du disque doit être valide (> 0)");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif (> 0)");
        }
        
        // Créer et enregistrer la transaction
        Transaction transaction = new Transaction(buyerId, disqueId, amount, description);
        transaction.setStatus("PENDING");
        transaction.setTransactionDate(LocalDateTime.now());
        
        return transactionRepository.save(transaction);
    }
    
    // ============ OPÉRATIONS DE LECTURE ============
    
    /**
     * Récupère une transaction par son ID.
     * 
     * @param id l'ID de la transaction à rechercher
     * @return Optional contenant la Transaction si trouvée, vide sinon
     */
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    /**
     * Récupère toutes les transactions enregistrées.
     * 
     * @return Liste de toutes les transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    /**
     * Récupère l'historique d'achat d'un utilisateur spécifique.
     * 
     * @param buyerId l'ID de l'acheteur
     * @return Liste des transactions effectuées par cet acheteur
     */
    public List<Transaction> getTransactionsByBuyer(Long buyerId) {
        return transactionRepository.findByBuyerId(buyerId);
    }
    
    /**
     * Récupère les achats d'un disque spécifique.
     * 
     * Utile pour connaître la popularité d'un disque.
     * 
     * @param disqueId l'ID du disque
     * @return Liste des transactions portant sur ce disque
     */
    public List<Transaction> getTransactionsByDisque(Long disqueId) {
        return transactionRepository.findByDisqueId(disqueId);
    }
    
    /**
     * Récupère les transactions avec un statut spécifique.
     * 
     * @param status le statut à rechercher (ex: "COMPLETED", "PENDING")
     * @return Liste des transactions ayant ce statut
     */
    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status);
    }
    
    /**
     * Récupère les transactions d'un acheteur avec un statut donné.
     * 
     * Par exemple : tous les achats complétés d'un utilisateur.
     * 
     * @param buyerId l'ID de l'acheteur
     * @param status le statut à rechercher
     * @return Liste des transactions correspondant aux critères
     */
    public List<Transaction> getTransactionsByBuyerAndStatus(Long buyerId, String status) {
        return transactionRepository.findByBuyerIdAndStatus(buyerId, status);
    }
    
    // ============ OPÉRATIONS DE MISE À JOUR ============
    
    /**
     * Marque une transaction comme complétée.
     * 
     * Passe le statut de la transaction à "COMPLETED".
     * Appelé après une validation réussie du paiement par exemple.
     * 
     * @param transactionId l'ID de la transaction à mettre à jour
     * @return la Transaction mise à jour, ou Optional.empty() si non trouvée
     */
    public Optional<Transaction> completeTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> {
                    transaction.setStatus("COMPLETED");
                    return transactionRepository.save(transaction);
                });
    }
    
    /**
     * Marque une transaction comme annulée.
     * 
     * Passe le statut de la transaction à "CANCELLED".
     * Appelé quand l'achat est annulé ou un remboursement effectué.
     * 
     * @param transactionId l'ID de la transaction à annuler
     * @return la Transaction mise à jour, ou Optional.empty() si non trouvée
     */
    public Optional<Transaction> cancelTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> {
                    transaction.setStatus("CANCELLED");
                    return transactionRepository.save(transaction);
                });
    }
    
    /**
     * Met à jour le statut d'une transaction à une valeur arbitraire.
     * 
     * Permet une flexibilité accrue pour des statuts personnalisés.
     * 
     * @param transactionId l'ID de la transaction à mettre à jour
     * @param newStatus le nouveau statut
     * @return la Transaction mise à jour, ou Optional.empty() si non trouvée
     */
    public Optional<Transaction> updateTransactionStatus(Long transactionId, String newStatus) {
        return transactionRepository.findById(transactionId)
                .map(transaction -> {
                    transaction.setStatus(newStatus);
                    return transactionRepository.save(transaction);
                });
    }
    
    // ============ OPÉRATIONS DE SUPPRESSION ============
    
    /**
     * Supprime une transaction par son ID.
     * 
     * À utiliser avec prudence en production (perte de données).
     * Généralement, on préfère annuler une transaction plutôt que la supprimer.
     * 
     * @param transactionId l'ID de la transaction à supprimer
     */
    public void deleteTransaction(Long transactionId) {
        transactionRepository.deleteById(transactionId);
    }
}
