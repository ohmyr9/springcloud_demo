create table if not exists `order`
(
    id         int primary key auto_increment,
    user_id    int          not null,
    status     varchar2(10) not null,
    created_at timestamp default now(),
    updated_at timestamp default now() on update now()
);
create table if not exists order_line_item
(
    id         int primary key auto_increment,
    product_id int not null,
    quantity   int not null,
    order_id   int not null
);