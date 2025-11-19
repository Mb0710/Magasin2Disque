package com.saf.magasin.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Contrôleur d'authentification qui délègue les opérations au microservice user-service.
 * Routes :
 * - POST /api/auth/register : délègue à user-service
 * - POST /api/auth/login : délègue à user-service
 * 
 * @author SAF - Team
 * @version 2.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String USER_SERVICE_URL = "http://localhost:8081/api/users";
    
    /**
     * Enregistre un nouvel utilisateur en appelant user-service.
     * 
     * @param request map contenant username, email, password
     * @return ResponseEntity avec le résultat du user-service
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            // Construire le body pour user-service
            Map<String, String> userBody = Map.of(
                "username", request.getOrDefault("username", ""),
                "email", request.getOrDefault("email", ""),
                "password", request.getOrDefault("password", ""),
                "role", request.getOrDefault("role", "USER")
            );
            
            // Appeler POST /api/users/register
            ResponseEntity<?> response = restTemplate.postForEntity(
                USER_SERVICE_URL + "/register",
                userBody,
                Object.class
            );
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de l'enregistrement: " + e.getMessage()));
        }
    }
    
    /**
     * Connecte un utilisateur en appelant user-service.
     * 
     * @param request map contenant username et password
     * @return ResponseEntity avec le résultat du user-service
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            Map<String, String> loginBody = Map.of(
                "username", username != null ? username : "",
                "password", password != null ? password : ""
            );
            
            // Appeler POST /api/users/login
            ResponseEntity<?> response = restTemplate.postForEntity(
                USER_SERVICE_URL + "/login",
                loginBody,
                Object.class
            );
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la connexion: " + e.getMessage()));
        }
    }
}
