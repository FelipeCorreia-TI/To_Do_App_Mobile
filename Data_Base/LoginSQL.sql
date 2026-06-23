create database to_do_list;
use to_do_list;



CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(12) NOT NULL
);


