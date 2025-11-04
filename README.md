# 📚 Projeto LibertyHive – Spring Boot + PostgreSQL + Frontend

Este repositório faz parte do **TCC 2025** da graduação e tem como objetivo demonstrar a estrutura de um sistema **web + backend** com **Java Spring Boot**, banco de dados **PostgreSQL**, e frontend simples em **HTML + JavaScript**.

---

## ⚙️ Tecnologias Utilizadas

- **Java 17 (Microsoft OpenJDK 17.0.15)**
- **Spring Boot 2.7.8** (com `spring-boot-maven-plugin` 2.7.0)
- **Hibernate 5.6.0.Final**
- **PostgreSQL Driver 42.5.0**
- **Maven** para gerenciamento de dependências
- **JUnit 5** para testes automatizados
- **DBEaver** / **pgAdmin** para visualização/manipulação do banco
- **Live Server (VSCode)** para servir o frontend local
- IDEs: **IntelliJ Community**, **VSCode**

---

## 📌 Objetivo

A API Spring Boot gerencia requisições com nome, email, usuário e senha. Os dados são persistidos em um banco PostgreSQL. Um formulário simples HTML faz requisições HTTP para o backend.

---

## Arquivo SQL do Banco de Dados PostgreSQL
CREATE TABLE IF NOT EXISTS escambista (
    id SERIAL PRIMARY KEY NOT NULL,
    userId INTEGER NOT NULL,
    userNome VARCHAR(255),
    nomeEscambista VARCHAR(255),
    contato VARCHAR(255),
    cpf VARCHAR(20),
    endereco VARCHAR(255),
    datanasc DATE,
    querNotifi BOOLEAN DEFAULT TRUE
);

CREATE TABLE avaliacoes (
    id SERIAL PRIMARY KEY,
    usuario_avaliador_id BIGINT NOT NULL,
    usuario_avaliado_id BIGINT NOT NULL,
    proposta_id BIGINT NOT NULL,
    nota INTEGER NOT NULL CHECK (nota >= 1 AND nota <= 5),
    UNIQUE (usuario_avaliador_id, proposta_id)
);

create table if not exists favorito(
	id SERIAL PRIMARY key not null,
    userId INTEGER not null,
    postagemId INTEGER not null
);

CREATE TABLE IF NOT EXISTS postagem (
    id SERIAL PRIMARY KEY NOT NULL,
    userId INTEGER NOT NULL,
    userNome VARCHAR(255),
    isProdOuServico BOOL,
    isDoacao BOOL,
    nomePostagem VARCHAR(255),
    descricao TEXT,
    categoria VARCHAR(255),
    disponibilidade BOOLEAN,
    categoriaInteresse1 VARCHAR(255),
    categoriaInteresse2 VARCHAR(255),
    categoriaInteresse3 VARCHAR(255),
    cidade VARCHAR(255),
    uf VARCHAR(255),
    imagem BYTEA,
    imagemS01 BYTEA,
    imagemS02 BYTEA,
    imagemS03 BYTEA,
    imagemS04 BYTEA,
    imagemS05 BYTEA
);

CREATE TABLE if not exists proposta (
    id SERIAL PRIMARY key not null,
    status INTEGER,
    userId01 INTEGER not null,
    userId02 INTEGER, 
    itemDesejadoId INTEGER not null,
    itemOferecidoId INTEGER,
    dataHora TIMESTAMP DEFAULT NOW()
);

CREATE table if not EXISTS cadastro (
    id SERIAL PRIMARY key not null,
    email VARCHAR(255),
    userNome VARCHAR(255),
    statusConta BOOLEAN DEFAULT true,
    senha VARCHAR(255)
); 

CREATE table if not EXISTS area_match_vistos(
    id SERIAL PRIMARY KEY,
    userId INTEGER NOT NULL,           
    postagemId INTEGER NOT NULL       
);

CREATE TABLE IF NOT EXISTS chat (
    id SERIAL PRIMARY KEY NOT NULL,
    valorProposto NUMERIC(10,2),
    bloqueado BOOLEAN DEFAULT FALSE,
    userNome01 VARCHAR(255),
    userNome02 VARCHAR(255),
    userId01 INTEGER,
    userId02 INTEGER
);

CREATE TABLE IF NOT EXISTS mensagem (
    id SERIAL PRIMARY KEY NOT NULL,
    chatId INTEGER NOT NULL,
    userId INTEGER NOT NULL,
    mensagem TEXT,
    dataHora TIMESTAMP DEFAULT NOW()
);

---

SOBRE


Projeto: LibertyHive

Disciplina: Trabalho de Conclusão de Curso 2025

Autores: João Pedro Marques Martins, Adrian de Almeida Polga

Instituição: Unigran

Professor Orientador: Antônio Pires de Almeida Junior
