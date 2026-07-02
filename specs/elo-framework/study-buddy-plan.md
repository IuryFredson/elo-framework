# Planejamento: Study Buddy como Instancia do Elo Framework

## Objetivo

Planejar a implementacao da instancia **Study Buddy** usando o Elo Framework.

Study Buddy sera uma plataforma para estudantes encontrarem grupos de estudo compativeis.

Este documento iniciou como planejamento e agora registra a execução da instância Study Buddy. O código correspondente fica em `backend/study-buddy`.

## Contexto

O Elo Framework ja possui um nucleo em `backend/elo-core` com contratos e Template Methods para:

- Usuario;
- Perfil;
- Oferta;
- Manifestacao de Interesse;
- Denuncia;
- Moderacao;
- Compatibilidade e Matching.

O Apto ja esta instanciado em `backend/apto`.

A nova instancia Study Buddy deve usar o mesmo principio:

```text
study-buddy -> elo-core
```

O core nao deve depender de Study Buddy.

## Escopo Minimo

O Study Buddy minimo deve demonstrar:

1. Cadastro/gestao de estudante.
2. Perfil academico.
3. Grupo de estudo como oferta publicada.
4. Manifestacao de Interesse em grupo de estudo.
5. Compatibilidade academica entre estudantes e grupos/candidatos.

Fora do escopo inicial:

- frontend;
- autenticacao real;
- deploy;
- reputacao;
- notificacoes;
- moderacao completa, salvo se a equipe quiser reaproveitar denuncia/moderacao depois;
- LLM obrigatoria;
- multiplos papeis complexos.

## Pontos Fixos Reutilizados

### Usuario

Usar:

- `com.elo.usuario.Usuario`
- `com.elo.usuario.UsuarioService`

Instancia Study Buddy:

- `Estudante`
- `EstudanteService`

### Perfil

Usar:

- `com.elo.perfil.Perfil`
- `com.elo.perfil.PerfilService`

Instancia Study Buddy:

- `PerfilAcademico`
- `PerfilAcademicoService`

### Oferta

Usar:

- `com.elo.oferta.Oferta`
- `com.elo.oferta.OfertaService`

Instancia Study Buddy:

- `GrupoEstudo`
- `GrupoEstudoService`

### Manifestacao de Interesse

Usar:

- `com.elo.manifestacao.ManifestacaoInteresse`
- `com.elo.manifestacao.ManifestacaoInteresseService`
- `com.elo.manifestacao.StatusManifestacaoInteresse`

Instancia Study Buddy:

- `ManifestacaoInteresseGrupo`
- `ManifestacaoInteresseGrupoService`

Manifestacao de Interesse continua sendo ponto fixo. O que muda e a oferta alvo: grupo de estudo.

### Compatibilidade e Matching

Usar:

- `com.elo.compatibilidade.CompatibilidadeStrategy`
- `com.elo.compatibilidade.MatchingService`
- `com.elo.compatibilidade.ResultadoCompatibilidade`
- `com.elo.compatibilidade.ResultadoMatching`

Instancia Study Buddy:

- `CompatibilidadeAcademicaCalculator`
- `StudyBuddyMatchingService`

## Pontos Flexiveis

### 1. Dados do Perfil

Classe sugerida:

- `PerfilAcademico`

Campos minimos:

- `curso`;
- `disciplinasInteresse`;
- `disponibilidade`;
- `objetivoEstudo`;
- `nivelConhecimento`;
- `modalidadePreferida`;
- `descricao`.

Enums sugeridos:

- `ObjetivoEstudo`
- `NivelConhecimento`
- `ModalidadeEstudo`
- `PeriodoDisponibilidade`

### 2. Tipo de Oferta Publicada

Classe sugerida:

- `GrupoEstudo`

Campos minimos:

- `id`;
- `titulo`;
- `descricao`;
- `disciplina`;
- `publicador`;
- `quantidadeVagas`;
- `modalidade`;
- `periodo`;
- `status`;
- `dataPublicacao`.

Enums sugeridos:

- `StatusGrupoEstudo`: `ATIVO`, `PAUSADO`, `ENCERRADO`
- `ModalidadeEstudo`: `PRESENCIAL`, `ONLINE`, `HIBRIDO`
- `PeriodoDisponibilidade`: `MANHA`, `TARDE`, `NOITE`, `FIM_DE_SEMANA`, `FLEXIVEL`

