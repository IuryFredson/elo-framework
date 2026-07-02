# Tasks: Elo Framework

## Objetivo

Registrar o estado final das tarefas da evolução do Apto para o Elo Framework e da instanciação do Study Buddy.

As tarefas abaixo refletem a implementação concluída até a Etapa 09. Apto e Study Buddy são instâncias concretas; Mentor Match permanece fora da implementação atual.

## Tarefas Concluídas

### TASK-BE-001: Estabilizar fronteira do core

Status: concluída.

Resultado:

- `elo-core` recebeu dependências necessárias para JPA, Validation e transações.
- Contratos, estados e portas independentes do Apto foram definidos.
- Teste arquitetural garante que `elo-core` não referencia `com.apto`.

### TASK-BE-002: Migrar usuário e criar Template Method

Status: concluída.

Resultado:

- `Usuario` foi movido para `com.elo.usuario`.
- `UsuarioService` controla o fluxo fixo de gestão de usuários.
- `LocadorService` e `UsuarioUniversitarioService` instanciam o template.

### TASK-BE-003: Extrair fluxo de perfil

Status: concluída.

Resultado:

- `Perfil` virou contrato do core.
- `PerfilService` controla busca e criação/atualização de perfil.
- `PerfilConvivencia` instancia o ponto flexível de dados do perfil no Apto.

### TASK-BE-004: Extrair publicação e gestão de ofertas

Status: concluída.

Resultado:

- `Oferta` virou contrato do core.
- `OfertaService` controla criação, consulta, atualização, status e remoção.
- `Anuncio` instancia o ponto flexível de oferta publicada no Apto.

### TASK-BE-005: Extrair Manifestação de Interesse

Status: concluída.

Resultado:

- `StatusManifestacaoInteresse` foi movido para o core.
- `ManifestacaoInteresseService` controla as regras fixas.
- Apto fornece hooks, repository, mapper e exceções.
- Operação comum `cancelarPendentesDaOferta` foi adicionada.

### TASK-BE-006: Extrair denúncia e moderação

Status: concluída.

Resultado:

- `StatusDenuncia` e máquina de estados foram movidos para o core.
- `DenunciaService` controla criação, consultas, transições e exclusão.
- `ModeracaoService` controla validação fixa da decisão.
- `CriterioDenunciaApto` instancia o critério de denúncia do Apto.

### TASK-BE-007: Completar compatibilidade e matching

Status: concluída.

Resultado:

- `MatchingService` controla elegibilidade, LLM, fallback, ordenação e `topN`.
- `ResultadoMatching` associa resultado diretamente ao candidato.
- `AptoCompatibilidadeLlmProvider` fornece a porta LLM do Apto.
- `CompatibilidadeDeterministicaCalculator` mantém os critérios de convivência.

### TASK-BE-008: Remover Observer e isolar funcionalidades do Apto

Status: concluída.

Resultado:

- Eventos, observers, publisher, notificações e testes correspondentes foram removidos.
- `AnuncioService` e `ModeracaoService` cancelam manifestações diretamente.
- `AvaliacaoService` recalcula reputação diretamente.
- Avaliação e reputação permanecem exclusivas do Apto.

### TASK-DOC-009: Atualizar documentação e validar instanciação

Status: concluída.

Resultado:

- `spec.md`, `contracts.md`, `data-model.md`, `tasks.md`, `traceability.md`, diagrama e `README.md` alinhados à arquitetura final.
- Study Buddy registrado como segunda instância concreta do framework.
- Hooks obrigatórios para novas instâncias documentados.
- Testes executados com sucesso.

### TASK-SB-001: Criar módulo Study Buddy

Status: concluída.

Resultado:

- `backend/study-buddy` criado como módulo Maven separado.
- O módulo depende de `elo-core`.
- `elo-core` e `apto` não dependem de `study-buddy`.

### TASK-SB-002: Instanciar usuário da aplicação

Status: concluída.

Resultado:

- `Estudante` estende `Usuario`.
- `EstudanteService` estende `UsuarioService`.
- DTOs, mapper, repository e testes de estudante criados.

### TASK-SB-003: Instanciar perfil acadêmico

Status: concluída.

Resultado:

- `PerfilAcademico` implementa `Perfil`.
- `PerfilAcademicoService` estende `PerfilService`.
- Dados acadêmicos representam o ponto flexível de perfil.

### TASK-SB-004: Instanciar grupo de estudo como oferta

Status: concluída.

Resultado:

- `GrupoEstudo` implementa `Oferta`.
- `GrupoEstudoService` estende `OfertaService`.
- Grupo de estudo representa o ponto flexível de oferta publicada.

### TASK-SB-005: Instanciar Manifestação de Interesse em grupo

Status: concluída.

Resultado:

- `ManifestacaoInteresseGrupo` implementa `ManifestacaoInteresse`.
- `ManifestacaoInteresseGrupoService` estende `ManifestacaoInteresseService`.
- Manifestação de Interesse permanece mecanismo fixo.

### TASK-SB-006: Instanciar compatibilidade acadêmica e matching

Status: concluída.

Resultado:

- `CompatibilidadeAcademicaCalculator` implementa `CompatibilidadeStrategy<PerfilAcademico>`.
- `StudyBuddyMatchingService` estende `MatchingService`.
- Matching ordena candidatos e respeita `topN`.

### TASK-SB-007: Expor controllers REST mínimos

Status: concluída.

Resultado:

- Controllers finos criados sob `/study-buddy`.
- DTOs e controllers permanecem na instância, não no core.

### TASK-SB-008: Validar instância completa

Status: concluída.

Resultado:

- Testes de service e controller da instância passando.
- Reactor Maven completo validado com `mvn test`.

## Validações

Comando de validação principal:

```bash
cd backend
mvn test
```

Critérios:

- `elo-core` deve passar nos testes.
- `apto-api` deve passar nos testes.
- `study-buddy` deve passar nos testes.
- `elo-core` deve permanecer independente de `com.apto`.
- `elo-core` deve permanecer independente de `com.studybuddy`.

## Tarefas Futuras Fora da Entrega

As ideias abaixo podem ser usadas em trabalhos futuros, mas não compõem a entrega atual:

- implementar Mentor Match;
- criar frontend para múltiplas instâncias;
- empacotar `elo-core` como artefato publicado;
- substituir permissões simplificadas por autenticação real.
