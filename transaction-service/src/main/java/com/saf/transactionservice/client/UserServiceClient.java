package com.saf.transactionservice.client;

import com.saf.transactionservice.dto.AnnonceDTO;
import com.saf.transactionservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/annonces/{id}")
    AnnonceDTO getAnnonce(@PathVariable("id") Long id);

    @DeleteMapping("/api/annonces/{id}")
    void deleteAnnonce(@PathVariable("id") Long id);

    @PutMapping("/api/annonces/{id}/mark-unavailable")
    void markAnnonceAsUnavailable(@PathVariable("id") Long id);

    @GetMapping("/api/users/{id}")
    UserDTO getUser(@PathVariable("id") Long id);

    @PostMapping("/api/notifications")
    void createNotification(@RequestBody Map<String, Object> notification);
}
