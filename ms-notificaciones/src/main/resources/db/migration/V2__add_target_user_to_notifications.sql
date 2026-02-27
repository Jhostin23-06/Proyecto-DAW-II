ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS target_user_id VARCHAR(120);

CREATE INDEX IF NOT EXISTS idx_notifications_target_user_id
    ON notifications(target_user_id);
