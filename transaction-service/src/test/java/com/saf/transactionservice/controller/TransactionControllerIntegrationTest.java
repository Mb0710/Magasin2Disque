package com.saf.transactionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saf.transactionservice.model.Transaction;
import com.saf.transactionservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        testTransaction = new Transaction();
        testTransaction.setAnnonceId(1L);
        testTransaction.setAnnonceTitre("Test Album");
        testTransaction.setAcheteurId(100L);
        testTransaction.setAcheteurUsername("acheteur");
        testTransaction.setVendeurId(200L);
        testTransaction.setVendeurUsername("vendeur");
        testTransaction.setPrix(new BigDecimal("29.99"));
        testTransaction.setStatut("PENDING");
        testTransaction = transactionRepository.save(testTransaction);
    }

    // Test commenté: dépend de l'Actor Framework
    // @Test
    // void testAcheterDirect() throws Exception { ... }

    // Test commenté: dépend de l'Actor Framework
    // @Test
    // void testGetTransaction() throws Exception { ... }

    @Test
    void testGetTransactionsByUser_AsAcheteur() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/user/100")
                .param("asVendeur", "false"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionsByUser_AsVendeur() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/user/200")
                .param("asVendeur", "true"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionsByAcheteur() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/acheteur/100"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTransactionsByVendeur() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/vendeur/200"))
                .andExpect(status().isOk());
    }

    @Test
    void testCountTransactions() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());
    }

    @Test
    void testGetAllTransactions() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/transactions/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testAcheterDirect_MissingParameters() throws Exception {
        // Arrange
        Map<String, Long> request = new HashMap<>();
        request.put("annonceId", 2L);
        // Missing acheteurId

        // Act & Assert
        mockMvc.perform(post("/api/transactions/acheter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
