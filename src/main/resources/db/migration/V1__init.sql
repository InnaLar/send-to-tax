CREATE TABLE IF NOT EXISTS receipts
(
    id        BIGSERIAL PRIMARY KEY,
    processed BOOLEAN DEFAULT FALSE,
    sum       VARCHAR(20),
    source    VARCHAR(10)
);
comment on table receipts is 'Платежки';
comment on column receipts.id is 'id';
comment on column receipts.processed is 'Признак обработки';
comment on column receipts.sum is 'Сумма платежки';
comment on column receipts.source is 'Назначение платежа';

CREATE TABLE IF NOT EXISTS shedlock
(
    id BIGSERIAL PRIMARY KEY,
    name varchar(50) UNIQUE,
    start_time timestamp,
    status varchar(20) NOT NULL
);

comment on table shedlock is 'блокирование вызовов методов';
comment on column shedlock.id is 'id метода';
comment on column shedlock.start_time is 'время начала блокировки метода';
comment on column shedlock.status is 'статус блокировки';

INSERT INTO Shedlock(name, status) VALUES('processReceipt', 'READY_TO_WORK') ON CONFLICT DO NOTHING;