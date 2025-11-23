package com.saf.userservice.service;

import com.saf.userservice.model.Notification;
import com.saf.userservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification createNotification(Long userId, String type, String message, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRelatedId(relatedId);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    @Transactional
    public void createNewOfferNotification(Long vendeurId, String annonceTitre, Double prixPropose, Long offreId) {
        createNotification(
                vendeurId,
                "NEW_OFFER",
                String.format("Nouvelle offre de %.2f€ pour \"%s\"", prixPropose, annonceTitre),
                offreId);
    }

    @Transactional
    public void createOfferAcceptedNotification(Long acheteurId, String annonceTitre, Double prix, Long transactionId) {
        createNotification(
                acheteurId,
                "OFFER_ACCEPTED",
                String.format("Votre offre de %.2f€ pour \"%s\" a été acceptée!", prix, annonceTitre),
                transactionId);
    }

    @Transactional
    public void createOfferRefusedNotification(Long acheteurId, String annonceTitre, Long offreId) {
        createNotification(
                acheteurId,
                "OFFER_REFUSED",
                String.format("Votre offre pour \"%s\" a été refusée", annonceTitre),
                offreId);
    }

    @Transactional
    public void createTransactionCompletedNotification(Long userId, String annonceTitre, Long transactionId) {
        createNotification(
                userId,
                "TRANSACTION_COMPLETED",
                String.format("Transaction complétée pour \"%s\"", annonceTitre),
                transactionId);
    }

    @Transactional
    public void createNewReviewNotification(Long userId, Long reviewerId, String reviewerUsername, Long reviewId) {
        createNotification(
                userId,
                "NEW_REVIEW",
                String.format("%s a laissé un avis sur votre profil", reviewerUsername),
                reviewId);
    }
}
