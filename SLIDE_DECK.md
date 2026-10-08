# SahanaMart: Capstone Project Slide Deck & Rehearsed Demo Script

> **Anna University R2025 Semester 3 Capstone Presentation**  
> **Topic:** Enterprise E-Commerce Platform with Java Servlets, JDBC, and AI Assistant  
> **Duration:** 10 Minutes (7 Min Presentation + 3 Min Live Demo)  

---

## 📽️ Slide Deck Outline

### Slide 1: Title & Introduction
- **Title:** SahanaMart — Enterprise Multi-Role E-Commerce Platform
- **Regulation:** Anna University R2025, Semester 3 Capstone
- **Core Technologies:** Java Servlets (`javax.servlet.*`), JDBC, Apache Tomcat 9, HikariCP, H2/MySQL
- **Presenter:** Solo Capstone Builder

### Slide 2: Problem Statement & Objectives
- **Problem:** Many modern web projects rely heavily on heavy frameworks (Spring/Hibernate) without mastering core foundational web protocols, connection pooling mechanics, and direct SQL parameterization.
- **Objectives:**
  - Build an end-to-end, high-performance e-commerce platform using pure Java Servlets & JDBC.
  - Implement role-based access for Buyers, Sellers, and Admins.
  - Guarantee data integrity via ACID transactional checkouts.
  - Integrate an AI Chatbot assistant with rate-limiting and pluggable provider strategies.

### Slide 3: System Architecture & Layered Design
- **Presentation Tier:** Responsive JSP 2.3, JSTL 1.2 (`fn:escapeXml` XSS protection), modern CSS Grid/Flexbox.
- **Controller Tier:** `javax.servlet.*` Servlets dispatching requests and enforcing HTTP semantics.
- **Filter Tier:** `AuthFilter` (RBAC session enforcement), `EncodingFilter` (UTF-8).
- **Service Tier:** POJO business logic, fail-fast validation, atomic checkout coordination.
- **Persistence Tier:** JDBC DAOs using `PreparedStatement` only, connected via a single HikariCP pool owned by `AppContextListener`.

### Slide 4: Database Design (D1 ERD)
- 7 Relational Tables: `users`, `products`, `cart_items`, `orders`, `order_items`, `reviews`, `wishlist_items`.
- Constraints: Primary keys, Foreign keys with cascading rules, Unique constraints on email and cart/wishlist tuples.
- Data Types: Exact monetary calculations using `DECIMAL(10,2)` (avoiding floating point precision loss).

### Slide 5: Core Shopping Lifecycle & State Machine (F1–F6)
- **Buyer Journey:** Register ➔ Browse Catalog ➔ Keyword & Category Filter ➔ Add to Cart ➔ Checkout.
- **Order State Machine (O2):** `PENDING` ➔ `CONFIRMED` ➔ `SHIPPED` ➔ `DELIVERED` (or `CANCELLED`).
- **ACID Checkout:** Stock bounds verification, order insertion, line item persistence, and stock deduction wrapped in an atomic commit/rollback block.

### Slide 6: Multi-Role Portals (F7 & F2)
- **Seller Portal:** Product listing management, inventory stock levels, real-time incoming orders view, and sales revenue analytics (O3).
- **Admin Console:** User account management, system-wide order status overrides, and product listing moderation.

### Slide 7: Verified Reviews & Wishlist (F8 & O1)
- **Verified Buyer Reviews:** Rating (1–5 stars) and feedback submission strictly restricted to buyers with confirmed purchases.
- **Save-for-Later Wishlist:** One-click personal wishlist with instant transfer to shopping cart.

### Slide 8: AI Shopping Assistant Architecture (O4 / Section 11)
- **Design Patterns:** Pluggable `ChatProvider` interface (Strategy Pattern) with `ChatProviderFactory`.
- **Providers:**
  - `MockChatProvider`: Smart heuristic engine answering domain FAQs (shipping, returns, tracking, payments).
  - `GeminiChatProvider`: REST client connecting to Google Gemini with a 5-second socket timeout and automatic fallback.
- **Security & Reliability:** Server-side API keys only, in-memory per-session query caching, and sliding-window rate limiting (10 msg/min).

### Slide 9: Security & Quality Assurance
- **SQL Injection Prevention:** 100% parameterization via `PreparedStatement`; 0 string concatenations.
- **Password Security:** Salted jBCrypt hashing (cost factor 10); passwords never logged.
- **Error Handling:** Custom `web.xml` error pages (404 and 500) preventing stack trace exposure.
- **Testing:** JUnit 5 integration tests against embedded in-memory H2 database (`jdbc:h2:mem:test`) + Mockito service tests.
- **CI/CD:** Automated build and verify workflow on GitHub Actions.

### Slide 10: Conclusion & Deliverables Summary
- Full code deliverables adhering strictly to university requirements.
- Zero-setup embedded execution (`mvn compile exec:java`) + standard WAR deployment.
- Deployed Health Check API (`GET /api/v1/health` returning `{"status":"UP","db":"UP"}`).

---

## 🎙️ Rehearsed Live Demo Script (3-Minute Walkthrough)

### [0:00 - 0:30] Introduction & Server Boot
> *"Respected evaluators, today I present **SahanaMart**, built with Java Servlets, JDBC, and Apache Tomcat 9. As you can see on the terminal, the application boots with a single command: `mvn compile exec:java`, initializing our HikariCP connection pool and verifying our schema and seed data."*
- **Action:** Point to running terminal output and navigate to `http://localhost:8080/`.

### [0:30 - 1:15] Buyer Complete Journey (Browse ➔ Cart ➔ Checkout)
> *"Starting as a buyer, I can search for 'Headphones' in the Electronics category. When I open the product page, we see real-time stock availability and verified star ratings. I add 2 units to the cart, review the running total, and proceed to checkout. Notice how our mock payment simulation completes, atomically decrements inventory stock in our database, and produces our confirmed order receipt with an explicit Order ID."*
- **Action:** Perform search, add to cart, checkout with UPI, show Order Confirmation screen.

### [1:15 - 1:55] Seller Portal & Order Workflow
> *"Next, logging in as a Seller (`seller@sahanamart.com`), we enter the Seller Portal. Here we see active inventory counts and total sales revenue. In the incoming orders view, the seller can transition the order status from CONFIRMED to SHIPPED and DELIVERED. Returning to the buyer's order history, the order tracking timeline updates in real-time."*
- **Action:** Log in as seller, show dashboard metrics, update order status to SHIPPED.

### [1:55 - 2:30] AI Shopping Assistant & Rate Limiting
> *"Now I will demonstrate our AI Shopping Assistant. Clicking the floating widget, a customer can ask 'How do I return a product?'. The assistant immediately provides our verified 7-day refund policy. Our backend enforces a strict per-session rate limit of 10 messages per minute and caches identical queries to ensure zero server strain."*
- **Action:** Open chat widget, click quick-reply pill, demonstrate instant response.

### [2:30 - 3:00] Admin Moderation & Health Check
> *"Finally, logging into the Admin console (`admin@sahanamart.com`), the administrator has platform oversight to moderate listings and manage user accounts. To verify system uptime, our health endpoint at `/api/v1/health` reports status UP and db UP. Thank you, I welcome your questions."*
- **Action:** Open `/admin/dashboard` and open `/api/v1/health` in browser tab.
