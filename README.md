# Apto / Elo Framework

Projeto acadêmico da disciplina Projeto Detalhado de Software.

A Fase 2 evolui a aplicação **Apto** para o **Elo Framework**, um framework Spring/JPA híbrido para plataformas baseadas em usuários, perfis, ofertas, Manifestação de Interesse e compatibilidade.

O núcleo reutilizável fica em `backend/elo-core`. A aplicação Apto fica em `backend/apto` e instancia o framework no domínio de moradias universitárias. A aplicação Study Buddy fica em `backend/study-buddy` e instancia o mesmo framework no domínio de grupos de estudo.

## Integrantes

- Iury Fredson Germano Miranda
- Gabriel Eugenio Vitalino da Silva
- Matheus Henrique Ferreira da Silva

## Estrutura

```text
.
├── backend/
│   ├── elo-core/       # Núcleo do Elo Framework
│   ├── apto/           # Instância Apto e API Spring Boot
│   └── study-buddy/    # Instância Study Buddy e API Spring Boot
├── frontend/           # Aplicação web React/Vite do Apto
├── specs/              # Especificações, contratos, modelo, tarefas e diagrama
└── docker/             # Serviços auxiliares, como PostgreSQL
```

## Arquitetura

A dependência é unidirecional:

```text
apto -> elo-core
study-buddy -> elo-core
```

`elo-core` define contratos, estados, portas e Template Methods. Ele não referencia classes de `com.apto` ou `com.studybuddy`.

Cada instância fornece entidades, DTOs, repositories, mappers, controllers, exceções e regras específicas.

## Núcleo do Framework

O `elo-core` controla os fluxos fixos:

- gestão de usuários;
- gestão de perfis;
- publicação e gestão de ofertas;
- Manifestação de Interesse;
- denúncia;
- moderação;
- compatibilidade e matching.

Contratos e templates principais:

- `Usuario` e `UsuarioService`;
- `Perfil` e `PerfilService`;
- `Oferta` e `OfertaService`;
- `ManifestacaoInteresse` e `ManifestacaoInteresseService`;
- `Denuncia`, `CriterioDenuncia` e `DenunciaService`;
- `ModeracaoService`;
- `CompatibilidadeStrategy`;
- `MatchingService`;
- `ProvedorCompatibilidadeLlm`.

## Apto Como Instância

O Apto instancia os pontos flexíveis do framework:

| Ponto flexível | Instância Apto |
| --- | --- |
| Dados do perfil | `PerfilConvivencia` |
| Tipo de oferta publicada | `Anuncio` associado a `Moradia` |
| Critérios de compatibilidade | `CompatibilidadeDeterministicaCalculator` |
| Critério de denúncia | `CriterioDenunciaApto` |
| Integração LLM | `AptoCompatibilidadeLlmProvider`, Groq, prompt e parser |

Manifestação de Interesse é ponto fixo. No Apto, ela representa interesse em um anúncio de moradia ou vaga.

## Study Buddy Como Instância

O Study Buddy instancia os pontos flexíveis do framework:

| Ponto flexível | Instância Study Buddy |
| --- | --- |
| Dados do perfil | `PerfilAcademico` |
| Tipo de oferta publicada | `GrupoEstudo` |
| Critérios de compatibilidade | `CompatibilidadeAcademicaCalculator` |
| Integração LLM | `StudyBuddyCompatibilidadeLlmProvider` sem LLM obrigatória |

Manifestação de Interesse continua sendo ponto fixo. No Study Buddy, ela representa interesse em um grupo de estudo.

## Funcionalidades do Apto

Backend:

- cadastro e gestão de usuários universitários;
- cadastro e gestão de locadores;
- perfil de convivência;
- perfil anunciante;
- moradias;
- anúncios;
- busca paginada e filtrada de anúncios;
- Manifestação de Interesse;
- aceite, recusa e cancelamento de manifestações;
- denúncia e moderação de anúncios;
- avaliação e reputação de anunciantes;
- matchmaking com LLM/Groq e fallback determinístico.

Frontend:

- login/cadastro simplificados para contexto acadêmico;
- sessão local em `localStorage`;
- busca e detalhes de anúncios;
- criação e gestão de anúncios;
- manifestações de interesse;
- matchmaking;
- avaliação;
- denúncias e moderação.

## Decisões de Projeto

- Study Buddy foi implementado como segunda instância concreta do framework.
- Mentor Match aparece apenas como exemplo futuro de extensibilidade.
- Avaliação e reputação continuam específicas do Apto.
- O mecanismo de Observer/Event Publisher foi removido.
- Cancelamento de manifestações e recálculo de reputação agora são chamadas diretas entre services.
- Autenticação real e deploy de produção estão fora do escopo.

## Documentação

Arquivos principais:

- `specs/elo-framework/spec.md`
- `specs/elo-framework/contracts.md`
- `specs/elo-framework/data-model.md`
- `specs/elo-framework/plan.md`
- `specs/elo-framework/tasks.md`
- `specs/elo-framework/traceability.md`
- `specs/elo-framework/diagrams/elo-framework-apto-class-diagram.puml`

## Como Rodar

### Banco de Dados

```bash
cd docker
docker compose up -d
```

Configuração padrão:

- Database: `apto`
- User: `apto`
- Password: `apto`
- Porta: `5432`

### Backend

```bash
cd backend
./mvnw -pl apto spring-boot:run
```

API:

```text
http://localhost:8080
```

Study Buddy:

```bash
cd backend
./mvnw -pl study-buddy spring-boot:run
```

Rotas da instância:

```text
/study-buddy
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

URL local padrão do Vite:

```text
http://localhost:5173
```

## Configuração Groq

A integração com Groq é opcional:

```bash
export GROQ_API_KEY=sua_chave_aqui
```

Sem chave ou em caso de falha, o matching usa fallback determinístico.

## Testes

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

## Status

Apto e Study Buddy estão instanciados no Elo Framework.

As etapas 1 a 9 do plano de evolução do framework e do plano Study Buddy foram executadas. A Etapa 09 atualiza documentação e valida a arquitetura final.
