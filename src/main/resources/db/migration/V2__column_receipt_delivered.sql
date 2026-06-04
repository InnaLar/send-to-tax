alter table if exists receipts add column status varchar(20);
alter table if exists receipts add column attempts int default 0;
comment on column receipts.status is 'Статус';
comment on column receipts.attempts is 'Количество попыток';