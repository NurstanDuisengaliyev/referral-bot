-- ============================================
-- V3__add_username_to_users.sql
-- Add Telegram username field to users
-- ============================================

ALTER TABLE users
    ADD COLUMN username VARCHAR(255);

-- Ensure usernames are unique (optional: you can defer this until after data migration)
ALTER TABLE users
    ADD CONSTRAINT uq_users_username UNIQUE (username);