# Research: Elo Framework

## Objetivo

Registrar investigacoes, decisoes e alternativas avaliadas antes da implementacao.

Este arquivo apoia o processo de Spec-Driven Development e evita que decisoes tecnicas fiquem implicitas.

## Estado Atual do Projeto

O repositorio da Fase 2 contem uma aplicacao chamada Apto.

Estrutura principal:

- `backend/apto-api`: API Spring Boot.
- `frontend`: aplicacao React/Vite.
- `docker`: infraestrutura local.

Backend atual:

- Java 21.
- Spring Boot.
- Spring Web.
- Spring Data JPA.
- Bean Validation.
- PostgreSQL.
- H2 para testes.
- Lombok.
- RestClient para integracao opcional com Groq.

Frontend atual:

- React.
- Vite.
- TypeScript.
- Tailwind CSS.
- React Router.

## Elementos do Apto que ja correspondem ao framework

### Usuarios

O backend possui `Usuario` como entidade base e especializacoes como:

- `UsuarioUniversitario`;
- `Locador`.

Isso se alinha ao ponto fixo de cadastro e gestao de usuarios.

### Perfis

O backend possui:

- `PerfilConvivencia`;
- `PerfilAnunciante`.

`PerfilConvivencia` e o principal perfil usado para compatibilidade entre universitarios.

### Ofertas

O backend possui:

- `Anuncio`;
- `Moradia`.

`Anuncio` representa a publicacao de uma oferta no dominio de moradias.

### Interacoes

O backend possui:

- `ManifestacaoInteresse`.

Ela representa o registro de uma interacao entre usuario interessado e anuncio.

### Compatibilidade

O backend possui:

- `MatchmakingService`;
- `CompatibilidadeDeterministicaCalculator`;
- integracao opcional com Groq/LLM;
- fallback deterministico.

O match atual e principalmente entre dois `UsuarioUniversitario`, usando dados de `PerfilConvivencia`.

## Decisoes

### Decisao 1: Preservar o Apto como instancia original

Motivo:

- O Apto e o produto da Fase 1.
- A Fase 2 deve demonstrar evolucao, nao substituicao.
- O comportamento existente ja cobre muitos pontos fixos do framework.

Consequencia:

- Refatoracoes devem ser incrementais.
- Controllers e fluxos existentes devem ser preservados.

### Decisao 2: Nao tratar interacao como ponto variavel

Motivo:

- Orientacao do professor.
- O mecanismo de Manifestacao de Interesse e comum.
- O que muda por instancia e o tipo de oferta em que o usuario manifesta interesse.

Consequencia:

- Todas as instancias devem ser explicadas usando Manifestacao de Interesse como mecanismo fixo.
- Apto: interesse em moradia ou vaga.
- Study Buddy: interesse em grupo de estudo.
- Mentor Match: interesse em sessao ou programa de mentoria.

### Decisao 3: Usar estrategia para criterios de compatibilidade

Motivo:

- Compatibilidade e ponto fixo.
- Criterios de compatibilidade sao ponto variavel.
- Strategy permite trocar criterio sem alterar o fluxo geral.

Alternativas avaliadas:

- Heranca profunda: rejeitada por aumentar acoplamento.
- Condicionais por tipo de instancia: rejeitada por reduzir extensibilidade.
- Strategy ou interfaces simples: recomendada por ser direta e demonstravel.

### Decisao 4: Implementar Study Buddy e Mentor Match de forma minima

Motivo:

- Prazo curto.
- Objetivo academico e demonstrar instanciacao.
- Nao e necessario criar produtos completos.

Consequencia:

- As instancias podem focar em perfil, oferta e compatibilidade.
- Persistencia completa so deve ser adicionada se for necessaria para demonstracao.

## Perguntas de Clarificacao

1. As instancias Study Buddy e Mentor Match precisam ter endpoints REST completos?
2. As novas instancias precisam persistir dados no banco?
3. A demonstracao sera feita por testes, endpoints, seed de dados ou slides?
4. O professor espera um framework mais conceitual ou uma refatoracao profunda de codigo?
5. O frontend precisa demonstrar as novas instancias ou o foco sera backend?

## Recomendacao Atual

Seguir com uma implementacao pequena no backend:

- contratos minimos para os pontos fixos;
- Apto adaptado ou documentado como instancia original;
- Study Buddy e Mentor Match com estrategias proprias de compatibilidade;
- testes unitarios comprovando variacao de criterios.

Essa abordagem reduz risco e atende ao objetivo academico.

