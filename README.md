# Elo Framework

Projeto academico da disciplina Projeto Detalhado de Software.

O repositorio contem o **Elo Framework**, um nucleo Spring/JPA para plataformas baseadas em usuarios, perfis, ofertas, manifestacoes de interesse, denuncias, moderacao e matching. O framework e instanciado hoje por tres dominios:

- **Apto**: moradias universitarias e vagas em moradia.
- **Study Buddy**: grupos de estudo.
- **Mentor Match**: sessoes e solicitacoes de mentoria.

## Integrantes

- Iury Fredson Germano Miranda
- Gabriel Eugenio Vitalino da Silva
- Matheus Henrique Ferreira da Silva

## Estrutura

```text
.
|-- backend/
|   |-- elo-core/       # Nucleo reutilizavel do Elo Framework
|   |-- apto/           # Instancia Apto, API Spring Boot
|   |-- study-buddy/    # Instancia Study Buddy, API Spring Boot
|   `-- mentor-match/   # Instancia Mentor Match, API Spring Boot
|-- frontend/           # Frontend React/Vite do Apto
|-- frontend-study-buddy/
|-- frontend-mentor-match/
|-- docker/             # PostgreSQL local
`-- specs/              # Especificacoes, planos, contratos e diagramas
```

## Stack

- Java 21
- Spring Boot 4.0.4
- Maven multi-module
- Spring Web, Validation, Data JPA e RestClient
- PostgreSQL 16 em desenvolvimento local
- H2 nos testes de Apto e Study Buddy
- React 19, Vite 8, TypeScript, React Router e Tailwind CSS 4 nos frontends

## Arquitetura

As instancias dependem do nucleo, e o nucleo nao depende das instancias:

```text
apto         -> elo-core
study-buddy  -> elo-core
mentor-match -> elo-core
```

O `elo-core` define contratos, templates e implementacoes reutilizaveis para os fluxos comuns. Cada instancia fornece entidades, DTOs, repositories, mappers, controllers, excecoes e regras especificas do seu dominio.

Principais pontos do nucleo:

- `Usuario` e `UsuarioService`
- `Perfil` e `PerfilService`
- `Oferta` e `OfertaService`
- `ManifestacaoInteresse` e `ManifestacaoInteresseService`
- `Denuncia`, `CriterioDenuncia` e `DenunciaService`
- `ModeracaoService`
- `CompatibilidadeStrategy` e `MatchingService`
- `ProvedorCompatibilidadeLlm`
- cliente base Groq em `com.elo.compatibilidade.llm.groq`

## Instancias

### Apto

Dominio de moradias universitarias.

- Usuarios universitarios e locadores
- Perfil de convivencia e perfil anunciante
- Moradias e anuncios
- Busca paginada e filtrada de ofertas
- Manifestacoes de interesse
- Denuncias e moderacao de anuncios
- Avaliacoes e reputacao de anunciantes
- Matching com Groq e fallback deterministico
- Diagnostico Groq em `GET /diagnostico/groq`

Rotas principais:

```text
/usuarios/universitarios
/usuarios/locadores
/usuarios/{id}/perfil
/perfis-anunciante
/moradias
/ofertas
/ofertas/busca
/manifestacoes
/denuncias
/moderacoes/denuncias
/avaliacoes
/reputacao
/matching
/diagnostico/groq
```

### Study Buddy

Dominio de grupos de estudo.

- Estudantes
- Perfil academico
- Grupos de estudo como ofertas
- Manifestacoes de interesse em grupos
- Denuncias e moderacao de grupos
- Matching academico com Groq e fallback deterministico

Rotas principais:

```text
/study-buddy/usuarios
/study-buddy/ofertas
/study-buddy/manifestacoes
/study-buddy/denuncias
/study-buddy/moderacoes/denuncias
/study-buddy/matching
```

### Mentor Match

Dominio de mentorias.

- Alunos e mentores
- Perfis de mentoria
- Sessoes de mentoria
- Solicitacoes e participantes
- Denuncias e moderacao de sessoes
- Matching de mentores com Groq e fallback deterministico

Rotas principais:

```text
/mentor-match/alunos
/mentor-match/mentores
/mentor-match/sessoes
/mentor-match/sessoes/busca
/mentor-match/solicitacoes
/mentor-match/denuncias
/mentor-match/moderacoes/denuncias
/mentor-match/matching
```

## Frontends

Cada instancia tem um frontend React/Vite separado:

| Pasta | Dominio | Backend padrao |
| --- | --- | --- |
| `frontend` | Apto | `http://localhost:8080` |
| `frontend-study-buddy` | Study Buddy | `http://localhost:8080` |
| `frontend-mentor-match` | Mentor Match | `http://localhost:8082` |

Todos aceitam `VITE_API_URL` para apontar para outro backend.

## Configuracao Local

### Banco de dados

Suba o PostgreSQL local:

```powershell
cd docker
docker compose up -d
```

Configuracao padrao do container:

```text
Database: apto
User: apto
Password: apto
Porta: 5432
```

### Groq

As integracoes LLM usam a API Groq no formato OpenAI-compatible. Configure a chave por variavel de ambiente:

```powershell
$env:GROQ_API_KEY="sua_chave_aqui"
```

No Linux/macOS:

```bash
export GROQ_API_KEY=sua_chave_aqui
```

Sem chave, ou em caso de falha na chamada externa, os fluxos de matching usam fallback deterministico quando aplicavel. No Apto, tambem existe a rota de diagnostico `GET /diagnostico/groq`.

## Como Rodar

Execute os comandos a partir de `backend`.

### Apto

```powershell
cd backend
.\mvnw.cmd -pl apto spring-boot:run
```

API padrao:

```text
http://localhost:8080
```

### Study Buddy

Por padrao tambem sobe na porta `8080`. Se for rodar junto com Apto, informe outra porta. Como o `application.properties` principal do Study Buddy declara apenas as propriedades da Groq, informe tambem o datasource se ele nao vier de outro profile:

```powershell
cd backend
.\mvnw.cmd -pl study-buddy -Dspring-boot.run.arguments="--server.port=8081 --spring.datasource.url=jdbc:postgresql://localhost:5432/apto --spring.datasource.username=apto --spring.datasource.password=apto --spring.datasource.driver-class-name=org.postgresql.Driver --spring.jpa.hibernate.ddl-auto=update" spring-boot:run
```

API com o comando acima:

```text
http://localhost:8081/study-buddy
```

### Mentor Match

```powershell
cd backend
.\mvnw.cmd -pl mentor-match spring-boot:run
```

API padrao:

```text
http://localhost:8082/mentor-match
```

### Frontend Apto

```powershell
cd frontend
npm install
npm run dev
```

### Frontend Study Buddy

```powershell
cd frontend-study-buddy
npm install
npm run dev
```

Se o backend estiver em outra porta:

```powershell
$env:VITE_API_URL="http://localhost:8081"
npm run dev
```

### Frontend Mentor Match

```powershell
cd frontend-mentor-match
npm install
npm run dev
```

## Build e Testes

Backend completo:

```powershell
cd backend
.\mvnw.cmd clean install
```

Modulo especifico com dependencias:

```powershell
cd backend
.\mvnw.cmd clean install -pl elo-core,apto -am
```

Testes:

```powershell
cd backend
.\mvnw.cmd test
```

Frontends:

```powershell
npm run lint
npm run typecheck
npm run build
```

Rode esses comandos dentro de `frontend`, `frontend-study-buddy` ou `frontend-mentor-match`.
