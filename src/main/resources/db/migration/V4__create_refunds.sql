CREATE TABLE IF NOT EXISTS refunds
(
    id BIGSERIAL PRIMARY KEY,
    receipt_id INTEGER REFERENCES receipts(id),
    created_at timestamp,
    status varchar(20) NOT NULL,
    UNIQUE(id, receipt_id)
);

comment on table refunds is 'Возвраты';
comment on column refunds.id is 'id';
comment on column refunds.receipt_id is 'id платежа';
comment on column refunds.created_at is 'дата создания';
comment on column refunds.status is 'статус возврата';