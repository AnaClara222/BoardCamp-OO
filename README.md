# 🎲 BoardCamp OO

API REST desenvolvida em **Java**, utilizando **Programação Orientada a Objetos**, com foco em **arquitetura em camadas, regras de negócio complexas e boas práticas de back-end**.

O sistema simula a gestão de uma locadora de jogos de tabuleiro, permitindo o controle de jogos, clientes e aluguéis.

## ⚙️ Funcionalidades

### Jogos
- Cadastro de jogos
- Listagem de jogos
- Validações de estoque e duplicidade

### Clientes
- Cadastro de clientes
- Listagem de clientes
- Busca por ID
- Validação de CPF único

### Aluguéis
- Criação de aluguéis
- Controle de estoque disponível
- Finalização de aluguel
- Cálculo automático de preço e multa por atraso
- Exclusão de aluguéis finalizados

## 🧠 Regras de Negócio
- Validação de dados de entrada
- Controle de estoque disponível
- Cálculo automático de:
  - preço total do aluguel
  - multa por atraso
- Tratamento de erros centralizado com `GlobalExceptionHandler`

## 🛠 Tecnologias Utilizadas

- Java
- Spring Boot
- PostgreSQL
- JPA / Hibernate
- Testes unitários e de integração
- Arquitetura em camadas (Controllers, Services, Repositories, DTOs)

## 🎯 Objetivo do Projeto

Projeto desenvolvido para praticar:
- Programação Orientada a Objetos
- organização de código
- regras de negócio reais
- construção de APIs REST robustas

---

💡 Projeto com fins educacionais, focado em back-end.
