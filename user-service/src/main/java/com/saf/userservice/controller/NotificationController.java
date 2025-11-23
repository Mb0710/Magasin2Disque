package com.saf.userservice.controller;

import com.saf.userservice.model.Notification;
import com.saf.userservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    // Créer une notification
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody Notification notification) {
        try {
            Notification saved = notificationRepository.save(notification);
            return ResponseEntity.ok(Map.of(
                    "message", "Notification créée",
                    "notification", saved));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la création de la notification"));
        }
    }

    // Récupérer toutes les notifications d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la récupération des notifications"));
        }
    }

    // Récupérer les notifications non lues
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationRepository
                    .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la récupération des notifications"));
        }
    }

    // Compter les notifications non lues
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        try {
            Long count = notificationRepository.countByUserIdAndIsReadFalse(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors du comptage"));
        }
    }

    // Marquer une notification comme lue
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            return notificationRepository.findById(id)
                    .map(notification -> {
                        notification.setRead(true);
                        notificationRepository.save(notification);
                        return ResponseEntity.ok(Map.of("message", "Notification marquée comme lue"));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la mise à jour"));
        }
    }

    // Marquer toutes les notifications comme lues
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        try {
            List<Notification> unreadNotifications = notificationRepository
                    .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
            unreadNotifications.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(unreadNotifications);

            return ResponseEntity.ok(Map.of(
                    "message", "Toutes les notifications ont été marquées comme lues",
                    "count", unreadNotifications.size()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la mise à jour"));
        }
    }

    // Supprimer une notification
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Notification supprimée"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la suppression"));
        }
    }
}
