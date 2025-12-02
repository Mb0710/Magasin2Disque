package com.saf.userservice.service;

import com.saf.userservice.dto.ConversationDTO;
import com.saf.userservice.dto.MessageDTO;
import com.saf.userservice.model.Conversation;
import com.saf.userservice.model.Message;
import com.saf.userservice.model.User;
import com.saf.userservice.repository.ConversationRepository;
import com.saf.userservice.repository.MessageRepository;
import com.saf.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MessageService messageService;

    private User sender;
    private User receiver;
    private Conversation conversation;
    private Message message;

    @BeforeEach
    void setUp() {
        sender = new User("sender", "sender@example.com", "password");
        sender.setId(1L);

        receiver = new User("receiver", "receiver@example.com", "password");
        receiver.setId(2L);

        conversation = new Conversation(sender, receiver);
        conversation.setId(1L);

        message = new Message(sender, receiver, "Hello!");
        message.setId(1L);
        message.setConversation(conversation);
    }

    @Test
    void testSendMessage_NewConversation() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findConversationBetweenUsers(1L, 2L))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // Act
        MessageDTO result = messageService.sendMessage(1L, 2L, "Hello!");

        // Assert
        assertNotNull(result);
        assertEquals("Hello!", result.getContent());
        assertEquals(1L, result.getSenderId());
        assertEquals(2L, result.getReceiverId());
        verify(conversationRepository, times(2)).save(any(Conversation.class));
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testSendMessage_ExistingConversation() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findConversationBetweenUsers(1L, 2L))
                .thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        // Act
        MessageDTO result = messageService.sendMessage(1L, 2L, "Hello!");

        // Assert
        assertNotNull(result);
        assertEquals("Hello!", result.getContent());
        verify(conversationRepository, times(1)).save(conversation);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testSendMessage_SenderNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> messageService.sendMessage(999L, 2L, "Hello!"));
    }

    @Test
    void testSendMessage_ReceiverNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> messageService.sendMessage(1L, 999L, "Hello!"));
    }

    @Test
    void testGetUserConversations() {
        // Arrange
        List<Conversation> conversations = List.of(conversation);
        List<Message> messages = List.of(message);
        
        when(conversationRepository.findConversationsByUserId(1L)).thenReturn(conversations);
        when(messageRepository.findByConversationId(1L)).thenReturn(messages);

        // Act
        List<ConversationDTO> result = messageService.getUserConversations(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getOtherUserId());
        assertEquals("receiver", result.get(0).getOtherUsername());
    }

    @Test
    void testGetConversationMessages() {
        // Arrange
        List<Message> messages = List.of(message);
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(messageRepository.findByConversationId(1L)).thenReturn(messages);

        // Act
        List<MessageDTO> result = messageService.getConversationMessages(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello!", result.get(0).getContent());
    }

    @Test
    void testGetConversationMessages_UnauthorizedAccess() {
        // Arrange
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> messageService.getConversationMessages(1L, 999L));
    }

    @Test
    void testGetConversationMessages_ConversationNotFound() {
        // Arrange
        when(conversationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> messageService.getConversationMessages(999L, 1L));
    }

    @Test
    void testMarkConversationAsRead() {
        // Arrange
        Message unreadMessage = new Message(sender, receiver, "Test");
        unreadMessage.setId(2L);
        unreadMessage.setRead(false);
        List<Message> messages = List.of(unreadMessage);
        
        when(messageRepository.findByConversationId(1L)).thenReturn(messages);
        when(messageRepository.save(any(Message.class))).thenReturn(unreadMessage);

        // Act
        messageService.markConversationAsRead(1L, 2L);

        // Assert
        verify(messageRepository).save(unreadMessage);
        assertTrue(unreadMessage.isRead());
        assertNotNull(unreadMessage.getReadAt());
    }

    @Test
    void testGetUnreadMessageCount() {
        // Arrange
        when(messageRepository.countUnreadMessages(1L)).thenReturn(5L);

        // Act
        long count = messageService.getUnreadMessageCount(1L);

        // Assert
        assertEquals(5L, count);
    }

    @Test
    void testGetUnreadMessages() {
        // Arrange
        List<Message> unreadMessages = List.of(message);
        when(messageRepository.findUnreadMessagesByReceiverId(1L)).thenReturn(unreadMessages);

        // Act
        List<MessageDTO> result = messageService.getUnreadMessages(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteMessage_Success() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // Act
        messageService.deleteMessage(1L, 1L);

        // Assert
        verify(messageRepository).delete(message);
    }

    @Test
    void testDeleteMessage_Unauthorized() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> messageService.deleteMessage(1L, 2L));
    }

    @Test
    void testDeleteMessage_NotFound() {
        // Arrange
        when(messageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
            () -> messageService.deleteMessage(999L, 1L));
    }

    @Test
    void testSendMessageWithAttachment() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(conversationRepository.findConversationBetweenUsers(1L, 2L))
                .thenReturn(Optional.of(conversation));
        
        Message messageWithAttachment = new Message(sender, receiver, "Photo");
        messageWithAttachment.setAttachmentUrl("/uploads/image.jpg");
        messageWithAttachment.setAttachmentType("image/jpeg");
        messageWithAttachment.setAttachmentName("photo.jpg");
        
        when(messageRepository.save(any(Message.class))).thenReturn(messageWithAttachment);
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);

        // Act
        MessageDTO result = messageService.sendMessageWithAttachment(
            1L, 2L, "Photo", "/uploads/image.jpg", "image/jpeg", "photo.jpg");

        // Assert
        assertNotNull(result);
        assertEquals("/uploads/image.jpg", result.getAttachmentUrl());
        assertEquals("image/jpeg", result.getAttachmentType());
        assertEquals("photo.jpg", result.getAttachmentName());
    }
}
