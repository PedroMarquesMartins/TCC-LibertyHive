# 📚 Projeto LibertyHive – Spring Boot + PostgreSQL + Frontend

Este repositório faz parte do **TCC 2025** da graduação e tem como objetivo demonstrar a estrutura de um sistema **web + backend** com **Java Spring Boot**, banco de dados **PostgreSQL**, e frontend simples em **HTML + JavaScript**, tudo integrado e funcional.

---

## ⚙️ Tecnologias Utilizadas

- **Java 17 (Microsoft OpenJDK 17.0.15)**
- **Spring Boot 2.7.8** (com `spring-boot-maven-plugin` 2.7.0)
- **Hibernate 5.6.0.Final**
- **PostgreSQL Driver 42.5.0**
- **Maven** para gerenciamento de dependências
- **JUnit 5** para testes automatizados
- **DBEaver** / **pgAdmin** para visualização/manipulação do banco
- IDEs: **IntelliJ Community**, **VSCode**

---

## 📌 Objetivo

A API Spring Boot gerencia requisições com nome, email, usuário e senha. Os dados são persistidos em um banco PostgreSQL. Um formulário simples HTML faz requisições HTTP para o backend.

---

## Arquivo SQL do Banco de Dados PostgreSQL

CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    categoria VARCHAR(255),
    numero INTEGER
);

CREATE TABLE chat (
    id SERIAL PRIMARY KEY,
    mensagem TEXT,
    valorProposto REAL,
    bloqueado BOOLEAN
);

CREATE TABLE escambista (
    id SERIAL PRIMARY KEY,
    userNome VARCHAR(255),
    nomeEscambista VARCHAR(255),
    avaliacao INTEGER,
    contato VARCHAR(255),
    email VARCHAR(255),
    endereco VARCHAR(255)
);

CREATE TABLE login (
    id SERIAL PRIMARY KEY,
    userNome VARCHAR(255),
    senha VARCHAR(255)
);

CREATE TABLE postagem (
    id SERIAL PRIMARY KEY,
    userNome VARCHAR(255),
    nomePostagem VARCHAR(255),
    descricao TEXT,
    categoria VARCHAR(255),
    cep VARCHAR(255),
    imagem BYTEA
);

CREATE TABLE proposta (
    id SERIAL PRIMARY KEY,
    status INTEGER,
    itemDesejado VARCHAR(255),
    itemOferecido VARCHAR(255),
    avaliarPerfil INTEGER
);
--Ok
CREATE table if not EXISTS cadastro (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255),
    userNome VARCHAR(255),
    senha VARCHAR(255)
); --OK

---

SOBRE


Projeto: LibertyHive

Disciplina: Trabalho de Conclusão de Curso 2025

Autores: João Pedro Marques Martins, Adrian de Almeida Polga

Instituição: Unigran

Professor Orientador: Antônio Pires de Almeida Junior
