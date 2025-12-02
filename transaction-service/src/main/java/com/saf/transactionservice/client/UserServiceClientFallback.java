package com.saf.transactionservice.client;

import com.saf.transactionservice.dto.AnnonceDTO;
import com.saf.transactionservice.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class UserServiceClientFallback implements UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClientFallback.class);

    @Override
    public AnnonceDTO getAnnonce(Long id) {
        logger.warn("user-service indisponible - Fallback pour getAnnonce({})", id);
        // Retourner une annonce par défaut
        AnnonceDTO fallback = new AnnonceDTO();
        fallback.setId(id);
        fallback.setTitre("Annonce temporairement indisponible");
        fallback.setArtiste("Inconnu");
        fallback.setPrix(BigDecimal.ZERO);
        fallback.setDisponible(false);
        return fallback;
    }

    @Override
    public void deleteAnnonce(Long id) {
        logger.warn("user-service indisponible - Impossible de supprimer l'annonce {}", id);
        // Ne rien faire, l'opération sera réessayée plus tard
    }

    @Override
    public void markAnnonceAsUnavailable(Long id) {
        logger.warn("user-service indisponible - Impossible de marquer l'annonce {} comme indisponible", id);
        // Ne rien faire, l'opération sera réessayée plus tard
    }

    @Override
    public UserDTO getUser(Long id) {
        logger.warn("user-service indisponible - Fallback pour getUser({})", id);
        // Retourner un utilisateur par défaut
        UserDTO fallback = new UserDTO();
        fallback.setId(id);
        fallback.setUsername("Utilisateur temporairement indisponible");
        fallback.setEmail("unavailable@example.com");
        return fallback;
    }

    @Override
    public void createNotification(Map<String, Object> notification) {
        logger.warn("user-service indisponible - Notification non envoyée: {}", notification.get("message"));
        // Ne rien faire, la notification sera perdue (ou on pourrait la stocker dans une queue)
    }
}