### 3. Criterios de Compatibilidade

Classe sugerida:

- `CompatibilidadeAcademicaCalculator`

Criterios minimos:

- mesma disciplina ou disciplina de interesse relacionada;
- disponibilidade compativel;
- objetivo de estudo igual ou complementar;
- nivel de conhecimento proximo;
- modalidade preferida compativel.

Resultado:

- percentual;
- justificativa;
- criterios atendidos.

## Pacotes Sugeridos

Criar uma nova instancia separada do Apto:

```text
backend/study-buddy/src/main/java/com/studybuddy
```

Pacotes:

```text
com.studybuddy.controller
com.studybuddy.dto.request
com.studybuddy.dto.response
com.studybuddy.exception
com.studybuddy.mapper
com.studybuddy.model.entity
com.studybuddy.model.enums
com.studybuddy.repository
com.studybuddy.service
com.studybuddy.service.matching
```

## Classes Sugeridas

### Entidades

- `Estudante extends Usuario`
- `PerfilAcademico implements Perfil`
- `GrupoEstudo implements Oferta`
- `ManifestacaoInteresseGrupo implements ManifestacaoInteresse`

### Enums

- `StatusGrupoEstudo`
- `ObjetivoEstudo`
- `NivelConhecimento`
- `ModalidadeEstudo`
- `PeriodoDisponibilidade`

### DTOs de Request

- `CriarEstudanteRequestDTO`
- `AtualizarEstudanteRequestDTO`
- `AtualizarPerfilAcademicoRequestDTO`
- `CriarGrupoEstudoRequestDTO`
- `AtualizarGrupoEstudoRequestDTO`
- `CriarManifestacaoInteresseGrupoRequestDTO`

### DTOs de Response

- `EstudanteResponseDTO`
- `PerfilAcademicoResponseDTO`
- `GrupoEstudoResponseDTO`
- `ManifestacaoInteresseGrupoResponseDTO`
- `MatchGrupoEstudoResponseDTO`
- `StudyBuddyMatchingResponseDTO`

### Repositories

- `EstudanteRepository`
- `PerfilAcademicoRepository`
- `GrupoEstudoRepository`
- `ManifestacaoInteresseGrupoRepository`

Repositories usados por templates devem ser compativeis com `RepositorioBase` quando necessario.

### Mappers

- `EstudanteMapper`
- `PerfilAcademicoMapper`
- `GrupoEstudoMapper`
- `ManifestacaoInteresseGrupoMapper`
- `StudyBuddyMatchingMapper`

### Services

- `EstudanteService extends UsuarioService`
- `PerfilAcademicoService extends PerfilService`
- `GrupoEstudoService extends OfertaService`
- `ManifestacaoInteresseGrupoService extends ManifestacaoInteresseService`
- `StudyBuddyMatchingService extends MatchingService`
- `CompatibilidadeAcademicaCalculator implements CompatibilidadeStrategy<PerfilAcademico>`

### Controllers

- `EstudanteController`
- `PerfilAcademicoController`
- `GrupoEstudoController`
- `ManifestacaoInteresseGrupoController`
- `StudyBuddyMatchingController`

## Endpoints Minimos

### Estudantes

```text
POST   /study-buddy/estudantes
GET    /study-buddy/estudantes
GET    /study-buddy/estudantes/{id}
PUT    /study-buddy/estudantes/{id}
PATCH  /study-buddy/estudantes/{id}/status
DELETE /study-buddy/estudantes/{id}
```

### Perfil Academico

```text
GET /study-buddy/estudantes/{id}/perfil
PUT /study-buddy/estudantes/{id}/perfil
```

### Grupos de Estudo

```text
POST   /study-buddy/grupos
GET    /study-buddy/grupos
GET    /study-buddy/grupos/{id}
PUT    /study-buddy/grupos/{id}
PATCH  /study-buddy/grupos/{id}/status
DELETE /study-buddy/grupos/{id}
```

### Manifestacoes de Interesse

