ALTER TABLE credit
    ADD COLUMN user_email VARCHAR(100) NOT NULL;
ALTER TABLE credit
    ADD COLUMN interest_rate DECIMAL;
ALTER TABLE credit
    ADD COLUMN manager_comment DECIMAL;