# Apto

Plataforma de moradias universitárias desenvolvida para a disciplina de Projeto Detalhado de Software.

O projeto busca apoiar estudantes universitários na busca por moradias, vagas compartilhadas e colegas compatíveis para convivência, além de permitir a gestão de anúncios por locadores e mecanismos de denúncia, avaliação e reputação.

## Integrantes

- Iury
- Gabriel
- Matheus

## Estrutura

```text
.
├── backend/
│   └── apto-api/       # API Spring Boot
├── frontend/           # Aplicação web React/Vite
└── docker/             # Serviços auxiliares, como PostgreSQL
```

## Tecnologias

Backend:

- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- H2 para testes automatizados
- Lombok
- RestClient

Frontend:

- React
- Vite
- TypeScript
- Tailwind CSS
- React Router
- lucide-react

Infraestrutura:

- Docker
- Docker Compose

## Funcionalidades Implementadas

Backend:

- Cadastro, listagem, atualização, ativação/inativação e remoção de usuários universitários.
- Cadastro, listagem, atualização, ativação/inativação e remoção de locadores.
- Cadastro, listagem, atualização e remoção de moradias.
- Cadastro, listagem, busca paginada, atualização, alteração de status e remoção de anúncios.
- Filtros de busca de anúncios por características da moradia e valor.
- Manifestação de interesse em anúncios.
- Aceite, recusa e cancelamento de manifestações de interesse.
- Liberação de contato quando uma manifestação é aceita.
- Denúncias de anúncios.
- Moderação de denúncias com ação sobre o anúncio.
- Avaliação de locadores/moradias vinculada a anúncios.
- Cálculo de reputação de locadores com base nas avaliações ativas.
- Matchmaking entre estudantes com apoio de LLM/Groq e fallback determinístico.

Frontend:

- Interface web com páginas de início, busca, matches e perfil.
- Navegação com React Router.
- Componentes visuais reutilizáveis.
- Estado atual majoritariamente visual/mockado, ainda sem integração completa com a API.

## Como Rodar

### 1. Subir o Banco de Dados

```bash
cd docker
docker compose up -d
```

O PostgreSQL será iniciado com:

- Database: `apto`
- User: `apto`
- Password: `apto`
- Porta: `5432`

### 2. Rodar o Backend

```bash
cd backend/apto-api
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

### 3. Rodar o Frontend

```bash
cd frontend
npm install
npm run dev
```

Por padrão, o Vite exibirá a URL local no terminal, normalmente:

```text
http://localhost:5173
```

## Configuração

O backend usa PostgreSQL no ambiente local, configurado em:

```text
backend/apto-api/src/main/resources/application.yml
```

Configuração padrão:

- URL: `jdbc:postgresql://localhost:5432/apto`
- User: `apto`
- Password: `apto`

Também existe integração opcional com Groq para o matchmaking:

```bash
export GROQ_API_KEY=sua_chave_aqui
```

Sem chave configurada ou em caso de falha da integração, o sistema utiliza o cálculo determinístico de compatibilidade.

## Testes e Verificações

Backend:

```bash
cd backend/apto-api
./mvnw test
```

Frontend:

```bash
cd frontend
npm run lint
npm run build
```

## Observações de Projeto

- O projeto ainda não possui autenticação real.
- As permissões são simplificadas por IDs enviados em requests ou query params, como `anuncianteId`, `avaliadorId`, `solicitanteId` e `interessadoId`.
- O backend está mais completo que o frontend neste momento.
- O schema do banco é atualizado automaticamente pelo Hibernate em ambiente local.

## Status

Em desenvolvimento.