```text
POST  /study-buddy/manifestacoes
POST  /study-buddy/manifestacoes/{id}/aceitar
POST  /study-buddy/manifestacoes/{id}/recusar
POST  /study-buddy/manifestacoes/{id}/cancelar
GET   /study-buddy/grupos/{id}/manifestacoes
GET   /study-buddy/estudantes/{id}/manifestacoes
```

### Matching

```text
GET /study-buddy/matching?estudanteId={id}&topN={n}
```

## Testes Minimos

### Core nao deve mudar

Antes de alterar `elo-core`, justificar necessidade. A expectativa inicial e nao alterar o core.

### Testes da instancia

Criar testes para:

- `EstudanteService`;
- `PerfilAcademicoService`;
- `GrupoEstudoService`;
- `ManifestacaoInteresseGrupoService`;
- `CompatibilidadeAcademicaCalculator`;
- `StudyBuddyMatchingService`.

Casos obrigatorios:

- criar estudante;
- criar/atualizar perfil academico;
- criar grupo de estudo;
- impedir manifestacao no proprio grupo;
- impedir manifestacao duplicada ativa;
- aceitar/recusar/cancelar manifestacao;
- calcular compatibilidade por disciplina, horario, objetivo, nivel e modalidade;
- ordenar matching por percentual;
- limitar por `topN`.

## Plano de Implementacao por Etapas

Este plano deve ser executado uma etapa por vez. A etapa seguinte so deve comecar depois que a anterior estiver compilando e com testes passando.

### Etapa 01: Criar a instancia Study Buddy no projeto

Objetivo:

- criar o modulo `backend/study-buddy`;
- adicionar o modulo ao reactor Maven;
- configurar a aplicacao Spring Boot da instancia;
- declarar dependencia somente para `elo-core` e dependencias proprias da aplicacao.

Entregavel:

- modulo `study-buddy` compilando;
- aplicacao inicial da instancia criada;
- nenhum codigo de dominio ainda.

Validacao:

- `mvn test` no reactor deve passar;
- `elo-core` nao deve depender de `study-buddy`;
- `apto` nao deve depender de `study-buddy`.

### Etapa 02: Implementar Usuario da instancia

Objetivo:

- criar `Estudante extends Usuario`;
- criar DTOs, mapper, repository e service de estudante;
- fazer `EstudanteService` reutilizar `UsuarioService` do framework.

Entregavel:

- fluxo minimo de cadastro, consulta, atualizacao e status de estudante;
- testes unitarios de `EstudanteService`.

Validacao:

- estudante deve provar reutilizacao do ponto fixo de cadastro/gestao de usuarios;
- endpoints so devem ser criados se isso nao atrasar a validacao do service.

### Etapa 03: Implementar Perfil Academico

Objetivo:

- criar `PerfilAcademico implements Perfil`;
- representar os dados flexiveis de perfil da instancia;
- criar DTOs, mapper, repository e service de perfil;
- fazer `PerfilAcademicoService` reutilizar `PerfilService` do framework.

Entregavel:

- perfil academico vinculado a estudante;
- campos academicos minimos definidos;
- testes de criacao/atualizacao/consulta de perfil.

Validacao:

- o ponto flexivel "Dados do perfil" deve estar claramente instanciado por `PerfilAcademico`;
- nao alterar contratos do Apto para encaixar Study Buddy.

### Etapa 04: Implementar Grupo de Estudo como Oferta

Objetivo:

- criar `GrupoEstudo implements Oferta`;
- representar grupo de estudo como tipo de oferta publicada;
- criar DTOs, mapper, repository e service de grupo;
- fazer `GrupoEstudoService` reutilizar `OfertaService` do framework.

Entregavel:

- fluxo minimo de publicacao, consulta, atualizacao e status de grupo de estudo;
- testes unitarios de `GrupoEstudoService`.

Validacao:

- o ponto flexivel "Tipo de oferta publicada" deve estar claramente instanciado por `GrupoEstudo`;
- grupo deve expor publicador e estado ativo/inativo conforme esperado pelo core.

### Etapa 05: Implementar Manifestacao de Interesse em Grupo

Objetivo:

- criar `ManifestacaoInteresseGrupo implements ManifestacaoInteresse`;
- criar DTOs, mapper, repository e service de manifestacao;
- fazer `ManifestacaoInteresseGrupoService` reutilizar `ManifestacaoInteresseService` do framework.

