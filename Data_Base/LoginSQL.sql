create table usuarios(
	id_user int auto_increment primary key,
    email varchar(100) not null UNIQUE,
    senha varchar(50) not null
);