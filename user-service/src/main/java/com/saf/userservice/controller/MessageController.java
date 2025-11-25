package com.saf.userservice.controller;

import com.saf.userservice.dto.ConversationDTO;
import com.saf.userservice.dto.MessageDTO;
import com.saf.userservice.dto.SendMessageRequest;
import com.saf.userservice.security.JwtUtil;
import com.saf.userservice.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final String UPLOAD_DIR = "uploads/messages/";
    
    /**
     * Extraire l'userId du token JWT
     */
    private Long getUserIdFromToken(jakarta.servlet.http.HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtil.extractUserId(token);
        }
        throw new RuntimeException("Token non trouvé");
    }
    
    /**
     * Envoyer un message
     * POST /api/messages/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestBody SendMessageRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            // Récupérer l'ID de l'utilisateur connecté depuis le token
            Long senderId = getUserIdFromToken(httpRequest);
            
            MessageDTO message = messageService.sendMessage(
                senderId, 
                request.getReceiverId(), 
                request.getContent()
            );
            
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Envoyer un message avec pièce jointe
     * POST /api/messages/send-with-attachment
     */
    @PostMapping("/send-with-attachment")
    public ResponseEntity<?> sendMessageWithAttachment(
            @RequestParam("receiverId") Long receiverId,
            @RequestParam("content") String content,
            @RequestParam("file") MultipartFile file,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long senderId = getUserIdFromToken(httpRequest);
            
            // Créer le dossier d'upload s'il n'existe pas
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Générer un nom de fichier unique
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nom de fichier invalide"));
            }
            
            String extension = originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
            String filename = UUID.randomUUID().toString() + extension;
            String filepath = UPLOAD_DIR + filename;
            
            // Sauvegarder le fichier
            Path path = Paths.get(filepath);
            Files.write(path, file.getBytes());
            
            // Envoyer le message avec les informations du fichier
            MessageDTO message = messageService.sendMessageWithAttachment(
                senderId,
                receiverId,
                content,
                "/uploads/messages/" + filename,
                file.getContentType(),
                originalFilename
            );
            
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Erreur lors de l'upload du fichier: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtenir toutes les conversations de l'utilisateur
     * GET /api/messages/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations(jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            List<ConversationDTO> conversations = messageService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtenir les messages d'une conversation
     * GET /api/messages/conversation/{conversationId}
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable Long conversationId,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            List<MessageDTO> messages = messageService.getConversationMessages(conversationId, userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Marquer une conversation comme lue
     * PUT /api/messages/conversation/{conversationId}/read
     */
    @PutMapping("/conversation/{conversationId}/read")
    public ResponseEntity<?> markConversationAsRead(
            @PathVariable Long conversationId,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            messageService.markConversationAsRead(conversationId, userId);
            return ResponseEntity.ok(Map.of("message", "Conversation marquée comme lue"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtenir le nombre de messages non lus
     * GET /api/messages/unread/count
     */
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            long count = messageService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtenir tous les messages non lus
     * GET /api/messages/unread
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadMessages(jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            List<MessageDTO> messages = messageService.getUnreadMessages(userId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Supprimer un message
     * DELETE /api/messages/{messageId}
     */
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long messageId,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromToken(httpRequest);
            messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok(Map.of("message", "Message supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
