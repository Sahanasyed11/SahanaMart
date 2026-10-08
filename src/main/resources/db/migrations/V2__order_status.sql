-- =======================================================================
-- SahanaMart Database Migration: V2__order_status.sql
-- Anna University R2025 Semester 3 Capstone Project
-- Complies with Section 14: Numbered migration file for Order Status Workflow (O2)
-- Workflow states: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED (or CANCELLED)
-- =======================================================================

-- Ensure status column exists and default is PENDING
-- In H2/MySQL syntax:
ALTER TABLE orders ALTER COLUMN status SET DEFAULT 'PENDING';

-- Index for fast status querying and reporting
CREATE INDEX IF NOT EXISTS idx_orders_status_date ON orders(status, created_at);
