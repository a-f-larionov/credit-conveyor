CREATE TABLE payments
(
    id          UUID PRIMARY KEY,
    loan_id     UUID                     NOT NULL REFERENCES loans (id) ON DELETE CASCADE,
    amount      NUMERIC(15, 2)           NOT NULL,
    datetime    TIMESTAMP WITH TIME ZONE NOT NULL,
    type        VARCHAR(20)              NOT NULL,
    new_balance NUMERIC(15, 2)           NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_payments_loan_id ON payments (loan_id);