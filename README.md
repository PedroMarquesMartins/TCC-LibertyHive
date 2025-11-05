# Projeto TCC - **LibertyHive**  
### Sistema Web com **Spring Boot**, **PostgreSQL** e **Frontend em JavaScript**

Este repositório compõe o **Trabalho de Conclusão de Curso (TCC 2025)** e apresenta o desenvolvimento de uma aplicação **web full-stack** baseada em **Java Spring Boot** no back-end, **PostgreSQL** como banco de dados e  **front-end** em **JavaScript + HTML + CSS**.

---

## ⚙️ Tecnologias Utilizadas

-  **Java 17** (Microsoft OpenJDK 17.0.15)  
-  **Spring Boot 2.7.8** (`spring-boot-maven-plugin` 2.7.0)  
-  **Hibernate 5.6.0.Final**  
-  **PostgreSQL Driver 42.5.0**  
-  **Maven** – Gerenciamento de dependências  
-  **JUnit 5** – Testes automatizados  
-  **DBeaver** / **pgAdmin 4** – Visualização e administração do banco  
-  **Live Server (VSCode)** – Servidor local para o frontend 
- IDEs: **IntelliJ IDEA Community** e **VSCode**

---

## Objetivo do Projeto

O **LibertyHive** é uma plataforma web voltada ao **escambo digital**, permitindo que usuários cadastrem produtos ou serviços e realizem trocas com outros membros da comunidade.

O sistema conta com:
- **API RESTful** desenvolvida em **Spring Boot** para o gerenciamento de usuários, postagens, propostas e mensagens.  
- **Banco de dados PostgreSQL** responsável pelo armazenamento seguro e consistente das informações.  
- **Interface web** desenvolvida com **HTML, CSS e JavaScript**, que se comunica com o backend por meio de **requisições HTTP (GET, POST, PUT, DELETE)**.  

---

## Modelo de Banco de Dados (PostgreSQL)

Abaixo está o **script SQL** utilizado para criação das tabelas principais do sistema.  
A estrutura é relacional, com chaves primárias, estrangeiras e regras de integridade referencial.

```sql
--Usuários cadastrados
CREATE TABLE IF NOT EXISTS cadastro (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    userNome VARCHAR(255),
    statusConta BOOLEAN DEFAULT TRUE,
    senha VARCHAR(255)
);

--Informações do escambista
CREATE TABLE IF NOT EXISTS escambista (
    id SERIAL PRIMARY KEY,
    userId INTEGER NOT NULL,
    userNome VARCHAR(255),
    nomeEscambista VARCHAR(255),
    contato VARCHAR(255),
    cpf VARCHAR(20),
    endereco VARCHAR(255),
    datanasc DATE,
    querNotifi BOOLEAN DEFAULT TRUE
);

--Postagens de produtos ou serviços
CREATE TABLE IF NOT EXISTS postagem (
    id SERIAL PRIMARY KEY,
    userId INTEGER NOT NULL,
    userNome VARCHAR(255),
    isProdOuServico BOOLEAN,
    isDoacao BOOLEAN,
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

--Sistema de propostas entre usuários
CREATE TABLE IF NOT EXISTS proposta (
    id SERIAL PRIMARY KEY,
    status INTEGER,
    userId01 INTEGER NOT NULL,
    userId02 INTEGER,
    itemDesejadoId INTEGER NOT NULL,
    itemOferecidoId INTEGER,
    dataHora TIMESTAMP DEFAULT NOW()
);

--Área de favoritos dos usuários
CREATE TABLE IF NOT EXISTS favorito (
    id SERIAL PRIMARY KEY,
    userId INTEGER NOT NULL,
    postagemId INTEGER NOT NULL
);

--Área de avaliações entre usuários
CREATE TABLE IF NOT EXISTS avaliacoes (
    id SERIAL PRIMARY KEY,
    usuario_avaliador_id BIGINT NOT NULL,
    usuario_avaliado_id BIGINT NOT NULL,
    proposta_id BIGINT NOT NULL,
    nota INTEGER NOT NULL CHECK (nota >= 1 AND nota <= 5),
    UNIQUE (usuario_avaliador_id, proposta_id)
);

--Controle de interações da AreaMATCH
CREATE TABLE IF NOT EXISTS area_match_vistos (
    id SERIAL PRIMARY KEY,
    userId INTEGER NOT NULL,
    postagemId INTEGER NOT NULL
);

--Estrutura de chat e mensagens privadas
CREATE TABLE IF NOT EXISTS chat (
    id SERIAL PRIMARY KEY,
    valorProposto NUMERIC(10,2),
    bloqueado BOOLEAN DEFAULT FALSE,
    userNome01 VARCHAR(255),
    userNome02 VARCHAR(255),
    userId01 INTEGER,
    userId02 INTEGER
);

CREATE TABLE IF NOT EXISTS mensagem (
    id SERIAL PRIMARY KEY,
    chatId INTEGER NOT NULL,
    userId INTEGER NOT NULL,
    mensagem TEXT,
    dataHora TIMESTAMP DEFAULT NOW()
);

```

## SOBRE


Projeto: LibertyHive

Disciplina: Trabalho de Conclusão de Curso 2025

Autores: João Pedro Marques Martins, Adrian de Almeida Polga

Instituição: Unigran

Professor Orientador: Antônio Pires de Almeida Junior
