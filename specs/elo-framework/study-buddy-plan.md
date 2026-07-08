# Study Buddy: Instancia do Elo Framework

## Objetivo

Registrar o planejamento e o resultado final da instancia **Study Buddy** usando o Elo Framework.

Study Buddy e uma plataforma para estudantes encontrarem grupos de estudo compativeis. O codigo correspondente fica em:

- `backend/study-buddy`
- `frontend-study-buddy`

## Contexto

O Elo Framework possui um nucleo em `backend/elo-core` com contratos e Template Methods para:

- usuario;
- perfil;
- oferta;
- manifestacao de interesse;
- denuncia;
- moderacao;
- compatibilidade e matching;
- integracao LLM via porta e cliente base Groq.

A instancia Study Buddy segue a dependencia:

```text
study-buddy -> elo-core
```

O core nao depende de Study Buddy.

## Escopo Implementado

O Study Buddy final demonstra:

1. Cadastro e gestao de estudantes.
2. Perfil academico.
3. Grupo de estudo como oferta publicada.
4. Manifestacao de Interesse em grupo de estudo.
5. Denuncia e moderacao de grupos de estudo.
6. Compatibilidade academica e matching.
7. Integracao LLM/Groq com fallback deterministico.
8. API REST sob `/study-buddy`.
9. Frontend dedicado em `frontend-study-buddy`.

Fora do escopo:

- autenticacao real;
- deploy;
- reputacao;
- notificacoes;
- frontend multi-instancia unico.

## Pontos Fixos Reutilizados

### Usuario

Usa:

- `com.elo.usuario.Usuario`
- `com.elo.usuario.UsuarioService`

Instancia Study Buddy:

- `Estudante`
- `EstudanteService`

### Perfil

Usa:

- `com.elo.perfil.Perfil`
- `com.elo.perfil.PerfilService`

Instancia Study Buddy:

- `PerfilAcademico`
- `PerfilAcademicoService`

### Oferta

Usa:

- `com.elo.oferta.Oferta`
- `com.elo.oferta.OfertaService`

Instancia Study Buddy:

- `GrupoEstudo`
- `GrupoEstudoService`

### Manifestacao de Interesse

Usa:

- `com.elo.manifestacao.ManifestacaoInteresse`
- `com.elo.manifestacao.ManifestacaoInteresseService`
- `com.elo.manifestacao.StatusManifestacaoInteresse`

Instancia Study Buddy:

- `ManifestacaoInteresseGrupo`
- `ManifestacaoInteresseGrupoService`

Manifestacao de Interesse continua sendo ponto fixo. O que muda e a oferta alvo: grupo de estudo.

### Denuncia e Moderacao

Usa:

- `com.elo.denuncia.Denuncia`
- `com.elo.denuncia.DenunciaService`
- `com.elo.denuncia.CriterioDenuncia`
- `com.elo.moderacao.ModeracaoService`

Instancia Study Buddy:

- `DenunciaGrupoEstudo`
- `CriterioDenunciaStudyBuddy`
- `DenunciaGrupoEstudoService`
- `ModeracaoGrupoEstudoService`

### Compatibilidade e Matching

Usa:

- `com.elo.compatibilidade.CompatibilidadeStrategy`
- `com.elo.compatibilidade.MatchingService`
- `com.elo.compatibilidade.ResultadoCompatibilidade`
- `com.elo.compatibilidade.ResultadoMatching`
- `com.elo.compatibilidade.ProvedorCompatibilidadeLlm`

Instancia Study Buddy:

- `CompatibilidadeAcademicaCalculator`
- `StudyBuddyMatchingService`
- `StudyBuddyCompatibilidadeLlmProvider`
- `StudyBuddyMatchingPromptBuilder`
- `StudyBuddyMatchingLlmParser`

## Pontos Flexiveis

### 1. Dados do Perfil

Classe:

- `PerfilAcademico`

Campos principais:

- `curso`;
- `disciplinasInteresse`;
- `disponibilidade`;
- `objetivoEstudo`;
- `nivelConhecimento`;
- `modalidadePreferida`;
- `descricao`.

Enums:

- `ObjetivoEstudo`
- `NivelConhecimento`
- `ModalidadeEstudo`
- `PeriodoDisponibilidade`

### 2. Tipo de Oferta Publicada

Classe:

- `GrupoEstudo`

Campos principais:

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

Enums:

- `StatusGrupoEstudo`
- `ModalidadeEstudo`
- `PeriodoDisponibilidade`

### 3. Criterios de Compatibilidade

Classe:

- `CompatibilidadeAcademicaCalculator`

Criterios:

- disciplina ou disciplina de interesse relacionada;
- disponibilidade compativel;
- objetivo de estudo igual ou complementar;
- nivel de conhecimento proximo;
- modalidade preferida compativel.

Resultado:

- percentual;
- justificativa;
- criterios atendidos.

### 4. Criterios de Denuncia

Classe:

- `CriterioDenunciaStudyBuddy`

Representa criterios especificos para denuncia de grupos de estudo.

## Pacotes

```text
backend/study-buddy/src/main/java/com/studybuddy
```

Pacotes principais:

```text
com.studybuddy.config
com.studybuddy.controller
com.studybuddy.dto.request
com.studybuddy.dto.response
com.studybuddy.exception
com.studybuddy.integration.llm
com.studybuddy.mapper
com.studybuddy.model.entity
com.studybuddy.model.enums
com.studybuddy.repository
com.studybuddy.service
com.studybuddy.service.matching
```

## Classes Principais

### Entidades

