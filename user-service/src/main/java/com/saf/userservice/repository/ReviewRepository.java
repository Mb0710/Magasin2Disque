package com.saf.userservice.repository;

import com.saf.userservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Trouver tous les avis reçus par un utilisateur
    List<Review> findByReviewedUserIdOrderByCreatedAtDesc(Long reviewedUserId);

    // Trouver tous les avis donnés par un utilisateur
    List<Review> findByReviewerIdOrderByCreatedAtDesc(Long reviewerId);

    // Vérifier si un avis existe déjà pour une transaction
    Optional<Review> findByTransactionIdAndReviewerId(Long transactionId, Long reviewerId);

    // Calculer la note moyenne d'un utilisateur
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUserId = ?1")
    Double calculateAverageRating(Long userId);

    // Compter le nombre d'avis reçus
    Long countByReviewedUserId(Long userId);
}
