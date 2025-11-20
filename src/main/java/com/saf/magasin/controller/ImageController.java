package com.saf.magasin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {

    @Value("${upload.path:src/main/resources/static/uploads/}")
    private String uploadPath;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validation du fichier
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
            }

            // Vérifier le type de fichier
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/"))) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le fichier doit être une image"));
            }

            // Extensions autorisées
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nom de fichier invalide"));
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Extension non autorisée. Utilisez jpg, jpeg, png, gif ou webp"
                ));
            }

            // Vérifier la taille (10MB max déjà géré par Spring, mais on peut ajouter une vérification custom)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "Fichier trop volumineux (max 10MB)"));
            }

            // Créer le dossier uploads s'il n'existe pas
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Générer un nom de fichier unique
            String uniqueFilename = UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(uploadPath + uniqueFilename);

            // Sauvegarder le fichier
            Files.write(filePath, file.getBytes());

            // Retourner l'URL relative
            String imageUrl = "/uploads/" + uniqueFilename;

            return ResponseEntity.ok(Map.of(
                "message", "Image uploadée avec succès",
                "imageUrl", imageUrl,
                "filename", uniqueFilename
            ));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors de l'upload: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deleteImage(@PathVariable String filename) {
        try {
            // Validation du nom de fichier pour éviter les path traversal attacks
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nom de fichier invalide"));
            }

            Path filePath = Paths.get(uploadPath + filename);
            File file = filePath.toFile();

            if (!file.exists()) {
                return ResponseEntity.status(404).body(Map.of("error", "Image introuvable"));
            }

            if (file.delete()) {
                return ResponseEntity.ok(Map.of("message", "Image supprimée avec succès"));
            } else {
                return ResponseEntity.status(500).body(Map.of("error", "Impossible de supprimer l'image"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Erreur lors de la suppression: " + e.getMessage()
            ));
        }
    }
}
