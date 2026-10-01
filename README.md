# ShelfIQ — Autonomous Retail Intelligence & POS Engine

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.0-61DAFB?style=for-the-badge&logo=react&logoColor=black)](https://react.dev)
[![Vite](https://img.shields.io/badge/Vite-6.0-646CFF?style=for-the-badge&logo=vite&logoColor=white)](https://vitejs.dev)
[![Java](https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com)

ShelfIQ is an enterprise-grade, multi-tenant retail management system designed for modern brick-and-mortar storefronts, supermarkets, and chain retail. It combines high-speed POS transactions, automated real-time inventory synchronization, OCR invoice ingestion, AI copilot decision-making, and nationwide cross-territory sales velocity analytics.

---

## ✨ Key Features

### 🖥️ Spatial Apple Liquid Glass UI
* **Hyper-Glossy Frosted Aesthetics**: Multi-layered backdrop blurs (`blur(32px) saturate(220%)`), specular upper menisci, chromatic refraction borders, and glowing focus states.
* **Physics-Driven Interactions**:
  * 🧲 **Magnetic Buttons**: Pull toward the cursor within a 120px interaction radius with elastic snapback.
  * 🔢 **60fps Animated Counters**: Exponential ease-out rolling digits on all KPIs, totals, and cart values.
  * 🌊 **Coordinate-Aware Ripples**: Smooth concentric waves originating from click coordinates.
  * 🎉 **Particle Physics Explosions**: Canvas-driven confetti bursts upon completing checkouts.
  * 🪟 **Ubiquitous Hovering Glass**: Spatial card elevation (`translateY(-6px)`), luminous table row highlighting, and depth transitions.

### ⚡ Real-Time Point of Sale (POS)
* Fast product search, category filtering, and barcode scanner simulation.
* Dynamic cart calculation with tax, item discounts, and multiple payment methods (Cash, UPI / QR, Card, Store Credit).
* Instant printable customer receipts with animated invoice rendering and cash-drawer integration hooks.

### 📦 Inventory & Stock Intelligence
* Real-time stock decrement on checkout with automatic reorder point triggers.
* Comprehensive stock movement audit logs (Sales, Adjustments, Restocks, Returns).
* SKU health classification: In-Stock, Low-Stock Warning, and Out-of-Stock alerts.

### 📄 OCR Supplier Invoice Ingestion
* Drag-and-drop supplier invoice scanner with instant text parsing.
* Extracts vendor details, invoice numbers, line items, quantities, and cost prices.
* One-click bulk inventory restock directly from parsed supplier invoices.

### 🏆 Cross-Territory Regional Sales Leaderboard
* **Nationwide Indian Coverage**: Multi-dimensional rankings supporting **all 28 Indian States & 5 Union Territories (33 territories total)**.
* **Granular Drilldowns**:
  * **By Entity**: Top selling SKUs vs. Top revenue Categories.
  * **By Geography**: 12 Major Metro Cities, 33 States & UTs, and 6 Macro Regional Zones.
  * **By Timeframe**: Today, 7 Days, 30 Days, All-Time.
* **Interactive Presentation**: 3D Gold, Silver, and Bronze podiums, dynamic market share progress bars, and localized brand intelligence benchmarks (Amul, Nandini, Verka, Aavin, Milma, etc.).

### 🤖 AI Retail Copilot
* Conversational assistant for instant store telemetry and data-driven recommendations.
* Inquires on inventory health, sales velocity, reorder forecasting, and supplier performance.

---

## 🏗️ System Architecture

```
ShopKeeper AI / ShelfIQ
├── backend/                  # Spring Boot 3.3.4 (Java 21+)
│   ├── src/main/java/com/shelfiq/
│   │   ├── auth/             # JWT Authentication & Spring Security
│   │   ├── common/           # Multi-tenant context & Exception handlers
│   │   ├── inventory/        # Stock tracking & Movement audit service
│   │   ├── product/          # Product catalog & Category taxonomy
│   │   ├── purchase/         # Purchase order replenishment workflow
│   │   ├── sales/            # POS checkout & Regional leaderboard engine
│   │   ├── store/            # Store profile & Cashier assignment
│   │   └── supplier/         # Supplier & Vendor management
│   └── src/main/resources/   # Application YAML & Database schema
├── frontend/                 # React 19 + Vite 6 Single Page Application
│   ├── src/
│   │   ├── api/              # Axios REST client with interceptors
│   │   ├── components/       # Views: POS, Inventory, Leaderboard, etc.
│   │   ├── context/          # React Context (Auth, Store state)
│   │   └── index.css         # Apple Liquid Glass design system & Keyframes
│   └── vite.config.js
└── docker/                   # Docker Compose & MySQL initialization scripts
```

---

## 🚀 Getting Started

### Prerequisites
* **Java**: JDK 21 or higher
* **Maven**: 3.8+ (or bundled `mvnw`)
* **Node.js**: v18+ and `npm`
* **Docker & Docker Compose** (Optional, for production container deployment)

---

### 1. Run Backend (Spring Boot)

```bash
cd backend

# Run with local H2 in-memory profile
mvn spring-boot:run
```

* Backend API will start on **`http://localhost:8085`**.
* Swagger UI / OpenAPI docs: `http://localhost:8085/swagger-ui.html`
* H2 Console: `http://localhost:8085/h2-console` (JDBC URL: `jdbc:h2:mem:shelfiq_db`)

---

### 2. Run Frontend (React + Vite)

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

* Frontend application will start on **`http://localhost:5173`**.

---

### 3. Docker Deployment (Optional)

```bash
# Start MySQL container
docker-compose -f docker/docker-compose.yml up -d
```

---

## 🔐 Default Access Credentials

On fresh deployment, the primary store and accounts are auto-initialized:

| Role | Username / Email | Password |
|---|---|---|
| **Store Owner (Administrator)** | `owner@shelfiq.io` | `Password123!` |
| **Cashier Staff (Employee)** | `staff@shelfiq.io` | `Password123!` |

*(Can also be authenticated using 1-Click Fast Login on the login screen).*

---

## 🧪 Testing & Verification

```bash
# Backend Integration Tests
cd backend
mvn test -Dtest=ShelfIqIntegrationTest

# Frontend Production Build
cd frontend
npm run build
```

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).
