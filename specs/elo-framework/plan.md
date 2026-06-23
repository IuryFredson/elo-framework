# Plan: Elo Framework

## Estrategia

Implementar a evolucao para o Elo Framework de forma incremental e demonstravel, preservando o Apto e evitando uma reescrita completa do backend.

O plano tecnico deve partir da especificacao em `spec.md` e manter o foco em evidenciar:

- pontos fixos;
- pontos variaveis;
- reuso;
- extensibilidade;
- instanciacao em Apto, Study Buddy e Mentor Match.

## Principios de Implementacao

1. Preservar comportamento existente do Apto.
2. Refatorar apenas onde houver ganho claro para demonstrar framework.
3. Introduzir contratos pequenos e compreensiveis.
4. Evitar novas dependencias.
5. Manter testes focados nos pontos de extensao.
6. Nao tratar interacao como ponto variavel principal.

## Arquitetura Proposta

### Camada do framework

Criar uma camada conceitual para representar os pontos fixos:

- Usuario.
- Perfil.
- Oferta.
- Interacao.
- Compatibilidade.

Essa camada pode ser implementada por interfaces, classes abstratas, services ou contratos simples, conforme aderencia ao codigo atual.

Para a entrega parcial, a prioridade de implementacao deve comecar por compatibilidade, pois ela demonstra com clareza a separacao entre fluxo fixo do framework e criterio variavel da instancia:

- `CompatibilidadeStrategy<T>` representa o ponto flexivel de criterios de compatibilidade.
- `CompatibilidadeService<T>` representa o fluxo fixo do framework para filtrar candidatos elegiveis, calcular compatibilidades, ordenar resultados e limitar retornos.
- `CompatibilidadeDeterministicaCalculator` instancia `CompatibilidadeStrategy<UsuarioUniversitario>` para o Apto.
- `MatchmakingService` deve orquestrar o caso de uso atual reaproveitando `CompatibilidadeService<UsuarioUniversitario>`, preservando LLM, fallback e respostas publicas.

### Instancia Apto

O Apto deve continuar sendo a instancia principal e funcional.

O codigo existente ja possui:

- `Usuario` e especializacoes;
- `PerfilConvivencia`;
- `PerfilAnunciante`;
- `Anuncio`;
- `ManifestacaoInteresse`;
- `MatchmakingService`;
- `CompatibilidadeDeterministicaCalculator`.

A refatoracao deve reaproveitar esses elementos como evidencia da instancia Apto.

No Apto, `Anuncio` deve ser tratado como oferta publicada por meio de `PerfilAnunciante`, que representa o papel de publicador assumido por `Locador` ou `UsuarioUniversitario`.

### Instancias Study Buddy e Mentor Match

As novas instancias devem ser minimas.

Elas nao precisam ter todo o fluxo produtivo do Apto, mas precisam demonstrar:

- dados de perfil diferentes;
- tipo de oferta diferente;
- criterio de compatibilidade diferente.

## Fases

### Fase 1: Documentar e estabilizar a especificacao

Objetivo:

- Aprovar `spec.md`.
- Confirmar pontos fixos e variaveis.
- Confirmar limites de escopo.

Entregaveis:

- `spec.md`.
- `research.md`.
- `data-model.md`.
- `contracts.md`.

### Fase 2: Extrair contratos minimos

Objetivo:

- Criar contratos de perfil, oferta, manifestacao de interesse e compatibilidade.
- Criar o fluxo fixo `CompatibilidadeService<T>` antes de alterar o `MatchmakingService`.
- Evitar alterar comportamento existente.

Possiveis entregaveis:

- contrato de perfil;
- contrato de oferta;
- contrato de manifestacao de interesse;
- contrato de estrategia de compatibilidade.
- service fixo de compatibilidade.

### Fase 3: Adaptar Apto como instancia

Objetivo:

- Mapear o Apto atual para os contratos.
- Usar `CompatibilidadeService<UsuarioUniversitario>` como primeira instancia operacional do Elo Framework no Apto.
- Manter matchmaking atual funcionando.
- Explicar que o match atual compara usuarios universitarios por perfil de convivencia.

Possiveis entregaveis:

- `CompatibilidadeDeterministicaCalculator` implementando a strategy de compatibilidade do Apto;
- `MatchmakingService` usando o fluxo fixo de `CompatibilidadeService<T>`;
- testes garantindo compatibilidade existente.

### Fase 4: Implementar Study Buddy minimo

Objetivo:

- Demonstrar uma segunda instancia.

Possiveis entregaveis:

- perfil academico;
- oferta de grupo de estudo;
- estrategia de compatibilidade academica;
- teste de compatibilidade.

### Fase 5: Implementar Mentor Match minimo

Objetivo:

- Demonstrar uma terceira instancia.

Possiveis entregaveis:

- perfil de mentoria;
- oferta de sessao de mentoria;
- estrategia de compatibilidade de mentoria;
- teste de compatibilidade.

### Fase 6: Validacao e apresentacao

Objetivo:

- Garantir alinhamento com especificacao e slides.
- Preparar evidencias para apresentacao.

Possiveis entregaveis:

- testes passando;
- README atualizado;
- matriz de rastreabilidade;
- exemplos de uso.

## Riscos

### Risco: refatoracao excessiva

Mitigacao:

- Priorizar adapters e contratos pequenos.
- Evitar mover todo o dominio para pacotes genericos.

### Risco: quebrar o Apto

Mitigacao:

- Rodar testes existentes.
- Preservar controllers e services publicos.

### Risco: confundir ponto fixo e ponto variavel

Mitigacao:

- Usar `spec.md` como referencia.
- Manter interacao como ponto fixo em todos os documentos.

### Risco: instancia minima parecer artificial

Mitigacao:

- Garantir que Study Buddy e Mentor Match tenham criterios de compatibilidade realmente diferentes.
- Relacionar cada instancia aos pontos variaveis definidos.

## Validacoes Tecnicas

Comandos esperados:

```bash
cd backend/apto-api
./mvnw test
```

Se houver alteracoes no frontend:

```bash
cd frontend
npm run lint
npm run build
```

## Politica de Commits

Mensagens devem informar o proposito da mudanca.

Exemplos:

```text
docs: specify elo framework variation points
refactor: extract compatibility extension point
feat: implement study buddy compatibility instance
feat: implement mentor match compatibility instance
test: cover elo framework compatibility strategies
```

Quando houver adaptacao de sugestao da LLM, registrar isso na mensagem ou descricao do commit.

