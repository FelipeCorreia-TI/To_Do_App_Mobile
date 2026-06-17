create database to_do_list;
use to_do_list;

create table tarefas (
	id int auto_increment primary key,
    tarefa varchar(500) not null,
    descricao TEXT
);