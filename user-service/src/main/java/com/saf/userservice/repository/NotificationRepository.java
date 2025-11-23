package com.saf.userservice.repository;

import com.saf.userservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Trouver toutes les notifications d'un utilisateur (les plus récentes en
    // premier)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Trouver les notifications non lues d'un utilisateur
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Compter les notifications non lues
    Long countByUserIdAndIsReadFalse(Long userId);
}
