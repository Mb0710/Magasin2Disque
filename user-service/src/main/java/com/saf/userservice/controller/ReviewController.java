package com.saf.userservice.controller;

import com.saf.userservice.model.Review;
import com.saf.userservice.repository.ReviewRepository;
import com.saf.userservice.repository.UserRepository;
import com.saf.userservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Créer un avis
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        try {
            // Vérifier qu'on ne note pas deux fois la même transaction
            if (review.getTransactionId() != null) {
                var existing = reviewRepository.findByTransactionIdAndReviewerId(
                        review.getTransactionId(), review.getReviewerId());
                if (existing.isPresent()) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Vous avez déjà noté cette transaction"));
                }
            }

            // Vérifier que la note est entre 1 et 5
            if (review.getRating() < 1 || review.getRating() > 5) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La note doit être entre 1 et 5"));
            }

            Review saved = reviewRepository.save(review);

            // Créer une notification pour l'utilisateur noté
            try {
                var reviewer = userRepository.findById(review.getReviewerId());
                if (reviewer.isPresent()) {
                    notificationService.createNewReviewNotification(
                            review.getReviewedUserId(),
                            review.getReviewerId(),
                            reviewer.get().getUsername(),
                            saved.getId());
                }
            } catch (Exception e) {
                // Ne pas bloquer si la notification échoue
                System.err.println("Erreur création notification: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Avis publié avec succès",
                    "review", saved));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la création de l'avis: " + e.getMessage()));
        }
    }

    // Récupérer les avis reçus par un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable Long userId) {
        try {
            List<Review> reviews = reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(userId);
            Double avgRating = reviewRepository.calculateAverageRating(userId);
            Long totalReviews = reviewRepository.countByReviewedUserId(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("reviews", reviews);
            response.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
            response.put("totalReviews", totalReviews);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la récupération des avis"));
        }
    }

    // Récupérer les statistiques d'un utilisateur
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            Double avgRating = reviewRepository.calculateAverageRating(userId);
            Long totalReviews = reviewRepository.countByReviewedUserId(userId);

            // Compter les avis par note
            List<Review> allReviews = reviewRepository.findByReviewedUserIdOrderByCreatedAtDesc(userId);
            long rating5 = allReviews.stream().filter(r -> r.getRating() == 5).count();
            long rating4 = allReviews.stream().filter(r -> r.getRating() == 4).count();
            long rating3 = allReviews.stream().filter(r -> r.getRating() == 3).count();
            long rating2 = allReviews.stream().filter(r -> r.getRating() == 2).count();
            long rating1 = allReviews.stream().filter(r -> r.getRating() == 1).count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
            stats.put("totalReviews", totalReviews);
            stats.put("rating5Count", rating5);
            stats.put("rating4Count", rating4);
            stats.put("rating3Count", rating3);
            stats.put("rating2Count", rating2);
            stats.put("rating1Count", rating1);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la récupération des statistiques"));
        }
    }

    // Vérifier si l'utilisateur peut noter une transaction
    @GetMapping("/can-review")
    public ResponseEntity<?> canReview(
            @RequestParam Long transactionId,
            @RequestParam Long reviewerId) {
        try {
            var existing = reviewRepository.findByTransactionIdAndReviewerId(transactionId, reviewerId);
            return ResponseEntity.ok(Map.of("canReview", existing.isEmpty()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la vérification"));
        }
    }
}
