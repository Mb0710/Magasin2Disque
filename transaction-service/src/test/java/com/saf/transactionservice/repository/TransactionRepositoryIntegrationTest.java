package com.saf.transactionservice.repository;

import com.saf.transactionservice.model.Transaction;
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
class TransactionRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction1;
    private Transaction transaction2;

    @BeforeEach
    void setUp() {
        transaction1 = new Transaction();
        transaction1.setAnnonceId(1L);
        transaction1.setAnnonceTitre("Album Test 1");
        transaction1.setAcheteurId(100L);
        transaction1.setAcheteurUsername("acheteur1");
        transaction1.setVendeurId(200L);
        transaction1.setVendeurUsername("vendeur1");
        transaction1.setPrix(new BigDecimal("29.99"));
        transaction1.setStatut("PENDING");
        entityManager.persist(transaction1);

        transaction2 = new Transaction();
        transaction2.setAnnonceId(2L);
        transaction2.setAnnonceTitre("Album Test 2");
        transaction2.setAcheteurId(100L);
        transaction2.setAcheteurUsername("acheteur1");
        transaction2.setVendeurId(300L);
        transaction2.setVendeurUsername("vendeur2");
        transaction2.setPrix(new BigDecimal("39.99"));
        transaction2.setStatut("COMPLETED");
        entityManager.persist(transaction2);

        entityManager.flush();
    }

    @Test
    void testFindByAcheteurId() {
        // Act
        List<Transaction> transactions = transactionRepository.findByAcheteurId(100L);

        // Assert
        assertEquals(2, transactions.size());
    }

    @Test
    void testFindByVendeurId() {
        // Act
        List<Transaction> transactions = transactionRepository.findByVendeurId(200L);

        // Assert
        assertEquals(1, transactions.size());
        assertEquals("Album Test 1", transactions.get(0).getAnnonceTitre());
    }

    @Test
    void testFindByAnnonceId() {
        // Act
        List<Transaction> transactions = transactionRepository.findByAnnonceId(1L);

        // Assert
        assertEquals(1, transactions.size());
        assertEquals(new BigDecimal("29.99"), transactions.get(0).getPrix());
    }

    @Test
    void testSaveTransaction() {
        // Arrange
        Transaction newTransaction = new Transaction();
        newTransaction.setAnnonceId(3L);
        newTransaction.setAnnonceTitre("New Album");
        newTransaction.setAcheteurId(101L);
        newTransaction.setVendeurId(201L);
        newTransaction.setPrix(new BigDecimal("49.99"));

        // Act
        Transaction saved = transactionRepository.save(newTransaction);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("New Album", saved.getAnnonceTitre());
        assertEquals("PENDING", saved.getStatut());
    }
}
