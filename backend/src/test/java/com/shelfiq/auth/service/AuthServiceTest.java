package com.shelfiq.auth.service;

import com.shelfiq.auth.dto.AuthResponse;
import com.shelfiq.auth.dto.LoginRequest;
import com.shelfiq.auth.dto.RegisterRequest;
import com.shelfiq.auth.jwt.JwtTokenProvider;
import com.shelfiq.common.exception.BadRequestException;
import com.shelfiq.store.entity.Store;
import com.shelfiq.store.repository.StoreEmployeeRepository;
import com.shelfiq.store.repository.StoreRepository;
import com.shelfiq.user.entity.Role;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.RoleRepository;
import com.shelfiq.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreEmployeeRepository storeEmployeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                roleRepository,
                storeRepository,
                storeEmployeeRepository,
                passwordEncoder,
                tokenProvider
        );
    }

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@shelfiq.io");
        req.setPassword("Password123!");
        req.setFullName("Test User");
        req.setStoreName("Test Store");
        req.setBusinessType("GROCERY");
        req.setAddressLine1("123 Main St");
        req.setCity("Metropolis");
        req.setState("NY");
        req.setPostalCode("10001");
        req.setLatitude(new BigDecimal("40.7128"));
        req.setLongitude(new BigDecimal("-74.0060"));

        when(userRepository.existsByEmail("test@shelfiq.io")).thenReturn(false);
        when(roleRepository.findByName("ROLE_STORE_OWNER")).thenReturn(Optional.of(new Role("ROLE_STORE_OWNER")));
        when(roleRepository.findByName("ROLE_EMPLOYEE")).thenReturn(Optional.of(new Role("ROLE_EMPLOYEE")));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(new Role("ROLE_ADMIN")));
        when(passwordEncoder.encode(any())).thenReturn("hashedPass");

        User savedUser = new User("test@shelfiq.io", "hashedPass", "Test User", null);
        savedUser.setId(10L);
        savedUser.setRoles(Set.of(new Role("ROLE_STORE_OWNER")));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Store savedStore = new Store("ST-12345", "Test Store", "GROCERY", savedUser);
        savedStore.setId(20L);
        when(storeRepository.save(any(Store.class))).thenReturn(savedStore);

        when(tokenProvider.generateToken(eq(10L), eq("test@shelfiq.io"), eq(20L), any())).thenReturn("mockJwtToken");

        AuthResponse resp = authService.register(req);

        assertNotNull(resp);
        assertEquals("test@shelfiq.io", resp.getEmail());
        assertEquals("mockJwtToken", resp.getToken());
        assertEquals(20L, resp.getStoreId());
    }

    @Test
    void register_ExistingEmail_ThrowsBadRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("existing@shelfiq.io");
        when(userRepository.existsByEmail("existing@shelfiq.io")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(req));
    }

    @Test
    void login_InvalidPassword_ThrowsBadCredentials() {
        LoginRequest req = new LoginRequest("user@shelfiq.io", "wrongPass");
        User user = new User("user@shelfiq.io", "hashedPass", "User", null);
        when(userRepository.findByEmail("user@shelfiq.io")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "hashedPass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(req));
    }
}