- `Estudante extends Usuario`
- `PerfilAcademico implements Perfil`
- `GrupoEstudo implements Oferta`
- `ManifestacaoInteresseGrupo implements ManifestacaoInteresse`
- `DenunciaGrupoEstudo implements Denuncia`

### DTOs de Request

- `CriarEstudanteRequestDTO`
- `AtualizarEstudanteRequestDTO`
- `AtualizarPerfilAcademicoRequestDTO`
- `CriarGrupoEstudoRequestDTO`
- `AtualizarGrupoEstudoRequestDTO`
- `CriarManifestacaoInteresseGrupoRequestDTO`
- `CriarDenunciaGrupoEstudoRequestDTO`
- `AtualizarStatusDenunciaGrupoEstudoRequestDTO`
- `ModerarDenunciaGrupoEstudoRequestDTO`

### DTOs de Response

- `EstudanteResponseDTO`
- `PerfilAcademicoResponseDTO`
- `GrupoEstudoResponseDTO`
- `ManifestacaoInteresseGrupoResponseDTO`
- `DenunciaGrupoEstudoResponseDTO`
- `ModeracaoGrupoEstudoResponseDTO`
- `MatchEstudanteResponseDTO`
- `StudyBuddyMatchingResponseDTO`

### Services

- `EstudanteService extends UsuarioService`
- `PerfilAcademicoService extends PerfilService`
- `GrupoEstudoService extends OfertaService`
- `ManifestacaoInteresseGrupoService extends ManifestacaoInteresseService`
- `DenunciaGrupoEstudoService extends DenunciaService`
- `ModeracaoGrupoEstudoService extends ModeracaoService`
- `StudyBuddyMatchingService extends MatchingService`
- `CompatibilidadeAcademicaCalculator implements CompatibilidadeStrategy<PerfilAcademico>`

### Controllers

- `EstudanteController`
- `PerfilAcademicoController`
- `GrupoEstudoController`
- `ManifestacaoInteresseGrupoController`
- `DenunciaGrupoEstudoController`
- `ModeracaoGrupoEstudoController`
- `StudyBuddyMatchingController`

## Endpoints Finais

### Estudantes e Perfil Academico

```text
/study-buddy/usuarios
/study-buddy/usuarios/{id}/perfil
```

### Grupos de Estudo

```text
/study-buddy/ofertas
```

### Manifestacoes de Interesse

```text
/study-buddy/manifestacoes
```

### Denuncias e Moderacao

```text
/study-buddy/denuncias
/study-buddy/moderacoes/denuncias
```

### Matching

```text
/study-buddy/matching
```

## Plano de Implementacao Executado

### Etapa 01: Criar a instancia Study Buddy

Status: concluida.

Resultado:

- modulo `backend/study-buddy` criado;
- modulo adicionado ao reactor Maven;
- aplicacao Spring Boot criada;
- dependencia apenas para `elo-core` e dependencias proprias.

### Etapa 02: Implementar usuario da instancia

Status: concluida.

Resultado:

- `Estudante extends Usuario`;
- DTOs, mapper, repository e service de estudante;
- `EstudanteService` reutiliza `UsuarioService`.

### Etapa 03: Implementar perfil academico

Status: concluida.

Resultado:

- `PerfilAcademico implements Perfil`;
- `PerfilAcademicoService` reutiliza `PerfilService`;
- dados academicos representam o ponto flexivel de perfil.

### Etapa 04: Implementar grupo de estudo como oferta

Status: concluida.

Resultado:

- `GrupoEstudo implements Oferta`;
- `GrupoEstudoService` reutiliza `OfertaService`;
- grupo representa o ponto flexivel de oferta publicada.

### Etapa 05: Implementar Manifestacao de Interesse em grupo

Status: concluida.

Resultado:

- `ManifestacaoInteresseGrupo implements ManifestacaoInteresse`;
- `ManifestacaoInteresseGrupoService` reutiliza `ManifestacaoInteresseService`;
- regras de interesse proprio, duplicidade e transicoes reutilizadas.

### Etapa 06: Implementar denuncia e moderacao

Status: concluida.

Resultado:

- `DenunciaGrupoEstudo implements Denuncia`;
- `CriterioDenunciaStudyBuddy implements CriterioDenuncia`;
- service de denuncia e moderacao reutilizam templates do core.

### Etapa 07: Implementar compatibilidade academica e matching

Status: concluida.

Resultado:

- `CompatibilidadeAcademicaCalculator` implementado;
- `StudyBuddyMatchingService` reutiliza `MatchingService`;
- provider LLM, prompt e parser da instancia criados;
- fallback deterministico mantido.

### Etapa 08: Criar controllers REST e frontend

Status: concluida.

Resultado:

- controllers sob `/study-buddy`;
- frontend dedicado criado em `frontend-study-buddy`.

### Etapa 09: Testar e documentar

Status: concluida.

Resultado:

- testes de services, matching, parser e controllers criados;
- diagramas e documentos atualizados para refletir Study Buddy como instancia concreta.

## Mudancas Evitadas

- Nao alterar o Apto para encaixar Study Buddy.
- Nao transformar Manifestacao de Interesse em ponto flexivel.
- Nao mover DTOs ou controllers para o core.
- Nao criar frontend multi-instancia unico.
- Nao generalizar avaliacao/reputacao.

## Resultado

Study Buddy foi implementado como segunda instancia concreta do Elo Framework.

A dependencia permanece unidirecional:

```text
study-buddy -> elo-core
```

Isso evita misturar dominio do Apto com a nova instancia e demonstra que `elo-core` e reutilizavel por outra aplicacao.
