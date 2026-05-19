# Apto

Plataforma de moradias universitárias desenvolvida para a disciplina de Projeto Detalhado de Software.

O projeto busca apoiar estudantes universitários na busca por moradias, vagas compartilhadas e colegas compatíveis para convivência. A aplicação também permite que locadores e universitários com papel de anunciante publiquem anúncios, recebam manifestações de interesse, sejam avaliados e tenham reputação consolidada, além de contar com mecanismos de denúncia e moderação.

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
- Spring Boot 4
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- H2 para testes automatizados
- Lombok
- RestClient
- Integração opcional com Groq/LLM para matchmaking

Frontend:

- React 19
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
- Perfil de convivência para universitários, criado/atualizado sob demanda.
- Papel de anunciante por meio de `PerfilAnunciante`.
- Criação automática de perfil anunciante para locadores.
- Habilitação/desabilitação de perfil anunciante para universitários.
- Cadastro, listagem, atualização e remoção de moradias.
- Cadastro, listagem, busca paginada, atualização, alteração de status e remoção de anúncios.
- Filtros de busca de anúncios por características da moradia, valor e tipo de anúncio.
- Manifestação de interesse em anúncios.
- Aceite, recusa e cancelamento de manifestações de interesse.
- Liberação de contato quando uma manifestação é aceita.
- Denúncias de anúncios.
- Moderação de denúncias com ação sobre o anúncio.
- Avaliação de anunciantes/moradias vinculada a anúncios.
- Cálculo de reputação de anunciantes com base nas avaliações ativas.
- Matchmaking entre estudantes com apoio de LLM/Groq e fallback determinístico.
- CORS configurado para o frontend local.

Frontend:

- Login/cadastro simplificados para o contexto acadêmico.
- Sessão local em `localStorage`, sem autenticação real por token.
- Navegação com React Router e rotas protegidas por tipo de usuário.
- Páginas de início, busca, detalhes de anúncio, criação de anúncio, meus anúncios, interesses, interesses recebidos, matchmaking, perfil, perfil de convivência, perfil público de anunciante e moderação.
- Camada de API integrada ao backend via `fetch`.
- Componentes visuais reutilizáveis.

## Domínio Atual

O conceito central para publicação e reputação é o `PerfilAnunciante`.

- `Locador` é um tipo de usuário anunciante por padrão.
- `UsuarioUniversitario` pode habilitar o papel de anunciante.
- `Anuncio` aponta para `PerfilAnunciante`.
- `Avaliacao` aponta para o anunciante avaliado, para o anúncio e para a moradia.
- `ReputacaoAnunciante` consolida avaliações ativas de um `PerfilAnunciante`.

Esse desenho permite que tanto locadores quanto universitários possam publicar anúncios, sem acoplar as regras de anúncio e reputação apenas ao tipo `Locador`.

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

O frontend usa como padrão:

```text
http://localhost:8080
```

Para alterar a URL da API:

```bash
VITE_API_URL=http://localhost:8080 npm run dev
```

## Perfil de Desenvolvimento

Existe um seeder de dados de desenvolvimento em:

```text
backend/apto-api/src/main/java/com/apto/config/seed/DevDataSeeder.java
```

Ele roda no profile `dev` e popula usuários, locadores, perfis anunciantes, moradias, anúncios, avaliações, reputações, manifestações de interesse e denúncias.

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
- A sessão do frontend é local e simplificada, baseada em ID, tipo de usuário e nome.
- As permissões são simplificadas por IDs enviados em requests ou query params, como `usuarioId`, `anuncianteId`, `avaliadorId`, `solicitanteId` e `interessadoId`.
- O schema do banco é atualizado automaticamente pelo Hibernate em ambiente local.
- Dados do PostgreSQL persistem enquanto o volume Docker não for removido.

## Status

Em desenvolvimento.
