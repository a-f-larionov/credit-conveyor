CREATE TABLE email_outbox
(
    id         UUID PRIMARY KEY,
    recipient  VARCHAR(254)             NOT NULL,
    subject    VARCHAR(998)             NOT NULL DEFAULT '',
    body       TEXT                     NOT NULL DEFAULT '',
    status     VARCHAR(32)              NOT NULL,
    last_error VARCHAR(1000),
    sent_at    TIMESTAMP WITH TIME ZONE,
    created    TIMESTAMP WITH TIME ZONE NOT NULL
);