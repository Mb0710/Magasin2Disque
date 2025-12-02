package com.saf.userservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AnnonceTest {

    private Annonce annonce;

    @BeforeEach
    void setUp() {
        annonce = new Annonce();
    }

    @Test
    void testAnnonceCreation() {
        annonce.setTitre("Abbey Road");
        annonce.setArtiste("The Beatles");
        annonce.setGenre("Rock");
        annonce.setAnneeSortie(1969);
        annonce.setPrix(new BigDecimal("39.99"));
        annonce.setDescription("Album classique des Beatles");
        annonce.setVendeurId(1L);
        annonce.setVendeurUsername("seller");

        assertEquals("Abbey Road", annonce.getTitre());
        assertEquals("The Beatles", annonce.getArtiste());
        assertEquals("Rock", annonce.getGenre());
        assertEquals(1969, annonce.getAnneeSortie());
        assertEquals(new BigDecimal("39.99"), annonce.getPrix());
        assertEquals("Album classique des Beatles", annonce.getDescription());
        assertEquals(1L, annonce.getVendeurId());
        assertEquals("seller", annonce.getVendeurUsername());
    }

    @Test
    void testDefaultValues() {
        assertTrue(annonce.isDisponible());
        assertEquals("NEUF", annonce.getEtat());
        assertNotNull(annonce.getCreatedAt());
    }

    @Test
    void testSetEtat() {
        annonce.setEtat("OCCASION");
        assertEquals("OCCASION", annonce.getEtat());

        annonce.setEtat("COLLECTOR");
        assertEquals("COLLECTOR", annonce.getEtat());
    }

    @Test
    void testMarkAsUnavailable() {
        annonce.setDisponible(false);
        assertFalse(annonce.isDisponible());
    }

    @Test
    void testImageUrl() {
        String imageUrl = "http://example.com/image.jpg";
        annonce.setImageUrl(imageUrl);
        assertEquals(imageUrl, annonce.getImageUrl());
    }

    @Test
    void testCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        annonce.setCreatedAt(now);
        assertEquals(now, annonce.getCreatedAt());
    }

    @Test
    void testSetId() {
        annonce.setId(1L);
        assertEquals(1L, annonce.getId());
    }
}
