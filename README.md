# 🛒 SahanaMart — Enterprise E-Commerce Platform

> 🌐 **Live Cloud URL:** [https://sahanamart-vpr3.onrender.com/](https://sahanamart-vpr3.onrender.com/)  
> **Anna University R2025 · Semester 3 Capstone Project**  
> **Core Stack:** Java Servlets (`javax.servlet.*`) · JDBC (`PreparedStatement`) · Apache Tomcat 9.0.x · HikariCP · H2 / MySQL · jBCrypt · JUnit 5  
> **Builder:** Solo Capstone Submission  

---

## 📌 1. Project Overview

**SahanaMart** is an enterprise-grade, multi-role e-commerce web platform engineered strictly adhering to the Anna University R2025 Java Web Development curriculum. The application supports full shopping workflows for **Buyers**, listing management and order processing for **Sellers**, system oversight for **Administrators**, and an intelligent **AI Shopping Assistant** powered by a pluggable strategy provider.

### ✨ Key Capabilities (Features F1–F8 + O1–O4)
- **F1: Secure Authentication & Role Authorization**: Multi-role signup (Buyer/Seller), seed-only Admin account, salted jBCrypt password hashing, session fixation prevention (regenerated session ID upon login), and strict `AuthFilter` RBAC.
- **F2: Seller Catalog Management**: Real-time listing creation, modification, stock level monitoring, and soft deletion.
- **F3: Buyer Browse, Search & Filter**: Keyword search, category-based filtering, and paginated product browsing.
- **F4: Interactive Shopping Cart**: Add, modify quantity, bounds checking against real inventory, and running total calculations.
- **F5: ACID Transactional Checkout**: Multi-step checkout with mock payment confirmation (UPI, Mock Card, Net Banking, COD), atomic stock deduction, and order generation inside a single database transaction.
- **F6: Order Tracking & Fulfillment**: Buyer order history and tracking; Seller incoming orders management.
- **F7: Administrator Moderation**: Comprehensive management of user accounts, order statuses, and listing moderation.
- **F8: Verified Reviews & Star Ratings**: 1-to-5 star rating and feedback submission restricted to verified purchasers.
- **O1: Save-for-Later Wishlist**: Dedicated personal wishlist with one-click migration to cart.
- **O2: Order Status Progression**: Full state machine: `PENDING` ➔ `CONFIRMED` ➔ `SHIPPED` ➔ `DELIVERED` (or `CANCELLED`).
- **O3: Seller Analytics Dashboard**: Store metrics covering active inventory count, units sold, and gross revenue.
- **O4: AI Shopping Assistant**: Domain-scoped AI chatbot (`MockChatProvider` + `GeminiChatProvider`) with per-session sliding-window rate limiting (10 req/min), in-memory query cache, and floating widget UI.

---

## 🏗️ 2. Architectural Diagrams

### D1: Entity-Relationship Diagram (ERD)
```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "sells"
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ CART_ITEMS : "has"
    USERS ||--o{ WISHLIST_ITEMS : "saves"
    USERS ||--o{ REVIEWS : "writes"
    PRODUCTS ||--o{ ORDER_ITEMS : "included_in"
    PRODUCTS ||--o{ CART_ITEMS : "added_to"
    PRODUCTS ||--o{ WISHLIST_ITEMS : "saved_in"
    PRODUCTS ||--o{ REVIEWS : "receives"
    ORDERS ||--|{ ORDER_ITEMS : "contains"

    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password_hash
        varchar role
        varchar phone
        text address
        timestamp created_at
    }

    PRODUCTS {
        bigint id PK
        bigint seller_id FK
        varchar name
        text description
        decimal price
        int stock_qty
        varchar category
        varchar image_url
        timestamp created_at
        timestamp updated_at
    }

    CART_ITEMS {
        bigint id PK
        bigint user_id FK
        bigint product_id FK
        int quantity
        timestamp created_at
    }

    ORDERS {
        bigint id PK
        bigint buyer_id FK
        decimal total_amount
        varchar status
        text shipping_address
        varchar payment_method
        varchar payment_status
        timestamp created_at
    }

    ORDER_ITEMS {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        int quantity
        decimal unit_price
        timestamp created_at
    }

    REVIEWS {
        bigint id PK
        bigint product_id FK
        bigint user_id FK
        int rating
        text comment
        timestamp created_at
    }

    WISHLIST_ITEMS {
        bigint id PK
        bigint user_id FK
        bigint product_id FK
        timestamp created_at
    }
```

---

### D2: Use Case Diagram
```mermaid
graph LR
    Buyer((Buyer))
    Seller((Seller))
    Admin((Administrator))

    subgraph SahanaMart Platform
        UC1[Register & Login]
        UC2[Browse & Search Catalog]
        UC3[Manage Cart & Wishlist]
        UC4[Place Order with Mock Pay]
        UC5[Track Order Status]
        UC6[Leave Verified Review]
        UC7[Interact with AI Chatbot]
        UC8[Manage Products & Stock]
        UC9[Process Incoming Orders]
        UC10[Moderate Users & Listings]
        UC11[System Health Monitoring]
    end

    Buyer --> UC1
    Buyer --> UC2
    Buyer --> UC3
    Buyer --> UC4
    Buyer --> UC5
    Buyer --> UC6
    Buyer --> UC7

    Seller --> UC1
    Seller --> UC8
    Seller --> UC9

    Admin --> UC1
    Admin --> UC10
    Admin --> UC11
```

---

### D3: Sequence Diagram — Transactional Place Order Flow
```mermaid
sequenceDiagram
    autonumber
    actor Buyer
    participant Browser as JSP / Web Browser
    participant CheckoutServlet as CheckoutServlet
    participant OrderService as OrderService
    participant DBUtil as DBUtil / HikariCP
    participant ProductDAO as ProductDAO
    participant OrderDAO as OrderDAO
    participant CartDAO as CartDAO

    Buyer->>Browser: Click "Place Order & Pay"
    Browser->>CheckoutServlet: POST /checkout (Address, PaymentMethod)
    CheckoutServlet->>OrderService: checkout(buyerId, orderDTO)
    OrderService->>CartDAO: findByUserId(buyerId)
    CartDAO-->>OrderService: List<CartItem>
    OrderService->>OrderService: Validate cart non-empty & verify stock
    OrderService->>DBUtil: getConnection()
    Note over OrderService,DBUtil: Begin ACID Transaction (autoCommit = false)
    OrderService->>OrderDAO: create(order, conn)
    OrderDAO-->>OrderService: createdOrder (ID: #1001)
    loop For each item in cart
        OrderService->>OrderDAO: addOrderItem(orderItem, conn)
        OrderService->>ProductDAO: deductStock(productId, qty, conn)
    end
    OrderService->>CartDAO: clearCart(buyerId, conn)
    OrderService->>DBUtil: conn.commit()
    Note over OrderService,DBUtil: Transaction Committed
    OrderService-->>CheckoutServlet: Order #1001 Confirmed
    CheckoutServlet-->>Browser: Redirect to /orders/success?orderId=1001
    Browser-->>Buyer: Render Confirmation Screen
```

---

## 🛠️ 3. Technology Stack & Design Patterns

| Layer | Technology / Implementation | Design Pattern Applied |
|---|---|---|
| **Presentation** | JSP 2.3, JSTL 1.2, CSS3 Flexbox/Grid, Vanilla ES6 JS | *View Helper / Decorator* |
| **Controller** | Java Servlets 4.0 (`javax.servlet.*`) | *Front Controller Pattern* |
| **Security** | `AuthFilter` (RBAC), `EncodingFilter` (UTF-8), jBCrypt 0.4 | *Interception Filter Pattern* |
| **Service Layer** | POJO Service classes with Fail-Fast Validation | *Service Layer / Facade* |
| **Data Access** | JDBC with `PreparedStatement` only | *Data Access Object (DAO)* |
| **Connection Pool** | HikariCP 5.1.0 owned by `AppContextListener` | *Singleton / Resource Pool* |
| **Database** | H2 (File-based Persistent & In-Memory for Unit Tests) + MySQL compatible | *Active Record / Abstract Factory* |
| **AI Integration** | `ChatProvider` interface, `MockChatProvider`, `GeminiChatProvider` | *Strategy Pattern & Factory Pattern* |
| **Testing** | JUnit 5 Jupiter, Mockito 5.11 | *Mock Object Pattern* |

---

## 🚀 4. Setup & Running Instructions

### Prerequisites
- **Java SE Development Kit (JDK 17 or higher)**
- **Apache Maven 3.8+**
- Git

### Option A: One-Click Execution (Embedded Server)
SahanaMart includes an embedded Apache Tomcat 9 launcher for instant execution with zero external server configuration:

```bash
# 1. Clone the repository
git clone https://github.com/shabana/sahanamart.git
cd sahanamart

# 2. Run with Maven
mvn compile exec:java
```
The server will boot on `http://localhost:8080/`.

---

### Option B: Deploying to External Apache Tomcat 9.0.x
1. Build the deployable WAR package:
   ```bash
   mvn clean package
   ```
2. Copy `target/sahanamart.war` into your Tomcat `webapps/` folder:
   ```bash
   cp target/sahanamart.war /path/to/apache-tomcat-9.0.x/webapps/
   ```
3. Start Tomcat via `bin/startup.sh` (or `bin/startup.bat` on Windows).
4. Access the web app at `http://localhost:8080/sahanamart/`.

---

## 🔑 5. Pre-seeded Demonstration Credentials

All seeded accounts are automatically loaded upon initial boot and confirmed by `AppContextListener`:

| Role | Email | Password | Access Rights |
|---|---|---|---|
| **Administrator** | `admin@sahanamart.com` | `Admin@123` | Full system oversight, user/listing moderation, order status override |
| **Seller** | `seller@sahanamart.com` | `Seller@123` | Store dashboard, add/edit/delete listings, manage incoming orders |
| **Buyer** | `buyer@sahanamart.com` | `Buyer@123` | Browse catalog, cart, place orders, write reviews, wishlist |

---

## 🩺 6. REST API & Health Check Reference

All API endpoints reside under `/api/v1/...` and return the standard JSON envelope:
`{ "success": true, "data": ..., "error": null, "timestamp": 1728362400000 }`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1/health` | Health check returning `{"status":"UP","db":"UP"}` |
| `POST` | `/api/v1/auth/login` | Authenticate and obtain user response DTO |
| `POST` | `/api/v1/auth/register` | Register new Buyer or Seller account |
| `GET` | `/api/v1/products` | Retrieve catalog with `keyword`, `category`, and `page` parameters |
| `GET` | `/api/v1/products/{id}` | Get detailed product specifications and ratings |
| `POST` | `/api/v1/chat` | AI Chatbot query endpoint (Rate limited: 10 msg/min) |

---

## 🧪 7. Automated Test Suite

Run the full automated test suite (in-memory H2 DAO integration + Mockito Service tests):
```bash
mvn clean test
```

---

## 📄 8. License & University Disclaimer
Submitted for **Anna University Regulation 2025 (R2025), Semester 3 Capstone Project Evaluation**. All academic honor code rules have been strictly observed.
