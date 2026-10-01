package com.shelfiq.auth.service;

import com.shelfiq.auth.dto.AuthResponse;
import com.shelfiq.auth.dto.LoginRequest;
import com.shelfiq.auth.dto.RegisterRequest;
import com.shelfiq.auth.dto.UserProfileDto;
import com.shelfiq.auth.jwt.JwtTokenProvider;
import com.shelfiq.common.context.TenantContext;
import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.BadRequestException;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.store.entity.Store;
import com.shelfiq.store.entity.StoreEmployee;
import com.shelfiq.store.entity.StoreLocation;
import com.shelfiq.store.repository.StoreEmployeeRepository;
import com.shelfiq.store.repository.StoreRepository;
import com.shelfiq.user.entity.Role;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.RoleRepository;
import com.shelfiq.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            StoreRepository storeRepository,
            StoreEmployeeRepository storeEmployeeRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.storeRepository = storeRepository;
        this.storeEmployeeRepository = storeEmployeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with email " + request.getEmail() + " already exists.");
        }

        Role ownerRole = roleRepository.findByName("ROLE_STORE_OWNER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STORE_OWNER")));
        // ensure other roles exist
        roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_EMPLOYEE")));
        roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhone()
        );
        user.getRoles().add(ownerRole);
        User savedUser = userRepository.save(user);

        // Generate clean store code
        String storeCode = "ST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Store store = new Store(storeCode, request.getStoreName(), request.getBusinessType(), savedUser);

        StoreLocation location = new StoreLocation(
                request.getAddressLine1(),
                request.getCity(),
                request.getState(),
                request.getPostalCode(),
                request.getLatitude(),
                request.getLongitude()
        );
        store.setLocation(location);
        Store savedStore = storeRepository.save(store);

        Set<String> roleNames = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = tokenProvider.generateToken(savedUser.getId(), savedUser.getEmail(), savedStore.getId(), roleNames);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedStore.getId(),
                savedStore.getStoreCode(),
                savedStore.getName(),
                roleNames
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        // Determine active store
        Long storeId = null;
        String storeCode = "";
        String storeName = "";

        List<Store> ownedStores = storeRepository.findByOwnerId(user.getId());
        if (!ownedStores.isEmpty()) {
            Store primaryStore = ownedStores.getFirst();
            storeId = primaryStore.getId();
            storeCode = primaryStore.getStoreCode();
            storeName = primaryStore.getName();
        } else {
            List<StoreEmployee> employeeRecords = storeEmployeeRepository.findByUserId(user.getId());
            if (!employeeRecords.isEmpty()) {
                Store employedStore = employeeRecords.getFirst().getStore();
                storeId = employedStore.getId();
                storeCode = employedStore.getStoreCode();
                storeName = employedStore.getName();
            }
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), storeId, roleNames);

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                storeId,
                storeCode,
                storeName,
                roleNames
        );
    }

    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile() {
        TenantContext ctx = TenantContextHolder.getContext();
        if (ctx == null || ctx.getUserId() == null) {
            throw new BadRequestException("No active user session found.");
        }

        User user = userRepository.findById(ctx.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ctx.getUserId()));

        String storeName = "";
        if (ctx.getStoreId() != null) {
            storeName = storeRepository.findById(ctx.getStoreId())
                    .map(Store::getName)
                    .orElse("");
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                roles,
                ctx.getStoreId(),
                storeName
        );
    }
}
