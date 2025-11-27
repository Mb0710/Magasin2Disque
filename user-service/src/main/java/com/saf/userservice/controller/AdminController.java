package com.saf.userservice.controller;

import com.saf.userservice.dto.*;
import com.saf.userservice.model.Annonce;
import com.saf.userservice.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Statistiques du dashboard
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStatistics() {
        try {
            AdminStatsDTO stats = adminService.getStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== GESTION DES UTILISATEURS ==========

    // Récupérer tous les utilisateurs
    @GetMapping("/users")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers() {
        try {
            List<UserDetailsDTO> users = adminService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDetailsDTO> getUserDetails(@PathVariable Long userId) {
        try {
            UserDetailsDTO user = adminService.getUserDetails(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Rechercher des utilisateurs
    @GetMapping("/users/search")
    public ResponseEntity<List<UserDetailsDTO>> searchUsers(@RequestParam String query) {
        try {
            List<UserDetailsDTO> users = adminService.searchUsers(query);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Récupérer les utilisateurs bannis
    @GetMapping("/users/banned")
    public ResponseEntity<List<UserDetailsDTO>> getBannedUsers() {
        try {
            List<UserDetailsDTO> users = adminService.getBannedUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Bannir un utilisateur
    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Map<String, Object>> banUser(
            @PathVariable Long userId,
            @RequestBody BanUserRequest request) {
        try {
            Map<String, Object> response = adminService.banUser(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur serveur"));
        }
    }

    // Débannir un utilisateur
    @PostMapping("/users/{userId}/unban")
    public ResponseEntity<Map<String, Object>> unbanUser(@PathVariable Long userId) {
        try {
            Map<String, Object> response = adminService.unbanUser(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur serveur"));
        }
    }

    // ========== GESTION DES ANNONCES ==========

    // Récupérer toutes les annonces
    @GetMapping("/annonces")
    public ResponseEntity<List<Annonce>> getAllAnnonces() {
        try {
            List<Annonce> annonces = adminService.getAllAnnonces();
            return ResponseEntity.ok(annonces);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Supprimer une annonce
    @DeleteMapping("/annonces/{annonceId}")
    public ResponseEntity<Map<String, Object>> deleteAnnonce(
            @PathVariable Long annonceId,
            @RequestParam(required = false, defaultValue = "Suppression par l'administrateur") String reason) {
        try {
            Map<String, Object> response = adminService.deleteAnnonce(annonceId, reason);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Erreur serveur"));
        }
    }

    // ========== GESTION DES TRANSACTIONS ==========

    // Récupérer toutes les transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Map<String, Object>>> getAllTransactions() {
        try {
            List<Map<String, Object>> transactions = adminService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== HISTORIQUE DES ACTIONS ADMIN ==========

    // Récupérer toutes les actions admin
    @GetMapping("/actions")
    public ResponseEntity<List<AdminActionDTO>> getAllAdminActions() {
        try {
            List<AdminActionDTO> actions = adminService.getAllAdminActions();
            return ResponseEntity.ok(actions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Récupérer les actions admin par type
    @GetMapping("/actions/type/{actionType}")
    public ResponseEntity<List<AdminActionDTO>> getAdminActionsByType(@PathVariable String actionType) {
        try {
            List<AdminActionDTO> actions = adminService.getAdminActionsByType(actionType);
            return ResponseEntity.ok(actions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Récupérer les actions admin pour une cible spécifique
    @GetMapping("/actions/target/{targetType}/{targetId}")
    public ResponseEntity<List<AdminActionDTO>> getAdminActionsForTarget(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        try {
            List<AdminActionDTO> actions = adminService.getAdminActionsForTarget(targetType, targetId);
            return ResponseEntity.ok(actions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Point de terminaison pour vérifier les permissions admin
    @GetMapping("/check-permission")
    public ResponseEntity<Map<String, Object>> checkAdminPermission() {
        return ResponseEntity.ok(Map.of(
                "isAdmin", true,
                "message", "Accès autorisé"));
    }
}
