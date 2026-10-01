package com.shelfiq.auth.controller;

import com.shelfiq.auth.dto.AuthResponse;
import com.shelfiq.auth.dto.LoginRequest;
import com.shelfiq.auth.dto.RegisterRequest;
import com.shelfiq.auth.dto.UserProfileDto;
import com.shelfiq.auth.service.AuthService;
import com.shelfiq.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and tenant registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new Store Owner with initial store and location")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Store and account registered successfully.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and receive JWT with tenant store context")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile and active store")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser() {
        UserProfileDto profile = authService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }
}
