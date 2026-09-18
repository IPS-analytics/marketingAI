create table if not exists users (
    id              bigint          primary key,
    user_name       varchar(255)    not null default '',
    user_surname    varchar(255)    not null default '',
    password        varchar(255)    not null default '',
    email           varchar(255)    not null unique
);

create table if not exists messages (
    id				bigint			primary key,
    id_chat			bigint			not null,
    message_type	varchar(255)	not null,
    message_text	text			not null
);

create table if not exists chats (
    id				bigint			primary key,
    id_user			bigint			not null,
    chat_name		varchar(255)	not null
);

create sequence if not exists chat_seq start 1 increment 1;
create sequence if not exists message_seq start 1 increment 1;
create sequence if not exists user_seq start 1 increment 1;
