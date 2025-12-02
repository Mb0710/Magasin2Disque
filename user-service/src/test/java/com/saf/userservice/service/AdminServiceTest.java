package com.saf.userservice.service;

import com.saf.userservice.dto.*;
import com.saf.userservice.model.AdminAction;
import com.saf.userservice.model.Annonce;
import com.saf.userservice.model.User;
import com.saf.userservice.repository.AdminActionRepository;
import com.saf.userservice.repository.AnnonceRepository;
import com.saf.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnnonceRepository annonceRepository;

    @Mock
    private AdminActionRepository adminActionRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private Annonce testAnnonce;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password");
        testUser.setId(1L);
        testUser.setRole("USER");
        testUser.setEnabled(true);
        testUser.setBanned(false);

        testAnnonce = new Annonce();
        testAnnonce.setId(1L);
        testAnnonce.setTitre("Test Album");
        testAnnonce.setArtiste("Test Artist");
        testAnnonce.setPrix(new BigDecimal("29.99"));
        testAnnonce.setVendeurId(1L);

        // Setup Security Context
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetStatistics() {
        // Arrange
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByEnabledTrue()).thenReturn(90L);
        when(userRepository.countByIsBannedTrue()).thenReturn(5L);
        when(annonceRepository.count()).thenReturn(200L);
        when(annonceRepository.countByDisponibleTrue()).thenReturn(180L);
        when(adminActionRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(any(), any()))
                .thenReturn(List.of());
        
        ResponseEntity<Long> responseEntity = ResponseEntity.ok(50L);
        when(restTemplate.getForEntity(anyString(), eq(Long.class)))
                .thenReturn(responseEntity);

        // Act
        AdminStatsDTO stats = adminService.getStatistics();

        // Assert
        assertNotNull(stats);
        assertEquals(100L, stats.getTotalUsers());
        assertEquals(90L, stats.getActiveUsers());
        assertEquals(5L, stats.getBannedUsers());
        assertEquals(200L, stats.getTotalAnnonces());
        assertEquals(180L, stats.getActiveAnnonces());
        assertEquals(50L, stats.getTotalTransactions());
    }

    @Test
    void testBanUser_Success() {
        // Arrange
        BanUserRequest request = new BanUserRequest();
        request.setReason("Violation des règles");
        request.setDuration("permanent");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(adminActionRepository.save(any(AdminAction.class))).thenReturn(new AdminAction());

        // Act
        Map<String, Object> result = adminService.banUser(1L, request);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("Utilisateur banni avec succès", result.get("message"));
        assertTrue(testUser.isBanned());
        assertFalse(testUser.isEnabled());
        assertEquals("Violation des règles", testUser.getBannedReason());
        verify(userRepository).save(testUser);
        verify(adminActionRepository).save(any(AdminAction.class));
    }

    @Test
    void testBanUser_AlreadyBanned() {
        // Arrange
        testUser.setBanned(true);
        BanUserRequest request = new BanUserRequest();
        request.setReason("Test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminService.banUser(1L, request));
        assertEquals("Cet utilisateur est déjà banni", exception.getMessage());
    }

    @Test
    void testBanUser_UserNotFound() {
        // Arrange
        BanUserRequest request = new BanUserRequest();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminService.banUser(999L, request));
        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    void testUnbanUser_Success() {
        // Arrange
        testUser.setBanned(true);
        testUser.setBannedReason("Previous ban");
        testUser.setEnabled(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(adminActionRepository.save(any(AdminAction.class))).thenReturn(new AdminAction());

        // Act
        Map<String, Object> result = adminService.unbanUser(1L);

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("Utilisateur débanni avec succès", result.get("message"));
        assertFalse(testUser.isBanned());
        assertTrue(testUser.isEnabled());
        assertNull(testUser.getBannedReason());
        verify(userRepository).save(testUser);
    }

    @Test
    void testUnbanUser_NotBanned() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminService.unbanUser(1L));
        assertEquals("Cet utilisateur n'est pas banni", exception.getMessage());
    }

    @Test
    void testDeleteAnnonce_Success() {
        // Arrange
        when(annonceRepository.findById(1L)).thenReturn(Optional.of(testAnnonce));
        when(adminActionRepository.save(any(AdminAction.class))).thenReturn(new AdminAction());

        // Act
        Map<String, Object> result = adminService.deleteAnnonce(1L, "Contenu inapproprié");

        // Assert
        assertTrue((Boolean) result.get("success"));
        assertEquals("Annonce supprimée avec succès", result.get("message"));
        verify(annonceRepository).delete(testAnnonce);
        verify(adminActionRepository).save(any(AdminAction.class));
    }

    @Test
    void testDeleteAnnonce_NotFound() {
        // Arrange
        when(annonceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> adminService.deleteAnnonce(999L, "Test"));
        assertEquals("Annonce non trouvée", exception.getMessage());
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findAll()).thenReturn(users);
        when(annonceRepository.countByVendeurId(1L)).thenReturn(5);

        // Act
        List<UserDetailsDTO> result = adminService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        assertEquals(5, result.get(0).getTotalAnnonces());
    }

    @Test
    void testGetBannedUsers() {
        // Arrange
        testUser.setBanned(true);
        List<User> bannedUsers = List.of(testUser);
        when(userRepository.findByIsBannedTrue()).thenReturn(bannedUsers);
        when(annonceRepository.countByVendeurId(1L)).thenReturn(3);

        // Act
        List<UserDetailsDTO> result = adminService.getBannedUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isBanned());
    }

    @Test
    void testGetAllAnnonces() {
        // Arrange
        List<Annonce> annonces = List.of(testAnnonce);
        when(annonceRepository.findAll()).thenReturn(annonces);

        // Act
        List<Annonce> result = adminService.getAllAnnonces();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Album", result.get(0).getTitre());
    }

    @Test
    void testGetUserDetails() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(annonceRepository.countByVendeurId(1L)).thenReturn(7);

        // Act
        UserDetailsDTO result = adminService.getUserDetails(1L);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(7, result.getTotalAnnonces());
    }

    @Test
    void testSearchUsers() {
        // Arrange
        List<User> users = List.of(testUser);
        when(userRepository.findByUsernameContainingOrEmailContaining("test", "test"))
                .thenReturn(users);
        when(annonceRepository.countByVendeurId(1L)).thenReturn(2);

        // Act
        List<UserDetailsDTO> result = adminService.searchUsers("test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
    }

    @Test
    void testGetAllAdminActions() {
        // Arrange
        AdminAction action = new AdminAction("admin", "BAN_USER", "USER", 1L, "testuser", "Test");
        when(adminActionRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(action));

        // Act
        List<AdminActionDTO> result = adminService.getAllAdminActions();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BAN_USER", result.get(0).getActionType());
        assertEquals("admin", result.get(0).getAdminUsername());
    }

    @Test
    void testGetAdminActionsByType() {
        // Arrange
        AdminAction action = new AdminAction("admin", "BAN_USER", "USER", 1L, "testuser", "Test");
        when(adminActionRepository.findByActionTypeOrderByCreatedAtDesc("BAN_USER"))
                .thenReturn(List.of(action));

        // Act
        List<AdminActionDTO> result = adminService.getAdminActionsByType("BAN_USER");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BAN_USER", result.get(0).getActionType());
    }

    @Test
    void testGetAdminActionsForTarget() {
        // Arrange
        AdminAction action = new AdminAction("admin", "BAN_USER", "USER", 1L, "testuser", "Test");
        when(adminActionRepository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc("USER", 1L))
                .thenReturn(List.of(action));

        // Act
        List<AdminActionDTO> result = adminService.getAdminActionsForTarget("USER", 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTargetId());
        assertEquals("USER", result.get(0).getTargetType());
    }
}
