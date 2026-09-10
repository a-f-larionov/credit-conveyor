CREATE TABLE schedule_payments
(
    id               UUID PRIMARY KEY,
    loan_id          UUID                     NOT NULL REFERENCES loans (id) ON DELETE CASCADE,
    user_id          UUID                     NOT NULL,
    number           INTEGER                  NOT NULL CHECK (number > 0),
    date             TIMESTAMP WITH TIME ZONE NOT NULL,
    interest_amount  NUMERIC(15, 2)           NOT NULL,
    principal_amount NUMERIC(15, 2)           NOT NULL,
    remain_amount    NUMERIC(15, 2)           NOT NULL,
    status           VARCHAR(20)              NOT NULL,
    done_date        TIMESTAMP WITH TIME ZONE,
    overdue_days     INTEGER                  NOT NULL DEFAULT 0,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_schedule_payments_loan_id ON schedule_payments (loan_id);
CREATE INDEX idx_schedule_payments_user_id ON schedule_payments (user_id);
CREATE INDEX idx_schedule_payments_status ON schedule_payments (status);
CREATE INDEX idx_schedule_payments_overdue_days ON schedule_payments (overdue_days);

ALTER TABLE schedule_payments
    ADD CONSTRAINT chk_schedule_payments_status
        CHECK (status IN ('PENDING', 'DONE', 'OVERDUE'));