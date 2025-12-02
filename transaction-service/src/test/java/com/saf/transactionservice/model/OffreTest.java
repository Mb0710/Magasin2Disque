package com.saf.transactionservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OffreTest {

    private Offre offre;

    @BeforeEach
    void setUp() {
        offre = new Offre();
    }

    @Test
    void testOffreCreation() {
        offre.setAnnonceId(1L);
        offre.setAnnonceTitre("Test Album");
        offre.setAcheteurId(100L);
        offre.setAcheteurUsername("buyer");
        offre.setAcheteurEmail("buyer@example.com");
        offre.setVendeurId(200L);
        offre.setVendeurUsername("seller");
        offre.setVendeurEmail("seller@example.com");
        offre.setPrixPropose(new BigDecimal("25.00"));
        offre.setPrixInitial(new BigDecimal("29.99"));
        offre.setMessage("Je suis intéressé");

        assertEquals(1L, offre.getAnnonceId());
        assertEquals("Test Album", offre.getAnnonceTitre());
        assertEquals(100L, offre.getAcheteurId());
        assertEquals("buyer", offre.getAcheteurUsername());
        assertEquals("buyer@example.com", offre.getAcheteurEmail());
        assertEquals(200L, offre.getVendeurId());
        assertEquals("seller", offre.getVendeurUsername());
        assertEquals("seller@example.com", offre.getVendeurEmail());
        assertEquals(new BigDecimal("25.00"), offre.getPrixPropose());
        assertEquals(new BigDecimal("29.99"), offre.getPrixInitial());
        assertEquals("Je suis intéressé", offre.getMessage());
    }

    @Test
    void testDefaultValues() {
        assertEquals("PENDING", offre.getStatut());
        assertNotNull(offre.getCreatedAt());
        assertNull(offre.getRespondedAt());
    }

    @Test
    void testAcceptOffre() {
        LocalDateTime respondTime = LocalDateTime.now();
        offre.setStatut("ACCEPTED");
        offre.setRespondedAt(respondTime);

        assertEquals("ACCEPTED", offre.getStatut());
        assertEquals(respondTime, offre.getRespondedAt());
    }

    @Test
    void testRefuseOffre() {
        LocalDateTime respondTime = LocalDateTime.now();
        offre.setStatut("REFUSED");
        offre.setRespondedAt(respondTime);

        assertEquals("REFUSED", offre.getStatut());
        assertEquals(respondTime, offre.getRespondedAt());
    }

    @Test
    void testSetId() {
        offre.setId(1L);
        assertEquals(1L, offre.getId());
    }

    @Test
    void testSetCreatedAt() {
        LocalDateTime createdTime = LocalDateTime.now();
        offre.setCreatedAt(createdTime);
        assertEquals(createdTime, offre.getCreatedAt());
    }
}
