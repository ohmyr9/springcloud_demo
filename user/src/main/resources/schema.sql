create table if not exists user_account
(
    id       int primary key auto_increment,
    username varchar2(255) unique not null,
    password varchar2(127)        not null
);
create table if not exists user_role
(
    id       int primary key auto_increment,
    user_id  int not null,
    role_key varchar2(10) not null
);
create index if not exists idx_user_role on user_role (user_id, role_key);
create table if not exists role
(
    id       int primary key auto_increment,
    role_key varchar2(10) unique
);

insert into role(role_key)
values ('ROLE_USER');
insert into role(role_key)
values ('ROLE_ADMIN');
-- test users
-- 1. user: password 2. admin:admin
insert into user_account(username, password)
values ('user', '{bcrypt}$2a$10$Me7cdvvw0MGjL0BI/clQE.KEu1wc9H0PWVK0cOaEXRnIyQ5/eH0GS');
insert into user_account(username, password)
values ('admin', '{bcrypt}$2a$10$sgqZcp71wd6X72L38/GGheUbtsQYiinbJArD9gadIu/26gxGBfq.q');

insert into user_role(user_id,role_key) values ( 1,'ROLE_USER' );
insert into user_role(user_id,role_key) values ( 2,'ROLE_USER' );
insert into user_role(user_id,role_key) values ( 2,'ROLE_ADMIN' );

