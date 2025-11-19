package com.saf.userservice.controller;

import com.saf.userservice.model.User;
import com.saf.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 * Endpoints :
 * - POST /api/users/register : enregistrer un nouvel utilisateur
 * - POST /api/users/login : connecter un utilisateur
 * - GET /api/users : lister tous les utilisateurs
 * - GET /api/users/{id} : récupérer un utilisateur par ID
 * - GET /api/users/username/{username} : récupérer par username
 * - PUT /api/users/{id} : mettre à jour un utilisateur
 * - DELETE /api/users/{id} : supprimer un utilisateur
 * 
 * @author SAF - Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Enregistre un nouvel utilisateur.
     * Body attendu : { "username": "...", "email": "...", "password": "...", "role": "USER" }
     * 
     * @param user les données de l'utilisateur
     * @return ResponseEntity avec l'utilisateur créé et son ID
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            Map<String, Object> response = new HashMap<>();
            response.put("id", registeredUser.getId());
            response.put("username", registeredUser.getUsername());
            response.put("email", registeredUser.getEmail());
            response.put("role", registeredUser.getRole());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Connecte un utilisateur.
     * Body attendu : { "username": "...", "password": "..." }
     * 
     * @param loginData map contenant username et password
     * @return ResponseEntity avec l'ID, username, role et message de succès ou erreur
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        Optional<User> userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("username", user.getUsername());
            response.put("role", user.getRole());
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
    }
    
    /**
     * Liste tous les utilisateurs.
     * 
     * @return ResponseEntity avec la liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Récupère un utilisateur par son ID.
     * 
     * @param id l'identifiant de l'utilisateur
     * @return ResponseEntity avec l'utilisateur ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found with id: " + id));
    }
    
    /**
     * Récupère un utilisateur par son username.
     * 
     * @param username le nom d'utilisateur
     * @return ResponseEntity avec l'utilisateur ou 404 si non trouvé
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found with username: " + username));
    }
    
    /**
     * Met à jour un utilisateur.
     * 
     * @param id l'identifiant de l'utilisateur
     * @param updatedUser les données à mettre à jour
     * @return ResponseEntity avec l'utilisateur mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        try {
            User user = userService.update(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Supprime un utilisateur.
     * 
     * @param id l'identifiant de l'utilisateur
     * @return ResponseEntity avec un message de succès
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully with id: " + id));
    }
}
