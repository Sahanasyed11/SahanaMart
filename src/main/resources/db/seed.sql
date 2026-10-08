-- =======================================================================
-- SahanaMart Seed Data
-- Anna University R2025 Semester 3 Capstone Project
-- Default users:
-- 1. Admin: admin@sahanamart.com / Admin@123 (Role: ADMIN)
-- 2. Seller: seller@sahanamart.com / Seller@123 (Role: SELLER)
-- 3. Buyer: buyer@sahanamart.com / Buyer@123 (Role: BUYER)
-- Note: AppContextListener also verifies/updates password hashes on startup.
-- =======================================================================

-- Seed Users
-- Passwords will also be confirmed/re-hashed by AppContextListener on boot if needed
INSERT INTO users (id, name, email, password_hash, role, phone, address, created_at)
VALUES 
(1, 'System Administrator', 'admin@sahanamart.com', '$2a$10$wK8V18Uo9M1Jt.Qo6qQ4yeW1w6j9gN0bC6eH6l6m1v1n1u1z1w1y1', 'ADMIN', '9876543210', 'SahanaMart HQ, Chennai, TN', CURRENT_TIMESTAMP),
(2, 'Priya Sharma (Seller)', 'seller@sahanamart.com', '$2a$10$wK8V18Uo9M1Jt.Qo6qQ4yeW1w6j9gN0bC6eH6l6m1v1n1u1z1w1y1', 'SELLER', '9876543211', '12 Anna Salai, Chennai, TN', CURRENT_TIMESTAMP),
(3, 'Arun Kumar (Buyer)', 'buyer@sahanamart.com', '$2a$10$wK8V18Uo9M1Jt.Qo6qQ4yeW1w6j9gN0bC6eH6l6m1v1n1u1z1w1y1', 'BUYER', '9876543212', '45 Gandhi Road, Coimbatore, TN', CURRENT_TIMESTAMP);

-- Seed Products (Seller ID = 2)
INSERT INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, created_at)
VALUES
(1, 2, 'Noise-Cancelling Wireless Headphones', 'Active noise cancellation with 40-hour battery life and ultra-comfortable memory foam ear cushions.', 2999.00, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80', CURRENT_TIMESTAMP),
(2, 2, 'Mechanical Gaming Keyboard RGB', 'Customizable RGB backlighting with tactile blue switches and aircraft-grade aluminum frame.', 1899.00, 30, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&q=80', CURRENT_TIMESTAMP),
(3, 2, 'Ultra HD 4K Action Camera', 'Waterproof 4K 60FPS sports action camera with dual color screens and image stabilization.', 4499.00, 15, 'Electronics', 'https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=500&q=80', CURRENT_TIMESTAMP),
(4, 2, 'Men Casual Slim-Fit Denim Jacket', 'Classic vintage distressed denim jacket crafted from 100% premium breathable cotton.', 1299.00, 40, 'Fashion', 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=500&q=80', CURRENT_TIMESTAMP),
(5, 2, 'Women Floral Cotton Summer Dress', 'Elegant breathable floral printed midi dress ideal for weekend outings and casual gatherings.', 899.00, 50, 'Fashion', 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=500&q=80', CURRENT_TIMESTAMP),
(6, 2, 'Classic Stainless Steel Chronograph Watch', 'Water-resistant analog wrist watch featuring sapphire crystal glass and genuine leather strap.', 1999.00, 20, 'Fashion', 'https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=500&q=80', CURRENT_TIMESTAMP),
(7, 2, 'Clean Code by Robert C. Martin', 'A handbook of agile software craftsmanship. An essential book for every software engineer.', 650.00, 45, 'Books', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=500&q=80', CURRENT_TIMESTAMP),
(8, 2, 'Design Patterns: Elements of Reusable OO Software', 'Classic GoF design patterns reference explaining 23 standard reusable object-oriented patterns.', 799.00, 35, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777f?w=500&q=80', CURRENT_TIMESTAMP),
(9, 2, 'Ergonomic Memory Foam Office Chair Cushion', 'High-density orthopedic tailbone relief seat cushion with breathable mesh non-slip cover.', 699.00, 60, 'Home & Living', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500&q=80', CURRENT_TIMESTAMP),
(10, 2, 'Stainless Steel Thermal Water Bottle 1L', 'Double-wall vacuum insulated flask keeping drinks ice-cold for 24h or piping hot for 12h.', 449.00, 80, 'Home & Living', 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=500&q=80', CURRENT_TIMESTAMP),
(11, 2, 'Organic Single-Origin Dark Roast Coffee (500g)', 'Artisan roasted Arabica coffee beans from Chikmagalur hills with rich chocolate and caramel notes.', 380.00, 75, 'Groceries', 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=500&q=80', CURRENT_TIMESTAMP),
(12, 2, 'Raw Organic Forest Honey (1kg)', '100% pure unfiltered wildflower honey harvested responsibly from wild natural apiaries.', 520.00, 50, 'Groceries', 'https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=500&q=80', CURRENT_TIMESTAMP);

-- Seed Sample Reviews
INSERT INTO reviews (product_id, user_id, rating, comment, created_at)
VALUES
(1, 3, 5, 'Superb sound quality and battery life. ANC works wonders in noisy coffee shops!', CURRENT_TIMESTAMP),
(7, 3, 5, 'Every computer science student must read this. Changed the way I write code.', CURRENT_TIMESTAMP),
(11, 3, 4, 'Rich and aromatic coffee. Best morning cup for coding sessions.', CURRENT_TIMESTAMP);

-- Seed Sample Completed Order for Buyer (ID = 3)
INSERT INTO orders (id, buyer_id, total_amount, status, shipping_address, payment_method, payment_status, created_at)
VALUES
(1001, 3, 3649.00, 'DELIVERED', '45 Gandhi Road, Coimbatore, TN - 641001', 'UPI', 'COMPLETED', CURRENT_TIMESTAMP);

INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at)
VALUES
(1001, 1, 1, 2999.00, CURRENT_TIMESTAMP),
(1001, 7, 1, 650.00, CURRENT_TIMESTAMP);
