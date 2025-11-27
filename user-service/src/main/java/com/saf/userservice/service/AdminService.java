package com.saf.userservice.service;

import com.saf.userservice.dto.*;
import com.saf.userservice.model.AdminAction;
import com.saf.userservice.model.Annonce;
import com.saf.userservice.model.User;
import com.saf.userservice.repository.AdminActionRepository;
import com.saf.userservice.repository.AnnonceRepository;
import com.saf.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private AdminActionRepository adminActionRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String TRANSACTION_SERVICE_URL = "http://transaction-service";

    // Récupérer les statistiques générales
    public AdminStatsDTO getStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByEnabledTrue();
        long bannedUsers = userRepository.countByIsBannedTrue();
        long totalAnnonces = annonceRepository.count();
        long activeAnnonces = annonceRepository.countByDisponibleTrue();

        // Récupérer le nombre de transactions depuis le transaction-service
        long totalTransactions = 0;
        try {
            ResponseEntity<Long> response = restTemplate.getForEntity(
                    TRANSACTION_SERVICE_URL + "/api/transactions/count", Long.class);
            totalTransactions = response.getBody() != null ? response.getBody() : 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du nombre de transactions: " + e.getMessage());
        }

        // Actions admin aujourd'hui
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        long adminActionsToday = adminActionRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(
                startOfDay, endOfDay).size();

        return new AdminStatsDTO(
                totalUsers, activeUsers, bannedUsers,
                totalAnnonces, activeAnnonces, totalTransactions,
                0, // pending reports (à implémenter si nécessaire)
                adminActionsToday);
    }

    // Bannir un utilisateur
    @Transactional
    public Map<String, Object> banUser(Long userId, BanUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (user.isBanned()) {
            throw new RuntimeException("Cet utilisateur est déjà banni");
        }

        String adminUsername = getCurrentAdminUsername();

        user.setBanned(true);
        user.setBannedAt(LocalDateTime.now());
        user.setBannedReason(request.getReason());
        user.setBannedBy(adminUsername);
        user.setEnabled(false);
        userRepository.save(user);

        // Enregistrer l'action admin
        AdminAction action = new AdminAction(
                adminUsername, "BAN_USER", "USER", userId, user.getUsername(), request.getReason());
        action.setDetails("Durée: " + request.getDuration());
        adminActionRepository.save(action);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Utilisateur banni avec succès");
        response.put("user", convertToUserDetailsDTO(user));

        return response;
    }

    // Débannir un utilisateur
    @Transactional
    public Map<String, Object> unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!user.isBanned()) {
            throw new RuntimeException("Cet utilisateur n'est pas banni");
        }

        String adminUsername = getCurrentAdminUsername();

        user.setBanned(false);
        user.setBannedAt(null);
        user.setBannedReason(null);
        user.setBannedBy(null);
        user.setEnabled(true);
        userRepository.save(user);

        // Enregistrer l'action admin
        AdminAction action = new AdminAction(
                adminUsername, "UNBAN_USER", "USER", userId, user.getUsername(),
                "Utilisateur débanni");
        adminActionRepository.save(action);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Utilisateur débanni avec succès");
        response.put("user", convertToUserDetailsDTO(user));

        return response;
    }

    // Supprimer une annonce
    @Transactional
    public Map<String, Object> deleteAnnonce(Long annonceId, String reason) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        String adminUsername = getCurrentAdminUsername();
        String annonceTitre = annonce.getTitre();

        // Enregistrer l'action avant suppression
        AdminAction action = new AdminAction(
                adminUsername, "DELETE_ANNONCE", "ANNONCE", annonceId, annonceTitre, reason);
        adminActionRepository.save(action);

        // Supprimer l'annonce
        annonceRepository.delete(annonce);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Annonce supprimée avec succès");

        return response;
    }

    // Récupérer tous les utilisateurs avec détails
    public List<UserDetailsDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserDetailsDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les utilisateurs bannis
    public List<UserDetailsDTO> getBannedUsers() {
        List<User> users = userRepository.findByIsBannedTrue();
        return users.stream()
                .map(this::convertToUserDetailsDTO)
                .collect(Collectors.toList());
    }

    // Récupérer toutes les annonces
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAll();
    }

    // Récupérer toutes les transactions
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllTransactions() {
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(
                    TRANSACTION_SERVICE_URL + "/api/transactions/all", List.class);
            return response.getBody() != null ? response.getBody() : List.of();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des transactions: " + e.getMessage());
            return List.of();
        }
    }

    // Récupérer toutes les actions admin
    public List<AdminActionDTO> getAllAdminActions() {
        List<AdminAction> actions = adminActionRepository.findAllByOrderByCreatedAtDesc();
        return actions.stream()
                .map(this::convertToAdminActionDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les actions admin par type
    public List<AdminActionDTO> getAdminActionsByType(String actionType) {
        List<AdminAction> actions = adminActionRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
        return actions.stream()
                .map(this::convertToAdminActionDTO)
                .collect(Collectors.toList());
    }

    // Récupérer les actions admin pour une cible spécifique
    public List<AdminActionDTO> getAdminActionsForTarget(String targetType, Long targetId) {
        List<AdminAction> actions = adminActionRepository
                .findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId);
        return actions.stream()
                .map(this::convertToAdminActionDTO)
                .collect(Collectors.toList());
    }

    // Récupérer un utilisateur par ID avec détails
    public UserDetailsDTO getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return convertToUserDetailsDTO(user);
    }

    // Rechercher des utilisateurs
    public List<UserDetailsDTO> searchUsers(String query) {
        List<User> users = userRepository.findByUsernameContainingOrEmailContaining(query, query);
        return users.stream()
                .map(this::convertToUserDetailsDTO)
                .collect(Collectors.toList());
    }

    // Méthodes utilitaires privées
    private String getCurrentAdminUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "SYSTEM";
    }

    private UserDetailsDTO convertToUserDetailsDTO(User user) {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setEnabled(user.isEnabled());
        dto.setEmailVerified(user.isEmailVerified());
        dto.setBanned(user.isBanned());
        dto.setBannedAt(user.getBannedAt());
        dto.setBannedReason(user.getBannedReason());
        dto.setBannedBy(user.getBannedBy());
        dto.setCreatedAt(user.getCreatedAt());

        // Compter les annonces de l'utilisateur
        int totalAnnonces = annonceRepository.countByVendeurId(user.getId());
        dto.setTotalAnnonces(totalAnnonces);

        // Pour les transactions, on devrait appeler le transaction-service
        dto.setTotalTransactions(0);

        return dto;
    }

    private AdminActionDTO convertToAdminActionDTO(AdminAction action) {
        return new AdminActionDTO(
                action.getId(),
                action.getAdminUsername(),
                action.getActionType(),
                action.getTargetType(),
                action.getTargetId(),
                action.getTargetName(),
                action.getReason(),
                action.getCreatedAt(),
                action.getDetails());
    }
}