Entregavel:

- estudante pode manifestar interesse em grupo de estudo;
- publicador pode aceitar ou recusar;
- interessado pode cancelar;
- listagens minimas por grupo e por estudante.

Validacao:

- Manifestacao de Interesse continua sendo ponto fixo;
- nao tratar manifestacao como ponto flexivel;
- testes devem cobrir interesse no proprio grupo, duplicidade ativa e transicoes de status.

### Etapa 06: Implementar Compatibilidade Academica e Matching

Objetivo:

- criar `CompatibilidadeAcademicaCalculator`;
- criar `StudyBuddyMatchingService`;
- reutilizar contratos de compatibilidade e matching do framework.

Entregavel:

- calculo de compatibilidade por disciplina, disponibilidade, objetivo, nivel e modalidade;
- matching ordenado por percentual;
- suporte a limite `topN`.

Validacao:

- o ponto flexivel "Criterios de compatibilidade" deve estar claramente instanciado por `CompatibilidadeAcademicaCalculator`;
- testes devem provar ranking, percentual e justificativas.

### Etapa 07: Criar Controllers REST Minimos

Objetivo:

- expor endpoints minimos da instancia Study Buddy;
- manter os controllers finos, delegando regras para services.

Entregavel:

- controllers de estudante, perfil academico, grupo de estudo, manifestacao e matching;
- rotas sob `/study-buddy`.

Validacao:

- endpoints nao devem alterar endpoints do Apto;
- DTOs de Study Buddy nao devem ser movidos para o core.

### Etapa 08: Testar a instancia completa

Objetivo:

- consolidar testes unitarios e, se necessario, testes de controller;
- rodar a suite completa do backend.

Entregavel:

- testes cobrindo os pontos fixos reutilizados e os pontos flexiveis instanciados;
- build do reactor passando.

Validacao:

- `mvn test` deve passar em `elo-core`, `apto` e `study-buddy`;
- nenhum teste do Apto deve quebrar;
- a dependencia continua unidirecional: instancias dependem do core, core nao depende das instancias.

### Etapa 09: Atualizar documentacao e diagrama

Objetivo:

- registrar Study Buddy como segunda instancia concreta do Elo Framework;
- atualizar diagrama, se a equipe decidir apresentar a nova instancia;
- documentar prompts e decisoes tomadas.

Entregavel:

- documentacao atualizada;
- diagrama com Apto e/ou Study Buddy, conforme decisao da equipe;
- registro das interacoes relevantes.

Validacao:

- documentacao deve deixar claro que Apto e Study Buddy sao instancias separadas do mesmo framework;
- pontos fixos e flexiveis devem permanecer consistentes com as specs.

## Mudancas a Evitar

- Nao alterar o Apto.
- Nao transformar Manifestacao de Interesse em ponto flexivel.
- Nao mover DTOs ou controllers para o core.
- Nao criar frontend neste primeiro momento.
- Nao implementar Mentor Match junto.
- Nao criar LLM obrigatoria para Study Buddy.
- Nao generalizar avaliacao/reputacao.
- Nao mexer no core sem necessidade clara.

## Decisoes Tomadas pela Equipe

1. Study Buddy foi implementado como novo modulo Maven em `backend/study-buddy`.
2. A instancia possui API REST minima sob `/study-buddy`.
3. A instancia usa persistencia JPA e H2 nos testes, como os demais modulos.
4. O matching minimo implementado compara estudante com estudante por perfil academico.
5. Denuncia/moderacao nao entram no Study Buddy minimo.
6. Seed de dados nao foi incluido nesta etapa.
7. O diagrama final inclui Apto e Study Buddy como instancias separadas do mesmo framework.

## Resultado

Study Buddy foi implementado como segunda instancia concreta do Elo Framework.

A dependencia permanece unidirecional:

```text
study-buddy -> elo-core
```

Isso evita misturar dominio do Apto com a nova instancia e demonstra que `elo-core` e reutilizavel por outra aplicacao.

Escopo recomendado para a primeira entrega:

- sem frontend;
- sem LLM;
- sem denuncia/moderacao;
- com persistencia JPA simples;
- com controllers REST minimos;
- com testes unitarios cobrindo os pontos flexiveis.
