# Tasks: Elo Framework

## Objetivo

Registrar o estado final das tarefas da evolucao do Apto para o Elo Framework e da instanciacao de Study Buddy e Mentor Match.

As tarefas abaixo refletem a implementacao concluida no projeto final. Apto, Study Buddy e Mentor Match sao instancias concretas do mesmo framework.

## Tarefas Concluidas do Core e Apto

### TASK-BE-001: Estabilizar fronteira do core

Status: concluida.

Resultado:

- `elo-core` recebeu dependencias necessarias para JPA, Validation e transacoes.
- Contratos, estados e portas independentes das instancias foram definidos.
- Teste arquitetural garante que `elo-core` nao referencia as instancias.

### TASK-BE-002: Migrar usuario e criar Template Method

Status: concluida.

Resultado:

- `Usuario` foi movido para `com.elo.usuario`.
- `UsuarioService` controla o fluxo fixo de gestao de usuarios.
- Services concretos das instancias reutilizam o template.

### TASK-BE-003: Extrair fluxo de perfil

Status: concluida.

Resultado:

- `Perfil` virou contrato do core.
- `PerfilService` controla busca e criacao/atualizacao de perfil.
- Cada instancia fornece seu perfil concreto.

### TASK-BE-004: Extrair publicacao e gestao de ofertas

Status: concluida.

Resultado:

- `Oferta` virou contrato do core.
- `OfertaService` controla criacao, consulta, atualizacao, status e remocao.
- Cada instancia fornece sua oferta concreta.

### TASK-BE-005: Extrair Manifestacao de Interesse

Status: concluida.

Resultado:

- `StatusManifestacaoInteresse` foi movido para o core.
- `ManifestacaoInteresseService` controla as regras fixas.
- Operacao comum `cancelarPendentesDaOferta` foi adicionada.

### TASK-BE-006: Extrair denuncia e moderacao

Status: concluida.

Resultado:

- `StatusDenuncia` e maquina de estados foram movidos para o core.
- `DenunciaService` controla criacao, consultas, transicoes e exclusao.
- `ModeracaoService` controla validacao fixa da decisao.
- Instancias fornecem criterios de denuncia especificos.

### TASK-BE-007: Completar compatibilidade e matching

Status: concluida.

Resultado:

- `MatchingService` controla elegibilidade, LLM, fallback, ordenacao e `topN`.
- `ResultadoMatching` associa resultado diretamente ao candidato.
- Cada instancia fornece estrategia deterministica, provider LLM, prompt, parser e mapper.

### TASK-BE-008: Remover Observer e isolar funcionalidades especificas

Status: concluida.

Resultado:

- Eventos, observers, publisher, notificacoes e testes correspondentes foram removidos.
- Services passaram a chamar diretamente os fluxos necessarios.
- Avaliacao e reputacao permanecem exclusivas do Apto.

### TASK-APTO-009: Consolidar Apto como instancia do framework

Status: concluida.

Resultado:

- `backend/apto` permanece como instancia funcional.
- `frontend` permanece como frontend do Apto.
- Apto instancia usuarios, perfil de convivencia, anuncios, manifestacoes, denuncias, moderacao, avaliacao/reputacao e matching.
- Diagnostico da Groq exposto em `GET /diagnostico/groq`.

## Tarefas Concluidas do Study Buddy

### TASK-SB-001: Criar modulo Study Buddy

Status: concluida.

Resultado:

- `backend/study-buddy` criado como modulo Maven separado.
- O modulo depende de `elo-core`.
- `elo-core` e `apto` nao dependem de `study-buddy`.

### TASK-SB-002: Instanciar usuario da aplicacao

Status: concluida.

Resultado:

- `Estudante` estende `Usuario`.
- `EstudanteService` estende `UsuarioService`.
- DTOs, mapper, repository e testes de estudante criados.

### TASK-SB-003: Instanciar perfil academico

Status: concluida.

Resultado:

- `PerfilAcademico` implementa `Perfil`.
- `PerfilAcademicoService` estende `PerfilService`.
- Dados academicos representam o ponto flexivel de perfil.

