CREATE TABLE loans
(
    id                UUID PRIMARY KEY,
    user_id           UUID                     NOT NULL,
    total_amount      NUMERIC(15, 2)           NOT NULL,
    remaining_amount  NUMERIC(15, 2)           NOT NULL,
    next_payment_date TIMESTAMP WITH TIME ZONE NOT NULL,
    status            VARCHAR(20)              NOT NULL,
    created_at        TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_loans_user_id ON loans (user_id);
