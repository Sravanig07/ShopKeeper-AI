package com.shelfiq.store.controller;

import com.shelfiq.common.response.ApiResponse;
import com.shelfiq.store.dto.EmployeeInviteDto;
import com.shelfiq.store.dto.EmployeeResponseDto;
import com.shelfiq.store.dto.StoreResponseDto;
import com.shelfiq.store.dto.StoreUpdateDto;
import com.shelfiq.store.service.StoreEmployeeService;
import com.shelfiq.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@Tag(name = "Stores", description = "Store tenant profile, geolocation, and employee management")
public class StoreController {

    private final StoreService storeService;
    private final StoreEmployeeService storeEmployeeService;

    public StoreController(StoreService storeService, StoreEmployeeService storeEmployeeService) {
        this.storeService = storeService;
        this.storeEmployeeService = storeEmployeeService;
    }

    @GetMapping("/current")
    @Operation(summary = "Get current active store profile and location")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getCurrentStore() {
        return ResponseEntity.ok(ApiResponse.ok(storeService.getCurrentStore()));
    }

    @PutMapping("/current")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Update store details and location coordinates")
    public ResponseEntity<ApiResponse<StoreResponseDto>> updateCurrentStore(@Valid @RequestBody StoreUpdateDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Store updated successfully.", storeService.updateCurrentStore(dto)));
    }

    @GetMapping("/employees")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "List employees for current store")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getEmployees() {
        return ResponseEntity.ok(ApiResponse.ok(storeEmployeeService.getEmployees()));
    }

    @PostMapping("/employees")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Enroll new store employee")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> addEmployee(@Valid @RequestBody EmployeeInviteDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Employee added successfully.", storeEmployeeService.addEmployee(dto)));
    }

    @DeleteMapping("/employees/{id}")
    @PreAuthorize("hasRole('STORE_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Deactivate store employee")
    public ResponseEntity<ApiResponse<Void>> deactivateEmployee(@PathVariable Long id) {
        storeEmployeeService.deactivateEmployee(id);
        return ResponseEntity.ok(ApiResponse.ok("Employee deactivated.", null));
    }
}
