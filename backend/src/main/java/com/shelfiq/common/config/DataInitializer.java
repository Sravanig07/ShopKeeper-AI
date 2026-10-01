package com.shelfiq.common.config;

import com.shelfiq.inventory.repository.InventoryMovementRepository;
import com.shelfiq.inventory.repository.InventoryRepository;
import com.shelfiq.product.repository.ProductRepository;
import com.shelfiq.product.service.CategoryService;
import com.shelfiq.purchase.repository.PurchaseOrderRepository;
import com.shelfiq.sales.repository.SaleItemRepository;
import com.shelfiq.sales.repository.SaleTransactionRepository;
import com.shelfiq.store.entity.Store;
import com.shelfiq.store.entity.StoreEmployee;
import com.shelfiq.store.entity.StoreLocation;
import com.shelfiq.store.repository.StoreEmployeeRepository;
import com.shelfiq.store.repository.StoreRepository;
import com.shelfiq.supplier.repository.SupplierRepository;
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
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final SaleItemRepository saleItemRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            StoreRepository storeRepository,
            StoreEmployeeRepository storeEmployeeRepository,
            CategoryService categoryService,
            PasswordEncoder passwordEncoder,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository,
            InventoryMovementRepository inventoryMovementRepository,
            SaleTransactionRepository saleTransactionRepository,
            SaleItemRepository saleItemRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.storeEmployeeRepository = storeEmployeeRepository;
        this.categoryService = categoryService;
        this.passwordEncoder = passwordEncoder;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.saleTransactionRepository = saleTransactionRepository;
        this.saleItemRepository = saleItemRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking ShelfIQ initial setup (zero seed data mode)...");

        // 1. Purge any residual demo/seed data from previous runs
        saleItemRepository.deleteAll();
        saleTransactionRepository.deleteAll();
        purchaseOrderRepository.deleteAll();
        inventoryMovementRepository.deleteAll();
        inventoryRepository.deleteAll();
        productRepository.deleteAll();
        supplierRepository.deleteAll();

        // 2. System Roles
        Role ownerRole = roleRepository.findByName("ROLE_STORE_OWNER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STORE_OWNER")));
        Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_EMPLOYEE")));
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // 3. Master Categories
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
