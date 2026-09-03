CREATE TABLE loans
(
    id                UUID PRIMARY KEY,
    user_id           UUID                     NOT NULL,
    total_amount      NUMERIC(15, 2)           NOT NULL,
    remaining_amount  NUMERIC(15, 2)           NOT NULL,
    next_payment_date TIMESTAMP WITH TIME ZONE,
    status            VARCHAR(20)              NOT NULL,
    term_months       INTEGER                  NOT NULL,
    interest_rate     NUMERIC(5, 2)            NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_loans_user_id ON loans (user_id);
CREATE INDEX idx_loans_status ON loans (status);
CREATE INDEX idx_loans_user_status ON loans (user_id, status);

ALTER TABLE loans
    ADD CONSTRAINT chk_loans_status CHECK (status IN ('ACTIVE', 'CLOSED', 'OVERDUE'));