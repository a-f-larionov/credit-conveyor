CREATE TABLE credit
(

    id               UUID PRIMARY KEY,
    user_id          UUID                     NOT NULL,
    user_full_name   VARCHAR(100)             NOT NULL,
    requested_amount NUMERIC(15, 2)           NOT NULL,
    term_months      INTEGER                  NOT NULL,
    status           VARCHAR(50)              NOT NULL,
    creation_date    TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated     TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_credit_user_id ON credit (user_id);