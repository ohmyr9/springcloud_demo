create table if not exists product
(
    id    int primary key auto_increment,
    name  varchar2       not null,
    price decimal(10, 2) not null
);
create table if not exists inventory
(
    id         int primary key auto_increment,
    product_id int unique not null,
    quantity   int        not null,
    version    int        not null
);
create table if not exists inventory_change_log
(
    id         int primary key auto_increment,
    request_id varchar2(36) unique,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now()
);

create table if not exists inventory_change_item_log
(
    id         int primary key auto_increment,
    log_id      int not null,
    product_id int not null,
    quantity   int not null
);
create index if not exists idx_detail_request_id on inventory_change_item_log (log_id);
create index if not exists idx_detail_product_id on inventory_change_item_log (product_id);
-- test product
insert into product(name, price)
values ('phone', 2000.5);
insert into product(name, price)
values ('watch', 500);
insert into inventory(product_id, quantity, version)
values (1, 100, 1);
insert into inventory(product_id, quantity, version)
values (2, 200, 1);

