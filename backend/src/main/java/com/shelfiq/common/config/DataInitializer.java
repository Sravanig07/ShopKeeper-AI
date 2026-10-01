package com.shelfiq.common.config;

import com.shelfiq.product.service.CategoryService;
import com.shelfiq.store.entity.Store;
import com.shelfiq.store.entity.StoreEmployee;
import com.shelfiq.store.entity.StoreLocation;
import com.shelfiq.store.repository.StoreEmployeeRepository;
import com.shelfiq.store.repository.StoreRepository;
import com.shelfiq.user.entity.Role;
import com.shelfiq.user.entity.User;
import com.shelfiq.user.repository.RoleRepository;
import com.shelfiq.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final StoreEmployeeRepository storeEmployeeRepository;
    private final CategoryService categoryService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            StoreRepository storeRepository,
            StoreEmployeeRepository storeEmployeeRepository,
            CategoryService categoryService,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.storeEmployeeRepository = storeEmployeeRepository;
        this.categoryService = categoryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking ShelfIQ initial setup...");

        // 1. System Roles
        Role ownerRole = roleRepository.findByName("ROLE_STORE_OWNER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STORE_OWNER")));
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_EMPLOYEE")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // 2. Master Categories
        categoryService.seedDefaultMasterCategories();

        // 3. Initial Store and Account setup if fresh database (0 products, 0 inventory, 0 transactions)
        if (storeRepository.count() == 0) {
            log.info("Initializing primary store and administrator account...");

            // Owner
            User owner = new User("owner@shelfiq.io", passwordEncoder.encode("Password123!"), "Store Owner", "+91 9876543210");
            owner.getRoles().add(ownerRole);
            User savedOwner = userRepository.save(owner);

            // Employee
            User employee = new User("staff@shelfiq.io", passwordEncoder.encode("Password123!"), "Cashier Staff", "+91 9876543211");
            employee.getRoles().add(employeeRole);
            User savedEmployee = userRepository.save(employee);

            // Clean Store
            Store store = new Store("ST-RETAIL01", "My Retail Store", "SUPERMARKET", savedOwner);
            StoreLocation location = new StoreLocation(
                    "100 Feet Road, HAL 2nd Stage, Indiranagar",
                    "Bengaluru",
                    "Karnataka",
                    "560038",
                    new BigDecimal("12.9783690"),
                    new BigDecimal("77.6408290")
            );
            store.setLocation(location);
            Store savedStore = storeRepository.save(store);

            // Assign employee to store
            storeEmployeeRepository.save(new StoreEmployee(savedStore, savedEmployee, "SENIOR_CASHIER"));

            log.info("Ready! Primary store and owner account initialized. Ready for operations.");
        }
    }
}
