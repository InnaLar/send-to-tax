CREATE TABLE IF NOT EXISTS outbox
(
    id BIGSERIAL PRIMARY KEY,
    receipt_id BIGINT NOT NULL,
    sent_at timestamp,
    status varchar(20) NOT NULL
);