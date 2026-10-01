-- ==============================================================================
-- ShelfIQ / ShopKeeper AI - Database Schema Definition
-- Target Engine: MySQL 8.0+ / MariaDB / Cloud Relational Databases
-- Character Set: utf8mb4 / Unicode
-- Architecture: Multi-Tenant Retail Inventory Intelligence & Automated POS Engine
-- ==============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------------------------
-- 1. ROLES & PERMISSIONS
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 2. USERS
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `email` VARCHAR(150) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 3. USER ROLES (Join Table)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_roles` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`, `role_id`),
    CONSTRAINT `fk_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 4. STORES (Multi-Tenancy Anchor)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `stores` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_code` VARCHAR(50) NOT NULL UNIQUE,
    `name` VARCHAR(150) NOT NULL,
    `business_type` VARCHAR(50) NOT NULL,
    `currency` VARCHAR(10) NOT NULL DEFAULT 'INR',
    `timezone` VARCHAR(50) NOT NULL DEFAULT 'Asia/Kolkata',
    `owner_id` BIGINT NOT NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_stores_owner` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT,
    INDEX `idx_stores_code` (`store_code`),
    INDEX `idx_stores_owner` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 5. STORE LOCATIONS (Geospatial & Geohash Clustering)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `store_locations` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL UNIQUE,
    `address_line1` VARCHAR(255) NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `postal_code` VARCHAR(20) NOT NULL,
    `latitude` DECIMAL(10, 7) NOT NULL,
    `longitude` DECIMAL(10, 7) NOT NULL,
    `geohash_5` VARCHAR(5) NOT NULL,
    `geohash_6` VARCHAR(6) NOT NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_store_locations_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    INDEX `idx_store_locations_geohash5` (`geohash_5`),
    INDEX `idx_store_locations_geohash6` (`geohash_6`),
    INDEX `idx_store_locations_city` (`city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 6. STORE EMPLOYEES (Store Staff & Cashier Assignment)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `store_employees` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `designation` VARCHAR(50) DEFAULT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_store_employees_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_store_employees_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    INDEX `idx_store_emp_store` (`store_id`),
    INDEX `idx_store_emp_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 7. SUPPLIERS & VENDORS
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `suppliers` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `name` VARCHAR(150) NOT NULL,
    `contact_person` VARCHAR(100) DEFAULT NULL,
    `email` VARCHAR(150) DEFAULT NULL,
    `phone` VARCHAR(50) DEFAULT NULL,
    `lead_time_days` INT NOT NULL DEFAULT 3,
    `reliability_score` DECIMAL(3, 2) DEFAULT 0.95,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_suppliers_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    INDEX `idx_suppliers_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 8. PRODUCT CATEGORIES (Hierarchical Master & Store-Specific)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `categories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT DEFAULT NULL, -- NULL indicates global platform category
    `name` VARCHAR(100) NOT NULL,
    `description` TEXT DEFAULT NULL,
    `parent_id` BIGINT DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_categories_parent` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
    INDEX `idx_categories_store_id` (`store_id`),
    INDEX `idx_categories_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 9. PRODUCTS CATALOG (SKU, Barcode, Pricing & AI Thresholds)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `products` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `sku` VARCHAR(64) NOT NULL,
    `barcode` VARCHAR(64) DEFAULT NULL,
    `name` VARCHAR(200) NOT NULL,
    `brand` VARCHAR(100) DEFAULT NULL,
    `category_id` BIGINT DEFAULT NULL,
    `description` TEXT DEFAULT NULL,
    `selling_price` DECIMAL(12, 2) NOT NULL,
    `cost_price` DECIMAL(12, 2) NOT NULL,
    `min_stock` INT NOT NULL DEFAULT 5,
    `safety_stock` INT NOT NULL DEFAULT 10,
    `supplier_id` BIGINT DEFAULT NULL,
    `lead_time_days` INT DEFAULT 3,
    `unit_of_measure` VARCHAR(20) DEFAULT 'PCS',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `uk_store_sku` UNIQUE (`store_id`, `sku`),
    CONSTRAINT `uk_store_barcode` UNIQUE (`store_id`, `barcode`),
    CONSTRAINT `fk_products_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_products_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE SET NULL,
    INDEX `idx_products_store_id` (`store_id`),
    INDEX `idx_products_sku` (`sku`),
    INDEX `idx_products_barcode` (`barcode`),
    INDEX `idx_products_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 10. INVENTORIES (Real-Time Stock Levels & Optimistic Locking)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventories` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL UNIQUE,
    `current_stock` INT NOT NULL DEFAULT 0,
    `incoming_stock` INT NOT NULL DEFAULT 0,
    `reserved_stock` INT NOT NULL DEFAULT 0,
    `last_restocked_at` DATETIME(6) DEFAULT NULL,
    `version` BIGINT NOT NULL DEFAULT 0,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_inventories_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_inventories_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    INDEX `idx_inventories_store_id` (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 11. INVENTORY MOVEMENTS (Immutable Double-Entry Ledger)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `inventory_movements` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `movement_type` VARCHAR(30) NOT NULL, -- SALE, RESTOCK, RETURN, DAMAGE, ADJUSTMENT
    `reference_type` VARCHAR(30) DEFAULT NULL, -- SALE_TRANSACTION, PURCHASE_ORDER, MANUAL_AUDIT, CSV_IMPORT
    `reference_id` VARCHAR(100) DEFAULT NULL,
    `quantity_delta` INT NOT NULL,
    `previous_stock` INT NOT NULL,
    `new_stock` INT NOT NULL,
    `reason` VARCHAR(255) DEFAULT NULL,
    `created_by` BIGINT DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_inv_movements_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_inv_movements_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_inv_movements_user` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_inv_movements_store_id` (`store_id`),
    INDEX `idx_inv_movements_product_id` (`product_id`),
    INDEX `idx_inv_movements_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 12. SALE TRANSACTIONS (POS Header & Billing Invoices)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sale_transactions` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `invoice_number` VARCHAR(64) NOT NULL UNIQUE,
    `cashier_id` BIGINT DEFAULT NULL,
    `total_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `discount_amount` DECIMAL(12, 2) DEFAULT 0.00,
    `tax_amount` DECIMAL(12, 2) DEFAULT 0.00,
    `net_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `payment_method` VARCHAR(20) NOT NULL, -- CASH, UPI, CARD
    `payment_reference` VARCHAR(100) DEFAULT NULL,
    `customer_name` VARCHAR(100) DEFAULT NULL,
    `customer_phone` VARCHAR(30) DEFAULT NULL,
    `status` VARCHAR(20) NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, REFUNDED
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_sales_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sales_cashier` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_sales_store_id` (`store_id`),
    INDEX `idx_sales_invoice` (`invoice_number`),
    INDEX `idx_sales_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 13. SALE ITEMS (Transaction Line Items)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sale_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `sale_transaction_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(12, 2) NOT NULL,
    `subtotal` DECIMAL(12, 2) NOT NULL,
    `discount_amount` DECIMAL(12, 2) DEFAULT 0.00,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_sale_items_txn` FOREIGN KEY (`sale_transaction_id`) REFERENCES `sale_transactions` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_sale_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
    INDEX `idx_sale_items_txn_id` (`sale_transaction_id`),
    INDEX `idx_sale_items_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 14. PURCHASE ORDERS (Replenishment & Distributor Orders)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `purchase_orders` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `store_id` BIGINT NOT NULL,
    `po_number` VARCHAR(64) NOT NULL UNIQUE,
    `supplier_id` BIGINT NOT NULL,
    `status` VARCHAR(30) NOT NULL DEFAULT 'DRAFT', -- DRAFT, ORDERED, RECEIVED, CANCELLED
    `total_cost` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `notes` TEXT DEFAULT NULL,
    `expected_delivery_date` DATE DEFAULT NULL,
    `received_at` DATETIME(6) DEFAULT NULL,
    `received_by` BIGINT DEFAULT NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_po_store` FOREIGN KEY (`store_id`) REFERENCES `stores` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_po_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE RESTRICT,
    CONSTRAINT `fk_po_received_by` FOREIGN KEY (`received_by`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    INDEX `idx_po_store_id` (`store_id`),
    INDEX `idx_po_number` (`po_number`),
    INDEX `idx_po_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 15. PURCHASE ORDER ITEMS (Replenishment Line Items)
-- ------------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `purchase_order_items` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `purchase_order_id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `quantity` INT NOT NULL,
    `unit_cost` DECIMAL(12, 2) NOT NULL,
    `subtotal` DECIMAL(12, 2) NOT NULL,
    `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    `updated_at` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT `fk_poi_po` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_poi_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE RESTRICT,
    INDEX `idx_poi_po_id` (`purchase_order_id`),
    INDEX `idx_poi_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------------------------
-- 16. CORE SEED DATA (System Roles)
-- ------------------------------------------------------------------------------
INSERT IGNORE INTO `roles` (`id`, `name`) VALUES 
(1, 'ROLE_OWNER'),
(2, 'ROLE_MANAGER'),
(3, 'ROLE_CASHIER');

SET FOREIGN_KEY_CHECKS = 1;
