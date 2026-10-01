package com.shelfiq.store.service;

import com.shelfiq.common.context.TenantContextHolder;
import com.shelfiq.common.exception.BadRequestException;
import com.shelfiq.common.exception.ResourceNotFoundException;
import com.shelfiq.store.dto.EmployeeInviteDto;
import com.shelfiq.store.dto.EmployeeResponseDto;
import com.shelfiq.store.entity.Store;
import com.shelfiq.store.entity.StoreEmployee;
import com.shelfiq.store.repository.StoreEmployeeRepository;
import com.shelfiq.store.repository.StoreRepository;
import com.shelfiq.user.entity.Role;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.RoleRepository;
import com.shelfiq.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreEmployeeService {

    private final StoreRepository storeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreEmployeeService(
            StoreRepository storeRepository,
            StoreEmployeeRepository storeEmployeeRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.storeEmployeeRepository = storeEmployeeRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponseDto> getEmployees() {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        return storeEmployeeRepository.findByStoreId(storeId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeResponseDto addEmployee(EmployeeInviteDto dto) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store", "id", storeId));

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("A user with email " + dto.getEmail() + " already exists.");
        }

        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_EMPLOYEE")));

        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getFullName(),
                dto.getPhone()
        );
        user.getRoles().add(employeeRole);
        User savedUser = userRepository.save(user);

        StoreEmployee storeEmployee = new StoreEmployee(store, savedUser, dto.getDesignation());
        StoreEmployee saved = storeEmployeeRepository.save(storeEmployee);

        return toDto(saved);
    }

    @Transactional
    public void deactivateEmployee(Long employeeId) {
        Long storeId = TenantContextHolder.getRequiredStoreId();
        StoreEmployee employee = storeEmployeeRepository.findById(employeeId)
                .filter(e -> e.getStore().getId().equals(storeId))
                .orElseThrow(() -> new ResourceNotFoundException("StoreEmployee", "id", employeeId));

        employee.setActive(false);
        storeEmployeeRepository.save(employee);
    }

    private EmployeeResponseDto toDto(StoreEmployee se) {
        User u = se.getUser();
        return new EmployeeResponseDto(
                se.getId(),
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getPhone(),
                se.getDesignation(),
                se.isActive(),
                se.getCreatedAt()
        );
    }
}
