CREATE TABLE IF NOT EXISTS patogeno (
    id bigint auto_increment NOT NULL ,
    tipo VARCHAR(255) NOT NULL UNIQUE,
    cantEspecies int,
    PRIMARY KEY (id)
);