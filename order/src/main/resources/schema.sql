create table if not exists `order`
(
    id         varchar2(36) primary key,
    user_id    int          not null,
    status     varchar2(10) not null,
    version   int not null,
    created_at timestamp default now(),
    updated_at timestamp default now() on update now()
);
create table if not exists order_line_item
(
    id         int primary key auto_increment,
    product_id int          not null,
    quantity   int          not null,
    order_id   varchar2(36) not null
);

create table if not exists order_change_transaction
(
    id       varchar2(36) primary key,
    order_id varchar2(36) not null,
    version   int not null,

    status   varchar2(10) not null
)