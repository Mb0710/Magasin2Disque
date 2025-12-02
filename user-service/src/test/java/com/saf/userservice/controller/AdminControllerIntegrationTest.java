package com.saf.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saf.userservice.dto.*;
import com.saf.userservice.model.User;
import com.saf.userservice.service.AdminService;
import com.saf.userservice.config.TestSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    private AdminStatsDTO testStats;
    private UserDetailsDTO testUserDetails;

    @BeforeEach
    void setUp() {
        testStats = new AdminStatsDTO(100L, 90L, 5L, 200L, 180L, 50L, 0L, 3L);

        testUserDetails = new UserDetailsDTO();
        testUserDetails.setId(1L);
        testUserDetails.setUsername("testuser");
        testUserDetails.setEmail("test@example.com");
        testUserDetails.setRole("USER");
        testUserDetails.setEnabled(true);
        testUserDetails.setBanned(false);
        testUserDetails.setTotalAnnonces(5);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetStatistics() throws Exception {
        // Arrange
        when(adminService.getStatistics()).thenReturn(testStats);

        // Act & Assert
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.activeUsers").value(90))
                .andExpect(jsonPath("$.bannedUsers").value(5))
                .andExpect(jsonPath("$.totalAnnonces").value(200));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers() throws Exception {
        // Arrange
        when(adminService.getAllUsers()).thenReturn(List.of(testUserDetails));

        // Act & Assert
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testBanUser() throws Exception {
        // Arrange
        BanUserRequest request = new BanUserRequest();
        request.setReason("Violation des règles");
        request.setDuration("permanent");

        when(adminService.banUser(eq(1L), any(BanUserRequest.class)))
                .thenReturn(Map.of("success", true, "message", "Utilisateur banni avec succès"));

        // Act & Assert
        mockMvc.perform(post("/api/admin/users/1/ban")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Utilisateur banni avec succès"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUnbanUser() throws Exception {
        // Arrange
        when(adminService.unbanUser(1L))
                .thenReturn(Map.of("success", true, "message", "Utilisateur débanni avec succès"));

        // Act & Assert
        mockMvc.perform(post("/api/admin/users/1/unban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Utilisateur débanni avec succès"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetBannedUsers() throws Exception {
        // Arrange
        testUserDetails.setBanned(true);
        testUserDetails.setBannedReason("Violation");
        when(adminService.getBannedUsers()).thenReturn(List.of(testUserDetails));

        // Act & Assert
        mockMvc.perform(get("/api/admin/users/banned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].banned").value(true))
                .andExpect(jsonPath("$[0].bannedReason").value("Violation"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSearchUsers() throws Exception {
        // Arrange
        when(adminService.searchUsers(anyString())).thenReturn(List.of(testUserDetails));

        // Act & Assert
        mockMvc.perform(get("/api/admin/users/search")
                .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserDetails() throws Exception {
        // Arrange
        when(adminService.getUserDetails(1L)).thenReturn(testUserDetails);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.totalAnnonces").value(5));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteAnnonce() throws Exception {
        // Arrange
        when(adminService.deleteAnnonce(eq(1L), anyString()))
                .thenReturn(Map.of("success", true, "message", "Annonce supprimée avec succès"));

        // Act & Assert
        mockMvc.perform(delete("/api/admin/annonces/1")
                .param("reason", "Contenu inapproprié"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAdminActions() throws Exception {
        // Arrange
        AdminActionDTO action = new AdminActionDTO(
            1L, "admin", "BAN_USER", "USER", 1L, "testuser", "Test", LocalDateTime.now(), null);
        when(adminService.getAllAdminActions()).thenReturn(List.of(action));

        // Act & Assert
        mockMvc.perform(get("/api/admin/actions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actionType").value("BAN_USER"))
                .andExpect(jsonPath("$[0].adminUsername").value("admin"));
    }

    // Note: Les tests de sécurité sont désactivés car TestSecurityConfig permet tout
    // Pour tester réellement la sécurité, utiliser une config Spring Security complète
}
