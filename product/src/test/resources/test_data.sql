-- test product
insert into product(name, price)
values ('phone', 2000.5);
insert into product(name, price)
values ('watch', 500);
insert into inventory(product_id, quantity, version)
values (1, 100, 1);
insert into inventory(product_id, quantity, version)
values (2, 200, 1);