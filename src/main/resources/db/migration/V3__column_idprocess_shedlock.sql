alter table if exists shedlock add column process_id varchar(30);
comment on column shedlock.process_id is 'id процесса';