### TASK-SB-004: Instanciar grupo de estudo como oferta

Status: concluida.

Resultado:

- `GrupoEstudo` implementa `Oferta`.
- `GrupoEstudoService` estende `OfertaService`.
- Grupo de estudo representa o ponto flexivel de oferta publicada.

### TASK-SB-005: Instanciar Manifestacao de Interesse em grupo

Status: concluida.

Resultado:

- `ManifestacaoInteresseGrupo` implementa `ManifestacaoInteresse`.
- `ManifestacaoInteresseGrupoService` estende `ManifestacaoInteresseService`.
- Manifestacao de Interesse permanece mecanismo fixo.

### TASK-SB-006: Instanciar denuncia e moderacao de grupos

Status: concluida.

Resultado:

- `DenunciaGrupoEstudo` implementa `Denuncia`.
- `CriterioDenunciaStudyBuddy` implementa `CriterioDenuncia`.
- `DenunciaGrupoEstudoService` e `ModeracaoGrupoEstudoService` reutilizam os fluxos do core.

### TASK-SB-007: Instanciar compatibilidade academica e matching

Status: concluida.

Resultado:

- `CompatibilidadeAcademicaCalculator` implementa `CompatibilidadeStrategy<PerfilAcademico>`.
- `StudyBuddyMatchingService` estende `MatchingService`.
- Matching ordena candidatos e respeita `topN`.
- Provider LLM da instancia usa Groq e fallback deterministico.

### TASK-SB-008: Expor controllers REST e frontend

Status: concluida.

Resultado:

- Controllers criados sob `/study-buddy`.
- Frontend criado em `frontend-study-buddy`.
- DTOs e controllers permanecem na instancia, nao no core.

## Tarefas Concluidas do Mentor Match

### TASK-MM-001: Criar modulo Mentor Match

Status: concluida.

Resultado:

- `backend/mentor-match` criado como modulo Maven separado.
- O modulo depende de `elo-core`.
- O core nao depende de `com.mentormatch`.

### TASK-MM-002: Instanciar usuarios e perfis de mentoria

Status: concluida.

Resultado:

- Alunos e mentores representam participantes da instancia.
- `PerfilMentoria` representa dados de mentoria.
- Services, repositories, mappers e controllers especificos foram criados.

### TASK-MM-003: Instanciar sessoes, solicitacoes e participantes

Status: concluida.

Resultado:

- `SessaoMentoria` representa a oferta concreta de mentoria.
- `SolicitacaoMentoria` e `ParticipanteMentoria` representam o fluxo especifico da instancia.
- Endpoints da instancia ficam sob `/mentor-match`.

### TASK-MM-004: Instanciar denuncia, moderacao e matching

Status: concluida.

Resultado:

- `DenunciaSessaoMentoria` e `CriterioDenunciaMentorMatch` representam denuncia/moderacao.
- `CompatibilidadeMentoriaCalculator` implementa criterios de mentoria.
- `MentorMatchingService` reutiliza o fluxo de matching do core.
- Provider LLM da instancia usa Groq e fallback deterministico.

### TASK-MM-005: Criar frontend Mentor Match

Status: concluida.

Resultado:

- Frontend criado em `frontend-mentor-match`.
- O frontend usa `http://localhost:8082` como backend padrao e aceita `VITE_API_URL`.

## Validacoes

Comando de validacao principal:

```bash
cd backend
mvn test
```

Criterios:

- `elo-core` deve passar nos testes.
- `apto` deve passar nos testes.
- `study-buddy` deve passar nos testes.
- `mentor-match` deve compilar e seus testes devem passar quando presentes.
- `elo-core` deve permanecer independente de `com.apto`, `com.studybuddy` e `com.mentormatch`.

## Tarefas Futuras Fora da Entrega

As ideias abaixo podem ser usadas em trabalhos futuros, mas nao compoem a entrega atual:

- autenticar usuarios de forma real;
- criar frontend multi-instancia unico;
- empacotar `elo-core` como artefato publicado;
- preparar deploy de producao;
- generalizar avaliacao/reputacao.
