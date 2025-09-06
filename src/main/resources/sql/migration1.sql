CREATE TYPE priority AS ENUM ('Low', 'Medium', 'High', 'Vital');

drop table if exists task;

create table task
(
    id          integer generated always as identity,
    name        varchar,
    description varchar,
    priority    priority,
    completed   boolean
);
insert into public.task (name, description, priority, completed)
values ('watch nfl', 'Order Redzone At 12:45pm', 'High', false);
