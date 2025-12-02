package com.saf.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(value = "/user-service", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<Map<String, Object>> userServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service de connexion temporairement indisponible");
        response.put("message", "Nos serveurs d'authentification sont actuellement en maintenance. Veuillez réessayer dans quelques instants.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("statusCode", 503);
        response.put("service", "user-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @RequestMapping(value = "/transaction-service", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<Map<String, Object>> transactionServiceFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Service de transactions temporairement indisponible");
        response.put("message", "Le service de gestion des transactions et offres est actuellement en maintenance. Vos données sont sécurisées et le service sera bientôt rétabli.");
        response.put("status", "SERVICE_UNAVAILABLE");
        response.put("statusCode", 503);
        response.put("service", "transaction-service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
