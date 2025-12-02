package com.saf.transactionservice;

import com.saf.transactionservice.model.Transaction;
import com.saf.transactionservice.model.Offre;
import com.saf.transactionservice.repository.TransactionRepository;
import com.saf.transactionservice.repository.OffreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TransactionServiceApplicationIntegrationTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OffreRepository offreRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        offreRepository.deleteAll();
    }

    @Test
    void testCompleteTransactionFlow() {
        // 1. Créer une transaction
        Transaction transaction = new Transaction();
        transaction.setAnnonceId(1L);
        transaction.setAnnonceTitre("Abbey Road");
        transaction.setAcheteurId(100L);
        transaction.setAcheteurUsername("buyer");
        transaction.setVendeurId(200L);
        transaction.setVendeurUsername("seller");
        transaction.setPrix(new BigDecimal("39.99"));
        transaction.setStatut("PENDING");
        transaction.setTypeAchat("DIRECT");

        transaction = transactionRepository.save(transaction);

        assertNotNull(transaction.getId());
        assertEquals("PENDING", transaction.getStatut());
        assertEquals("DIRECT", transaction.getTypeAchat());

        // 2. Compléter la transaction
        transaction.setStatut("COMPLETED");
        transaction.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        Transaction completedTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        assertEquals("COMPLETED", completedTransaction.getStatut());
        assertNotNull(completedTransaction.getCompletedAt());
    }

    @Test
    void testCompleteOffreFlow() {
        // 1. Créer une offre
        Offre offre = new Offre();
        offre.setAnnonceId(1L);
        offre.setAnnonceTitre("The Wall");
        offre.setAcheteurId(100L);
        offre.setAcheteurUsername("buyer");
        offre.setAcheteurEmail("buyer@example.com");
        offre.setVendeurId(200L);
        offre.setVendeurUsername("seller");
        offre.setVendeurEmail("seller@example.com");
        offre.setPrixPropose(new BigDecimal("35.00"));
        offre.setPrixInitial(new BigDecimal("39.99"));
        offre.setMessage("Je propose 35€");
        offre.setStatut("PENDING");

        offre = offreRepository.save(offre);

        assertNotNull(offre.getId());
        assertEquals("PENDING", offre.getStatut());

        // 2. Accepter l'offre
        offre.setStatut("ACCEPTED");
        offre.setRespondedAt(LocalDateTime.now());
        offreRepository.save(offre);

        // 3. Créer une transaction depuis l'offre acceptée
        Transaction transaction = new Transaction();
        transaction.setAnnonceId(offre.getAnnonceId());
        transaction.setAnnonceTitre(offre.getAnnonceTitre());
        transaction.setAcheteurId(offre.getAcheteurId());
        transaction.setAcheteurUsername(offre.getAcheteurUsername());
        transaction.setVendeurId(offre.getVendeurId());
        transaction.setVendeurUsername(offre.getVendeurUsername());
        transaction.setPrix(offre.getPrixPropose());
        transaction.setStatut("PENDING");
        transaction.setTypeAchat("OFFRE_ACCEPTEE");
        transactionRepository.save(transaction);

        // Vérifications
        Offre acceptedOffre = offreRepository.findById(offre.getId()).orElseThrow();
        assertEquals("ACCEPTED", acceptedOffre.getStatut());
        assertNotNull(acceptedOffre.getRespondedAt());

        assertEquals(1, transactionRepository.count());
        Transaction createdTransaction = transactionRepository.findAll().get(0);
        assertEquals("OFFRE_ACCEPTEE", createdTransaction.getTypeAchat());
        assertEquals(new BigDecimal("35.00"), createdTransaction.getPrix());
    }

    @Test
    void testMultipleTransactionsForUser() {
        // Créer plusieurs transactions pour un acheteur
        Transaction t1 = new Transaction();
        t1.setAnnonceId(1L);
        t1.setAnnonceTitre("Album 1");
        t1.setAcheteurId(100L);
        t1.setVendeurId(200L);
        t1.setPrix(new BigDecimal("29.99"));
        transactionRepository.save(t1);

        Transaction t2 = new Transaction();
        t2.setAnnonceId(2L);
        t2.setAnnonceTitre("Album 2");
        t2.setAcheteurId(100L);
        t2.setVendeurId(201L);
        t2.setPrix(new BigDecimal("39.99"));
        transactionRepository.save(t2);

        Transaction t3 = new Transaction();
        t3.setAnnonceId(3L);
        t3.setAnnonceTitre("Album 3");
        t3.setAcheteurId(101L);
        t3.setVendeurId(200L);
        t3.setPrix(new BigDecimal("19.99"));
        transactionRepository.save(t3);

        // Vérifications
        List<Transaction> acheteur100Transactions = transactionRepository.findByAcheteurId(100L);
        assertEquals(2, acheteur100Transactions.size());

        List<Transaction> vendeur200Transactions = transactionRepository.findByVendeurId(200L);
        assertEquals(2, vendeur200Transactions.size());
    }

    @Test
    void testMultipleOffresForAnnonce() {
        // Créer plusieurs offres pour une même annonce
        Offre o1 = new Offre();
        o1.setAnnonceId(1L);
        o1.setAnnonceTitre("Popular Album");
        o1.setAcheteurId(100L);
        o1.setVendeurId(200L);
        o1.setPrixPropose(new BigDecimal("35.00"));
        o1.setPrixInitial(new BigDecimal("39.99"));
        o1.setStatut("PENDING");
        offreRepository.save(o1);

        Offre o2 = new Offre();
        o2.setAnnonceId(1L);
        o2.setAnnonceTitre("Popular Album");
        o2.setAcheteurId(101L);
        o2.setVendeurId(200L);
        o2.setPrixPropose(new BigDecimal("37.00"));
        o2.setPrixInitial(new BigDecimal("39.99"));
        o2.setStatut("PENDING");
        offreRepository.save(o2);

        Offre o3 = new Offre();
        o3.setAnnonceId(1L);
        o3.setAnnonceTitre("Popular Album");
        o3.setAcheteurId(102L);
        o3.setVendeurId(200L);
        o3.setPrixPropose(new BigDecimal("38.00"));
        o3.setPrixInitial(new BigDecimal("39.99"));
        o3.setStatut("REFUSED");
        offreRepository.save(o3);

        // Vérifications
        List<Offre> annonce1Offres = offreRepository.findByAnnonceId(1L);
        assertEquals(3, annonce1Offres.size());

        List<Offre> pendingOffres = offreRepository.findByVendeurIdAndStatut(200L, "PENDING");
        assertEquals(2, pendingOffres.size());
    }

    @Test
    void testOffreRefusal() {
        Offre offre = new Offre();
        offre.setAnnonceId(1L);
        offre.setAnnonceTitre("Test Album");
        offre.setAcheteurId(100L);
        offre.setVendeurId(200L);
        offre.setPrixPropose(new BigDecimal("25.00"));
        offre.setPrixInitial(new BigDecimal("39.99"));
        offre.setStatut("PENDING");

        offre = offreRepository.save(offre);

        // Refuser l'offre
        offre.setStatut("REFUSED");
        offre.setRespondedAt(LocalDateTime.now());
        offreRepository.save(offre);

        Offre refusedOffre = offreRepository.findById(offre.getId()).orElseThrow();
        assertEquals("REFUSED", refusedOffre.getStatut());
        assertNotNull(refusedOffre.getRespondedAt());
    }

    @Test
    void testGetOffresForAcheteur() {
        Offre o1 = new Offre();
        o1.setAnnonceId(1L);
        o1.setAcheteurId(100L);
        o1.setVendeurId(200L);
        o1.setPrixPropose(new BigDecimal("30.00"));
        o1.setStatut("PENDING");
        offreRepository.save(o1);

        Offre o2 = new Offre();
        o2.setAnnonceId(2L);
        o2.setAcheteurId(100L);
        o2.setVendeurId(201L);
        o2.setPrixPropose(new BigDecimal("40.00"));
        o2.setStatut("ACCEPTED");
        offreRepository.save(o2);

        Offre o3 = new Offre();
        o3.setAnnonceId(3L);
        o3.setAcheteurId(101L);
        o3.setVendeurId(200L);
        o3.setPrixPropose(new BigDecimal("25.00"));
        o3.setStatut("PENDING");
        offreRepository.save(o3);

        List<Offre> acheteur100Offres = offreRepository.findByAcheteurId(100L);
        assertEquals(2, acheteur100Offres.size());
    }
}
