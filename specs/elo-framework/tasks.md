# Tasks: Elo Framework

## Objetivo

Registrar o estado final das tarefas da evolução do Apto para o Elo Framework.

As tarefas abaixo refletem a implementação concluída até a Etapa 09. Study Buddy e Mentor Match não são tarefas de implementação desta entrega.

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

Status: em execução nesta etapa.

Resultado esperado:

- `spec.md`, `contracts.md`, `data-model.md`, `plan.md`, `tasks.md`, diagrama e `README.md` alinhados à arquitetura final.
- Study Buddy e Mentor Match removidos como tarefas de implementação.
- Hooks obrigatórios para futuras instâncias documentados.
- Testes executados com sucesso.

## Validações

Comando de validação principal:

```bash
cd backend
mvn test
```

Critérios:

- `elo-core` deve passar nos testes.
- `apto-api` deve passar nos testes.
- `elo-core` deve permanecer independente de `com.apto`.

## Tarefas Futuras Fora da Entrega

As ideias abaixo podem ser usadas em trabalhos futuros, mas não compõem a entrega atual:

- implementar Study Buddy;
- implementar Mentor Match;
- criar frontend para múltiplas instâncias;
- empacotar `elo-core` como artefato publicado;
- substituir permissões simplificadas por autenticação real.
