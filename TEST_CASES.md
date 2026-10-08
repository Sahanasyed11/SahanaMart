# SahanaMart End-to-End Test Case Sheet (TEST_CASES.md)

> **Evaluation Window:** Anna University R2025 Semester 3 Capstone Evaluation  
> **Core Flow:** Register ➔ Login ➔ Browse / Search ➔ Cart ➔ Checkout ➔ Track Order ➔ Verified Review  

---

## 📋 Test Matrix Summary

| Test Case ID | Test Category | Feature | Expected Outcome | Status |
|---|---|---|---|---|
| **TC-01** | Functional | F1: Registration | Buyer account registered; password hashed with jBCrypt; redirected to login | **PASS** |
| **TC-02** | Security | F1: Role Security | Admin registration blocked; returns validation error 400 | **PASS** |
| **TC-03** | Functional | F1: Authentication | Buyer logs in with valid credentials; session regenerated | **PASS** |
| **TC-04** | Security | F1: Password Safety | Login with wrong password rejected; 401 Unauthorized | **PASS** |
| **TC-05** | Functional | F2: Seller Listing | Seller creates product with title, price, stock, category; appears in catalog | **PASS** |
| **TC-06** | Validation | F2: Input Validation | Seller creating product with negative price or zero title rejected | **PASS** |
| **TC-07** | Functional | F3: Search & Filter | Buyer searches "Headphones" and filters by "Electronics"; matching items returned | **PASS** |
| **TC-08** | Functional | F4: Shopping Cart | Buyer adds item to cart; quantity incremented; running total updated | **PASS** |
| **TC-09** | Validation | F4: Stock Limit | Adding quantity higher than available stock rejected | **PASS** |
| **TC-10** | Validation | F5: Empty Cart | Checking out with 0 items in cart rejected with descriptive error | **PASS** |
| **TC-11** | Transaction | F5: ACID Checkout | Order placed with mock payment; stock decremented atomically; cart cleared | **PASS** |
| **TC-12** | Functional | F6: Order Tracking | Buyer views order history; status displays `CONFIRMED` | **PASS** |
| **TC-13** | Workflow | O2: Order Status | Seller marks order `SHIPPED` and `DELIVERED`; buyer sees live progress | **PASS** |
| **TC-14** | Functional | F8: Verified Review | Buyer leaves 5-star review on delivered item; review appears with star rating | **PASS** |
| **TC-15** | Security | F8: Review Restriction| Buyer attempting to review unpurchased item blocked | **PASS** |
| **TC-16** | Functional | O1: Wishlist | Buyer adds item to wishlist; moves to cart; item removed from wishlist | **PASS** |
| **TC-17** | Functional | F7: Admin Moderation | Admin deletes abusive product listing; product no longer visible in catalog | **PASS** |
| **TC-18** | Functional | O4: AI Chatbot FAQ | Chatbot queried with "How do I return?"; returns 7-day return policy answer | **PASS** |
| **TC-19** | Security | O4: AI Rate Limit | User sending > 10 messages within 1 minute receives rate limit message | **PASS** |
| **TC-20** | Health | Week 8 Health API | `GET /api/v1/health` returns `{"status":"UP","db":"UP"}` with HTTP 200 | **PASS** |

---

## 📝 Step-by-Step Manual Test Execution Script

### Journey 1: Buyer Complete Shopping Cycle
1. Open `http://localhost:8080/auth/register`.
2. Enter Name: `Kavitha Buyer`, Email: `kavitha@test.com`, Password: `Buyer@123`, Role: `BUYER`. Click **Register**.
3. Verify successful registration alert.
4. Log in using `kavitha@test.com` / `Buyer@123`.
5. Navigate to **Catalog** (`/products`).
6. In the search bar, enter `Headphones` and select `Electronics`. Click **Filter Catalog**.
7. Click the product card to view **Product Details** (`/products/view?id=1`).
8. Select Quantity: `2`. Click **Add to Cart 🛍️**.
9. In the cart page (`/cart`), verify Subtotal and Total reflect `₹5998.00`.
10. Click **Proceed to Checkout ➔**.
11. Enter delivery address: `12 Anna Salai, Guindy, Chennai, TN - 600025`.
12. Select Payment Method: `UPI Instant Pay`. Click **Place Order & Pay ➔**.
13. Verify redirection to **Order Confirmation** (`/orders/success`) showing Order ID.
14. Navigate to **My Orders** (`/orders`) and verify order status is `CONFIRMED`.
15. Navigate back to product page (`/products/view?id=1`) and submit a 5-star review.
16. Verify review appears under Customer Reviews with rating ⭐⭐⭐⭐⭐.

---

### Journey 2: Seller Inventory & Order Processing
1. Log in with pre-seeded seller credentials: `seller@sahanamart.com` / `Seller@123`.
2. Access **Seller Portal** (`/seller/dashboard`).
3. Verify store statistics: Active listings, units sold, total revenue.
4. Click **+ Add New Product** (`/seller/products/new`).
5. Fill in Name: `Ergonomic Wireless Trackball`, Price: `1499.00`, Stock: `25`, Category: `Electronics`. Click **Save Product Listing**.
6. Verify listing appears in **My Products** table.
7. Click **Incoming Orders** (`/seller/orders`).
8. For an incoming customer order, change status from `CONFIRMED` to `SHIPPED` and click **Update**.
9. Verify status is persisted in the database.

---

### Journey 3: Administrator Moderation
1. Log in with admin credentials: `admin@sahanamart.com` / `Admin@123`.
2. Access **Admin Console** (`/admin/dashboard`).
3. Verify platform KPIs: Total Registered Users, Total Listings, Orders Placed, Platform Revenue.
4. Click **Manage Users** (`/admin/users`) and inspect user account list.
5. Click **Moderate Products** (`/admin/products`) and verify product moderation controls.

---

### Journey 4: AI Shopping Assistant & Rate Limiting
1. Click the floating chat bubble icon 💬 in the bottom-right corner.
2. Click quick-pill **"Track Order"**. Verify instant AI reply explaining order tracking navigation.
3. Click quick-pill **"Returns"**. Verify 7-day refund policy reply.
4. Type: *"What is the delivery time?"*. Verify instant response regarding 3-5 business days.
5. Send more than 10 rapid messages. Verify sliding-window rate limit message activates gracefully without server crash.
