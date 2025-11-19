package com.saf.transactionservice.repository;

import com.saf.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour gérer l'accès aux données des transactions en base de données.
 * Fournit des méthodes prédéfinies et personnalisées pour les opérations CRUD
 * sur les entités Transaction.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Récupère toutes les transactions d'un acheteur spécifique.
     *
     * @param buyerId l'identifiant de l'acheteur
     * @return Liste de toutes les transactions effectuées par cet acheteur
     */
    List<Transaction> findByBuyerId(Long buyerId);

    /**
     * Récupère toutes les transactions associées à un disque spécifique.
     *
     * @param disqueId l'identifiant du disque
     * @return Liste de toutes les transactions portant sur ce disque
     */
    List<Transaction> findByDisqueId(Long disqueId);

    /**
     * Récupère toutes les transactions avec un statut spécifique.
     *
     * @param status le statut à rechercher (ex: "COMPLETED", "PENDING", "CANCELLED")
     * @return Liste des transactions ayant ce statut
     */
    List<Transaction> findByStatus(String status);

    /**
     * Récupère toutes les transactions d'un acheteur avec un statut spécifique.
     *
     * @param buyerId l'identifiant de l'acheteur
     * @param status le statut à rechercher
     * @return Liste des transactions correspondant aux deux critères
     */
    List<Transaction> findByBuyerIdAndStatus(Long buyerId, String status);
}
