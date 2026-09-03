CREATE TABLE payments
(
    id         UUID PRIMARY KEY,
    loan_id    UUID                     NOT NULL REFERENCES loans (id) ON DELETE CASCADE,
    amount     NUMERIC(15, 2)           NOT NULL,
    remaining  NUMERIC(15, 2)           NOT NULL,
    type       VARCHAR(20)              NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_loan_id ON payments (loan_id);

ALTER TABLE payments
    ADD CONSTRAINT chk_payments_type
        CHECK (type IN ('REGULAR', 'PARTIAL', 'EARLY', 'FULL'));