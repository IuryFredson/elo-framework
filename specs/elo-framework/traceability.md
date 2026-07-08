# Traceability: Elo Framework

## Objetivo

Relacionar requisitos, tarefas, arquivos e testes da evolucao do Apto, Study Buddy e Mentor Match para o Elo Framework.

## Matriz Final

| Requisito | Descricao | Arquivos principais | Testes/evidencias | Status |
| --- | --- | --- | --- | --- |
| EF-001 | Nucleo reutilizavel | `backend/elo-core/src/main/java/com/elo/**` | `DependenciaArquiteturalTest`, testes dos templates do core | Concluido |
| EF-002 | Pontos flexiveis limitados | `Perfil`, `Oferta`, `CompatibilidadeStrategy` e implementacoes das instancias | Testes de compatibilidade e matching das instancias | Concluido |
| EF-003 | Manifestacao de Interesse como ponto fixo | `ManifestacaoInteresseService`, `StatusManifestacaoInteresse`, manifestacoes concretas | Testes de manifestacao no core/Apto/Study Buddy | Concluido |
| EF-004 | Apto instanciado no framework | `backend/apto/src/main/java/com/apto/**` | Suite `apto` | Concluido |
| EF-005 | Matching com fallback | `MatchingService`, `ProvedorCompatibilidadeLlm`, providers das instancias | Testes de matching e parsers LLM | Concluido |
| EF-006 | Denuncia e moderacao | `DenunciaService`, `ModeracaoService`, criterios das instancias | Testes de denuncia/moderacao | Concluido |
| EF-007 | Extensibilidade controlada | `spec.md`, `contracts.md`, `plan.md`, `data-model.md`, diagramas | Teste arquitetural do core | Concluido |
| EF-008 | Study Buddy instanciado no framework | `backend/study-buddy/src/main/java/com/studybuddy/**`, `frontend-study-buddy` | Suite `study-buddy` e frontend dedicado | Concluido |
| EF-009 | Mentor Match instanciado no framework | `backend/mentor-match/src/main/java/com/mentormatch/**`, `frontend-mentor-match` | Modulo Maven, frontend dedicado e diagrama Mentor Match | Concluido |

## Commits Registrados

| Commit | Proposito | Observacao |
| --- | --- | --- |
| `457cae1` | Extrair fluxo de Manifestacao de Interesse | Etapa 05 |
| `ac7593b` | Registrar resumo da interacao 22 | Documento de estudo |
| `9f37167` | Extrair fluxo de denuncia e moderacao | Etapa 06 |
| `9128326` | Extrair fluxo de compatibilidade e matching | Etapa 07 |

Novas alteracoes podem ter sido commitadas em conjunto pela equipe conforme a estrategia de versionamento final.

## Testes Registrados

| Escopo | Comando | Resultado esperado |
| --- | --- | --- |
| Backend completo | `mvn test` em `backend` | `elo-core`, `apto`, `study-buddy` e `mentor-match` validos conforme suites disponiveis |
| Core | testes do reactor | templates independentes das instancias |
| Apto | testes do modulo `apto` | regressao dos fluxos de moradia |
| Study Buddy | testes do modulo `study-buddy` | fluxos academicos, matching, denuncia/moderacao |
| Mentor Match | testes/compilacao do modulo `mentor-match` | fluxos de mentoria e matching |

A contagem exata de testes pode variar conforme novas suites sejam adicionadas. O criterio final e o reactor Maven passar.

## Decisoes LLM e Equipe

| Tema | Decisao |
| --- | --- |
| Manifestacao de Interesse | Mantida como ponto fixo, nao como ponto flexivel |
| Apto | Instancia concreta principal do framework |
| Study Buddy | Segunda instancia concreta do framework |
| Mentor Match | Terceira instancia concreta do framework |
| Adapters | Evitados quando implementacao direta nos contratos era simples |
| Observer | Removido, substituido por chamadas diretas |
| Avaliacao e reputacao | Mantidas exclusivas do Apto |
| Groq | Cliente base generalizado no core; propriedades, excecoes, prompt e parser ficam nas instancias |

## Evidencias de Instanciacao

### Core

- `elo-core` contem contratos e templates reutilizaveis.
- Testes do core usam implementacoes falsas independentes das instancias.
- Teste arquitetural protege a dependencia unidirecional.

### Apto

- `PerfilConvivencia` implementa `Perfil`.
- `Anuncio` implementa `Oferta`.
- `ManifestacaoInteresse` implementa `ManifestacaoInteresse` do core.
- `Denuncia` implementa `Denuncia` do core.
- `CriterioDenunciaApto` implementa `CriterioDenuncia`.
- `CompatibilidadeDeterministicaCalculator` implementa `CompatibilidadeStrategy<PerfilConvivencia>`.
- Services do Apto estendem templates do core.

### Study Buddy

- `Estudante` especializa usuario da instancia.
- `PerfilAcademico` implementa `Perfil`.
- `GrupoEstudo` implementa `Oferta`.
- `ManifestacaoInteresseGrupo` implementa `ManifestacaoInteresse` do core.
- `DenunciaGrupoEstudo` implementa `Denuncia` do core.
- `CriterioDenunciaStudyBuddy` implementa `CriterioDenuncia`.
- `CompatibilidadeAcademicaCalculator` implementa `CompatibilidadeStrategy<PerfilAcademico>`.
- Services do Study Buddy estendem templates do core.

### Mentor Match

- `Aluno` e `Mentor` representam participantes da instancia.
- `PerfilMentoria` representa dados especificos de mentoria.
- `SessaoMentoria` representa a oferta de mentoria.
- `DenunciaSessaoMentoria` representa denuncia da instancia.
- `CriterioDenunciaMentorMatch` implementa criterios especificos.
- `CompatibilidadeMentoriaCalculator` implementa criterios de matching de mentoria.
- Services do Mentor Match reutilizam contratos/templates do core onde aplicavel.
