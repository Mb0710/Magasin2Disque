package com.saf.userservice.service;

import com.saf.userservice.dto.ConversationDTO;
import com.saf.userservice.dto.MessageDTO;
import com.saf.userservice.model.Conversation;
import com.saf.userservice.model.Message;
import com.saf.userservice.model.User;
import com.saf.userservice.repository.ConversationRepository;
import com.saf.userservice.repository.MessageRepository;
import com.saf.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Envoyer un message à un utilisateur
     */
    public MessageDTO sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));
        
        // Trouver ou créer la conversation
        Conversation conversation = conversationRepository
            .findConversationBetweenUsers(senderId, receiverId)
            .orElseGet(() -> {
                Conversation newConv = new Conversation(sender, receiver);
                return conversationRepository.save(newConv);
            });
        
        // Créer et enregistrer le message
        Message message = new Message(sender, receiver, content);
        message.setConversation(conversation);
        message = messageRepository.save(message);
        
        // Mettre à jour la date du dernier message de la conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return convertToDTO(message);
    }
    
    /**
     * Obtenir toutes les conversations d'un utilisateur
     */
    public List<ConversationDTO> getUserConversations(Long userId) {
        List<Conversation> conversations = conversationRepository.findConversationsByUserId(userId);
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        
        for (Conversation conv : conversations) {
            User otherUser = conv.getOtherUser(userId);
            
            // Obtenir le dernier message
            List<Message> messages = messageRepository.findByConversationId(conv.getId());
            String lastMessage = messages.isEmpty() ? "" : messages.get(messages.size() - 1).getContent();
            
            // Compter les messages non lus
            long unreadCount = messages.stream()
                .filter(m -> m.getReceiver().getId().equals(userId) && !m.isRead())
                .count();
            
            ConversationDTO dto = new ConversationDTO(
                conv.getId(),
                otherUser.getId(),
                otherUser.getUsername(),
                otherUser.getEmail(),
                lastMessage,
                conv.getLastMessageAt(),
                unreadCount
            );
            conversationDTOs.add(dto);
        }
        
        return conversationDTOs;
    }
    
    /**
     * Obtenir tous les messages d'une conversation
     */
    public List<MessageDTO> getConversationMessages(Long conversationId, Long userId) {
        // Vérifier que l'utilisateur fait partie de la conversation
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new RuntimeException("Conversation non trouvée"));
        
        if (!conversation.getUser1().getId().equals(userId) && 
            !conversation.getUser2().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette conversation");
        }
        
        List<Message> messages = messageRepository.findByConversationId(conversationId);
        return messages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Marquer les messages d'une conversation comme lus
     */
    public void markConversationAsRead(Long conversationId, Long userId) {
        List<Message> messages = messageRepository.findByConversationId(conversationId);
        
        for (Message message : messages) {
            if (message.getReceiver().getId().equals(userId) && !message.isRead()) {
                message.setRead(true);
                message.setReadAt(LocalDateTime.now());
                messageRepository.save(message);
            }
        }
    }
    
    /**
     * Compter le nombre total de messages non lus
     */
    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }
    
    /**
     * Obtenir tous les messages non lus
     */
    public List<MessageDTO> getUnreadMessages(Long userId) {
        List<Message> messages = messageRepository.findUnreadMessagesByReceiverId(userId);
        return messages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Supprimer un message
     */
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message non trouvé"));
        
        // Vérifier que l'utilisateur est l'expéditeur du message
        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Vous ne pouvez supprimer que vos propres messages");
        }
        
        messageRepository.delete(message);
    }
    
    /**
     * Envoyer un message avec pièce jointe
     */
    public MessageDTO sendMessageWithAttachment(Long senderId, Long receiverId, String content, 
                                                String attachmentUrl, String attachmentType, 
                                                String attachmentName) {
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        User receiver = userRepository.findById(receiverId)
            .orElseThrow(() -> new RuntimeException("Destinataire non trouvé"));
        
        // Trouver ou créer la conversation
        Conversation conversation = conversationRepository
            .findConversationBetweenUsers(senderId, receiverId)
            .orElseGet(() -> {
                Conversation newConv = new Conversation(sender, receiver);
                return conversationRepository.save(newConv);
            });
        
        // Créer et enregistrer le message
        Message message = new Message(sender, receiver, content);
        message.setConversation(conversation);
        message.setAttachmentUrl(attachmentUrl);
        message.setAttachmentType(attachmentType);
        message.setAttachmentName(attachmentName);
        message = messageRepository.save(message);
        
        // Mettre à jour la date du dernier message de la conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        return convertToDTO(message);
    }
    
    /**
     * Convertir une entité Message en DTO
     */
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO(
            message.getId(),
            message.getSender().getId(),
            message.getSender().getUsername(),
            message.getReceiver().getId(),
            message.getReceiver().getUsername(),
            message.getContent(),
            message.getSentAt(),
            message.getReadAt(),
            message.isRead(),
            message.getConversation() != null ? message.getConversation().getId() : null
        );
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setAttachmentType(message.getAttachmentType());
        dto.setAttachmentName(message.getAttachmentName());
        return dto;
    }
}
