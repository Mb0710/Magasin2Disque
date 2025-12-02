package com.saf.userservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password123");
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testDefaultValues() {
        assertEquals("USER", user.getRole());
        assertTrue(user.isEnabled());
        assertFalse(user.isEmailVerified());
        assertFalse(user.isBanned());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void testSetRole() {
        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testBanUser() {
        LocalDateTime banTime = LocalDateTime.now();
        user.setBanned(true);
        user.setBannedAt(banTime);
        user.setBannedReason("Violation des règles");
        user.setBannedBy("admin");

        assertTrue(user.isBanned());
        assertEquals(banTime, user.getBannedAt());
        assertEquals("Violation des règles", user.getBannedReason());
        assertEquals("admin", user.getBannedBy());
    }

    @Test
    void testEmailVerification() {
        user.setEmailVerified(true);
        assertTrue(user.isEmailVerified());
    }

    @Test
    void testVerificationToken() {
        String token = "test-token-123";
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);
        
        user.setVerificationToken(token);
        user.setTokenExpiryDate(expiryDate);

        assertEquals(token, user.getVerificationToken());
        assertEquals(expiryDate, user.getTokenExpiryDate());
    }

    @Test
    void testDisableUser() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testUnbanUser() {
        // Ban first
        user.setBanned(true);
        user.setBannedReason("Test reason");
        
        // Then unban
        user.setBanned(false);
        user.setBannedAt(null);
        user.setBannedReason(null);
        user.setBannedBy(null);

        assertFalse(user.isBanned());
        assertNull(user.getBannedAt());
        assertNull(user.getBannedReason());
        assertNull(user.getBannedBy());
    }

    @Test
    void testSetId() {
        user.setId(1L);
        assertEquals(1L, user.getId());
    }

    @Test
    void testDefaultConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getEmail());
    }
}
