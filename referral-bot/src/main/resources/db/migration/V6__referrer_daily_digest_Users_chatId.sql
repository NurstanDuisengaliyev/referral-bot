-- Add when we last notified a referrer (nullable = never sent)
ALTER TABLE referrers
    ADD COLUMN last_digest_at TIMESTAMP NULL;

ALTER TABLE users
    ADD COLUMN chat_id BIGINT NOT NULL;