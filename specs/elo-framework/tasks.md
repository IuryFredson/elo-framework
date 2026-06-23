# Tasks: Elo Framework

## Objetivo

Listar tarefas pequenas, rastreaveis e derivadas da especificacao.

Nenhuma tarefa deve ser implementada antes de revisao e aprovacao da equipe.

## Tarefas de Especificacao

### TASK-SPEC-001: Revisar pontos fixos e variaveis

Requisito relacionado:

- EF-001
- EF-002
- EF-003

Arquivos:

- `specs/elo-framework/spec.md`

Resultado esperado:

- pontos fixos e variaveis aprovados pela equipe;
- interacao mantida como ponto fixo.

### TASK-SPEC-002: Revisar modelo conceitual

Requisito relacionado:

- EF-004
- EF-005
- EF-006

Arquivos:

- `specs/elo-framework/data-model.md`

Resultado esperado:

- modelo de Usuario, Perfil, Oferta, Interacao e Compatibilidade validado.

### TASK-SPEC-003: Revisar contratos

Requisito relacionado:

- EF-001
- EF-007

Arquivos:

- `specs/elo-framework/contracts.md`

Resultado esperado:

- contratos minimos aprovados antes da implementacao.

## Tarefas de Backend

### TASK-BE-001: Extrair contrato de perfil

Requisito relacionado:

- EF-001
- EF-002
- EF-007

Objetivo:

- Criar contrato minimo para perfis usados pelo framework.

Validacao:

- backend compila;
- nenhum fluxo existente do Apto quebra.

### TASK-BE-002: Extrair contrato de oferta

Requisito relacionado:

- EF-001
- EF-002
- EF-007

Objetivo:

- Criar contrato minimo para ofertas publicadas.

Validacao:

- backend compila;
- `Anuncio` pode ser explicado como oferta da instancia Apto.

### TASK-BE-003: Extrair contrato de manifestacao de interesse

Requisito relacionado:

- EF-001
- EF-003
- EF-007

Objetivo:

- Criar contrato minimo para Manifestacao de Interesse como mecanismo fixo de interacao.

Validacao:

- `ManifestacaoInteresse` continua funcionando;
- documentacao deixa claro que interacao e ponto fixo.

### TASK-BE-004: Extrair contrato de compatibilidade

Requisito relacionado:

- EF-001
- EF-002
- EF-007

Objetivo:

- Criar contrato ou strategy para calculo de compatibilidade.

Validacao:

- compatibilidade do Apto continua funcionando;
- novas estrategias podem ser adicionadas sem alterar o fluxo fixo.

### TASK-BE-004A: Criar CompatibilidadeService

Requisito relacionado:

- EF-001
- EF-004
- EF-007

Objetivo:

- Criar `CompatibilidadeService<T>` como fluxo fixo do framework para aplicar elegibilidade, calcular compatibilidades, ordenar resultados e limitar a quantidade retornada.
- Priorizar este service como primeira instancia operacional do Elo Framework no Apto.

Arquivos:

- `backend/apto-api/src/main/java/com/apto/framework/CompatibilidadeService.java`
- `backend/apto-api/src/main/java/com/apto/framework/CompatibilidadeStrategy.java`

Validacao:

- backend compila;
- teste unitario cobre o fluxo com uma strategy fake ou com a strategy do Apto;
- o service nao depende de classes especificas do Apto.

Risco:

- duplicar responsabilidades do `MatchmakingService`;
- alterar ordenacao ou filtragem de compatibilidade sem perceber.

### TASK-BE-005: Adaptar Apto como instancia

Requisito relacionado:

- EF-004

Objetivo:

- Mapear perfil de convivencia, anuncio, manifestacao de interesse e matchmaking como elementos da instancia Apto.
- Fazer `CompatibilidadeDeterministicaCalculator` implementar `CompatibilidadeStrategy<UsuarioUniversitario>`.
- Fazer `MatchmakingService` usar `CompatibilidadeService<UsuarioUniversitario>` preservando o comportamento atual.

Validacao:

- testes existentes passam;
- README ou documentacao descreve Apto como instancia do Elo Framework.

Risco:

- quebrar o fallback deterministico usado quando a integracao LLM falha;
- mudar contratos REST existentes de matchmaking.

### TASK-BE-006: Implementar Study Buddy minimo

Requisito relacionado:

- EF-005

Objetivo:

- Criar representacao minima de perfil academico, grupo de estudo e compatibilidade academica.

Validacao:

- teste comprova compatibilidade por disciplina, horario, objetivo e nivel.

### TASK-BE-007: Implementar Mentor Match minimo

Requisito relacionado:

- EF-006

Objetivo:

- Criar representacao minima de perfil de mentoria, sessao de mentoria e compatibilidade de mentoria.

Validacao:

- teste comprova compatibilidade por area, objetivo, experiencia e disponibilidade.

### TASK-BE-008: Adicionar endpoint ou exemplo demonstrativo

Requisito relacionado:

- EF-005
- EF-006

Objetivo:

- Expor ou documentar exemplos de instanciacao.

Validacao:

- equipe consegue demonstrar as tres instancias.

Observacao:

- Esta tarefa so deve ser feita se a apresentacao precisar de demonstracao via API.

## Tarefas de Testes

### TASK-TEST-001: Garantir regressao do Apto

Requisito relacionado:

- EF-004

Objetivo:

- Rodar testes existentes do backend.

Validacao:

```bash
cd backend/apto-api
./mvnw test
```

### TASK-TEST-002: Testar variacao de compatibilidade

Requisito relacionado:

- EF-005
- EF-006
- EF-007

Objetivo:

- Testar que Study Buddy e Mentor Match usam criterios diferentes.

Validacao:

- testes unitarios para cada strategy.

## Tarefas de Documentacao

### TASK-DOC-001: Atualizar README

Requisito relacionado:

- EF-001
- EF-004
- EF-005
- EF-006

Objetivo:

- Explicar que o Apto passou a ser uma instancia do Elo Framework.

Validacao:

- README descreve pontos fixos, pontos variaveis e instancias.

### TASK-DOC-002: Atualizar matriz de rastreabilidade

Requisito relacionado:

- todos

Objetivo:

- Relacionar requisitos, tarefas, arquivos, testes e commits.

Validacao:

- `traceability.md` preenchido apos implementacao.

