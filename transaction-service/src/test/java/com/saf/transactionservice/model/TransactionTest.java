package com.saf.transactionservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
    }

    @Test
    void testTransactionCreation() {
        transaction.setAnnonceId(1L);
        transaction.setAnnonceTitre("Test Album");
        transaction.setAcheteurId(100L);
        transaction.setAcheteurUsername("buyer");
        transaction.setVendeurId(200L);
        transaction.setVendeurUsername("seller");
        transaction.setPrix(new BigDecimal("29.99"));

        assertEquals(1L, transaction.getAnnonceId());
        assertEquals("Test Album", transaction.getAnnonceTitre());
        assertEquals(100L, transaction.getAcheteurId());
        assertEquals("buyer", transaction.getAcheteurUsername());
        assertEquals(200L, transaction.getVendeurId());
        assertEquals("seller", transaction.getVendeurUsername());
        assertEquals(new BigDecimal("29.99"), transaction.getPrix());
    }

    @Test
    void testDefaultValues() {
        assertEquals("PENDING", transaction.getStatut());
        assertEquals("DIRECT", transaction.getTypeAchat());
        assertNotNull(transaction.getCreatedAt());
        assertNull(transaction.getCompletedAt());
    }

    @Test
    void testCompleteTransaction() {
        LocalDateTime completionTime = LocalDateTime.now();
        transaction.setStatut("COMPLETED");
        transaction.setCompletedAt(completionTime);

        assertEquals("COMPLETED", transaction.getStatut());
        assertEquals(completionTime, transaction.getCompletedAt());
    }

    @Test
    void testCancelTransaction() {
        transaction.setStatut("CANCELLED");
        assertEquals("CANCELLED", transaction.getStatut());
    }

    @Test
    void testTypeAchat() {
        transaction.setTypeAchat("OFFRE_ACCEPTEE");
        assertEquals("OFFRE_ACCEPTEE", transaction.getTypeAchat());
    }

    @Test
    void testSetId() {
        transaction.setId(1L);
        assertEquals(1L, transaction.getId());
    }

    @Test
    void testSetCreatedAt() {
        LocalDateTime createdTime = LocalDateTime.now();
        transaction.setCreatedAt(createdTime);
        assertEquals(createdTime, transaction.getCreatedAt());
    }
}
