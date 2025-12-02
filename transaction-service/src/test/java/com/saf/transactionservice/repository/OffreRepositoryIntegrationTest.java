package com.saf.transactionservice.repository;

import com.saf.transactionservice.model.Offre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OffreRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OffreRepository offreRepository;

    private Offre offre1;
    private Offre offre2;

    @BeforeEach
    void setUp() {
        offre1 = new Offre();
        offre1.setAnnonceId(1L);
        offre1.setAnnonceTitre("Album Test 1");
        offre1.setAcheteurId(100L);
        offre1.setAcheteurUsername("acheteur1");
        offre1.setVendeurId(200L);
        offre1.setVendeurUsername("vendeur1");
        offre1.setPrixPropose(new BigDecimal("25.00"));
        offre1.setPrixInitial(new BigDecimal("29.99"));
        offre1.setStatut("PENDING");
        entityManager.persist(offre1);

        offre2 = new Offre();
        offre2.setAnnonceId(2L);
        offre2.setAnnonceTitre("Album Test 2");
        offre2.setAcheteurId(101L);
        offre2.setAcheteurUsername("acheteur2");
        offre2.setVendeurId(200L);
        offre2.setVendeurUsername("vendeur1");
        offre2.setPrixPropose(new BigDecimal("35.00"));
        offre2.setPrixInitial(new BigDecimal("39.99"));
        offre2.setStatut("ACCEPTED");
        entityManager.persist(offre2);

        entityManager.flush();
    }

    @Test
    void testFindByVendeurIdAndStatut() {
        // Act
        List<Offre> pendingOffres = offreRepository.findByVendeurIdAndStatut(200L, "PENDING");

        // Assert
        assertEquals(1, pendingOffres.size());
        assertEquals(new BigDecimal("25.00"), pendingOffres.get(0).getPrixPropose());
    }

    @Test
    void testFindByAcheteurId() {
        // Act
        List<Offre> offres = offreRepository.findByAcheteurId(100L);

        // Assert
        assertEquals(1, offres.size());
        assertEquals("Album Test 1", offres.get(0).getAnnonceTitre());
    }

    @Test
    void testFindByAnnonceId() {
        // Act
        List<Offre> offres = offreRepository.findByAnnonceId(1L);

        // Assert
        assertEquals(1, offres.size());
        assertEquals("acheteur1", offres.get(0).getAcheteurUsername());
    }

    @Test
    void testSaveOffre() {
        // Arrange
        Offre newOffre = new Offre();
        newOffre.setAnnonceId(3L);
        newOffre.setAnnonceTitre("New Album");
        newOffre.setAcheteurId(102L);
        newOffre.setVendeurId(201L);
        newOffre.setPrixPropose(new BigDecimal("45.00"));
        newOffre.setPrixInitial(new BigDecimal("49.99"));

        // Act
        Offre saved = offreRepository.save(newOffre);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("New Album", saved.getAnnonceTitre());
        assertEquals("PENDING", saved.getStatut());
    }
}
