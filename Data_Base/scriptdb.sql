create database to_do_list;
use to_do_list;

create table tarefas (
    id int auto_increment primary key,
    titulo varchar(500) not null,
    descricao text,
   concluido boolean not null default FALSE
);