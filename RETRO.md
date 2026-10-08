# SahanaMart Sprint Retrospectives (RETRO.md)

> **Sprint Format**: What worked, what didn't, one change for next sprint.  
> **Course Window**: Jul 24 – Oct 10, 2026 (Weeks 1 to 11)  
> **Builder**: Solo Capstone Submission  

---

### Kickoff Sprint (Jul 24 – Jul 27)
- **What worked**: Project initialized cleanly targeting JDK 17, Tomcat 9 (`javax.servlet.*`), and base schema v1 drafted with foreign keys and indexes.
- **What didn't**: Deciding whether to use MySQL or embedded H2 as the primary development database took excess deliberation.
- **One change for next sprint**: Standardize on H2 with MySQL compatibility mode so the project runs immediately with zero database installation required.

### Sprint 1: Authentication & Base DAO (Jul 27 – Aug 2)
- **What worked**: jBCrypt password hashing and single `AppContextListener` owning HikariCP lifecycle worked reliably on first test run.
- **What didn't**: Session fixation vulnerability identified during initial login test; session was not invalidated before authenticating.
- **One change for next sprint**: Enforce `session.invalidate()` and regenerate new session ID immediately upon successful login.

### Sprint 2: Core Shopping Flow (Aug 3 – Aug 9)
- **What worked**: Seller listing CRUD, buyer product catalog browsing, and cart session tracking functioned smoothly.
- **What didn't**: Empty cart checkout was possible without prior validation, leading to blank order records.
- **One change for next sprint**: Implement strict cart emptiness and inventory stock boundary checks before opening any checkout transaction.

### Sprint 3: MVP Review & DTO Layer (Aug 10 – Aug 16)
- **What worked**: MVP review completed successfully; end-to-end buyer checkout journey demonstrated to evaluator with zero defects.
- **What didn't**: Entity models were initially leaking `passwordHash` in JSON serialization.
- **One change for next sprint**: Strictly separate DTOs from entity models using `UserResponseDTO` and the `{success, data, error}` envelope.

### Sprint 4: Dashboards & Moderation (Aug 17 – Aug 23)
- **What worked**: Seller portal incoming orders and Admin moderation panel built with complete role isolation.
- **What didn't**: Sellers could theoretically edit other sellers' listings by tampering with URL parameters.
- **One change for next sprint**: Add ownership verification in `ProductService` so only the listing owner or Admin can modify a product.

### Sprint 5: Search Polish & Order Status (Aug 24 – Aug 30)
- **What worked**: Category filtering and keyword search combined cleanly with pagination; migration `V2__order_status.sql` authored.
- **What didn't**: Multi-word keyword queries were missing matches because SQL was checking exact match rather than `LOWER(name) LIKE ?`.
- **One change for next sprint**: Case-insensitively tokenize keyword search queries across product names and descriptions.

### Sprint 6: Verified Reviews & Edge Cases (Aug 31 – Sep 6)
- **What worked**: Verified buyer review enforcement implemented; only customers with confirmed purchases can leave star ratings.
- **What didn't**: Negative price or zero stock values were occasionally permitted when submitting listings with edge-case form data.
- **One change for next sprint**: Add fail-fast validation in `ValidationUtil` rejecting invalid pricing or negative stock prior to DAO calls.

### Sprint 7: Security Hardening & Full Testing (Sep 7 – Sep 13)
- **What worked**: 100% of SQL parameterized with `PreparedStatement`; custom error pages in `web.xml` configured hiding stack traces.
- **What didn't**: Mocking HikariCP connections in early unit tests caused connection leak warnings during teardown.
- **One change for next sprint**: Ensure all test fixtures close in-memory H2 pools cleanly in `@AfterAll` hooks.

### Sprint 8: Deployment Packaging & Health Check (Sep 14 – Sep 20)
- **What worked**: Deployable WAR package generated; `GET /api/v1/health` returning `{"status":"UP","db":"UP"}` verified.
- **What didn't**: External Tomcat required manual WAR deployment during local testing.
- **One change for next sprint**: Build standalone `AppLauncher` embedded Tomcat runner for instant one-click execution via `mvn compile exec:java`.

### Sprint 9: AI Chatbot Backend (Sep 21 – Sep 27)
- **What worked**: `ChatProvider` strategy interface, `MockChatProvider` FAQ engine, and `ChatServlet` with 10 msg/min rate limit built.
- **What didn't**: Gemini API network calls would hang when internet was unstable without an explicit timeout.
- **One change for next sprint**: Set 5-second HTTP socket timeout and wrap API call with instant fallback to `MockChatProvider`.

### Sprint 10: Chatbot UI & Documentation (Sep 28 – Oct 4)
- **What worked**: Floating chat widget with quick-reply pills integrated across all pages; README and diagrams finalized.
- **What didn't**: Mobile viewport layout had overlapping chat widget buttons on small phone screens.
- **One change for next sprint**: Add CSS responsive media query so the chat widget smoothly adapts to full-width mobile viewports.

### Sprint 11: Final Regression & Presentation (Oct 5 – Oct 10)
- **What worked**: Full regression pass clean across all core user flows; final report, slide deck, and demo script completed.
- **What didn't**: Nothing major; all features F1–F8 and O1–O4 operating cleanly according to course specification.
- **One change for future iterations**: Extend analytics with CSV export for seller sales reports.
