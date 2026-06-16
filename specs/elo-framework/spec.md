# Spec: Elo Framework

## Objetivo

Evoluir o Apto, aplicacao da Fase 1, para o **Elo Framework**, um framework para plataformas baseadas em perfis, ofertas e compatibilidade.

O objetivo principal e demonstrar, em contexto academico, que a aplicacao original pode ser reorganizada em torno de pontos fixos reutilizaveis e pontos variaveis especializados por dominio.

## Descricao do Framework

**Nome:** Elo Framework

**Descricao:** Framework para Plataformas Baseadas em Perfis, Ofertas e Compatibilidade.

O framework deve apoiar aplicacoes em que:

- usuarios possuem perfis;
- ofertas sao publicadas;
- interacoes sao registradas;
- compatibilidade e calculada conforme regras de cada dominio.

## Escopo

### Dentro do escopo

- Preservar o Apto como instancia original.
- Evidenciar os pontos fixos do framework.
- Evidenciar os pontos variaveis do framework.
- Definir como Study Buddy e Mentor Match instanciam o framework.
- Refatorar ou organizar o backend apenas o suficiente para demonstrar reuso e extensibilidade.
- Manter testes ou validacoes que comprovem os pontos de extensao.

### Fora do escopo

- Autenticacao real.
- Deploy de producao.
- Reescrita completa do dominio.
- Transformar todo o sistema em uma biblioteca generica perfeita.
- Criar instancias alem de Apto, Study Buddy e Mentor Match.
- Expandir reputacao, moderacao ou eventos como pontos obrigatorios do framework.

## Pontos Fixos

Os pontos fixos sao funcionalidades comuns a todas as instancias:

1. Cadastro e gestao de usuarios.
2. Cadastro de perfis.
3. Publicacao de ofertas.
4. Registro de interacoes.
5. Calculo de compatibilidade.

## Pontos Variaveis

Os pontos variaveis sao especializacoes por dominio:

1. Dados do perfil.
2. Tipo de oferta publicada.
3. Criterios de compatibilidade.

## Regra Sobre Interacoes

O registro de interacoes e um ponto fixo do framework e sera concretizado como **Manifestacao de Interesse** em todas as instancias.

A Manifestacao de Interesse nao deve ser tratada como ponto variavel principal. O mecanismo e reutilizado; o que muda entre as instancias e o tipo de oferta pela qual o usuario manifesta interesse.

Exemplos:

- Apto: manifestacao de interesse em moradia ou vaga.
- Study Buddy: manifestacao de interesse em grupo de estudo.
- Mentor Match: manifestacao de interesse em sessao ou programa de mentoria.

## Instancias

### Apto

Dominio: moradias universitarias.

- Dados do perfil: convivencia, rotina, sono, organizacao e preferencias.
- Tipo de oferta publicada: moradia, vaga ou anuncio de aluguel.
- Criterios de compatibilidade: rotina, convivencia, preferencias e caracteristicas da moradia.
- Interacao fixa do framework: registro da manifestacao de interesse no anuncio.

### Study Buddy

Dominio: grupos e colegas de estudo.

- Dados do perfil: curso, disciplinas, disponibilidade e objetivo de estudo.
- Tipo de oferta publicada: grupo de estudo ou oportunidade de estudo em conjunto.
- Criterios de compatibilidade: disciplina, horario, objetivo e nivel de conhecimento.
- Interacao fixa do framework: registro da manifestacao de interesse no grupo de estudo.

### Mentor Match

Dominio: conexao entre mentores e mentorados.

- Dados do perfil: area de interesse, objetivo, experiencia e disponibilidade.
- Tipo de oferta publicada: sessao ou programa de mentoria.
- Criterios de compatibilidade: area, objetivo, experiencia e disponibilidade.
- Interacao fixa do framework: registro da manifestacao de interesse na sessao ou programa de mentoria.

## Requisitos

### EF-001: Pontos fixos reutilizaveis

THE Elo Framework SHALL define reusable fixed points for user management, profile management, offer publication, interaction registration, and compatibility calculation.

**Criterios de aceitacao:**

- Deve ser possivel identificar no backend os conceitos de usuario, perfil, oferta, interacao e compatibilidade.
- A implementacao deve preservar o comportamento existente do Apto.
- A documentacao deve explicar quais partes sao fixas e por que sao reutilizaveis.

### EF-002: Pontos variaveis limitados

THE Elo Framework SHALL define variation points only for profile data, published offer type, and compatibility criteria.

**Criterios de aceitacao:**

- A especificacao deve listar apenas os tres pontos variaveis definidos.
- Interacao nao deve aparecer como ponto variavel principal.
- Cada instancia deve demonstrar os tres pontos variaveis.

### EF-003: Interacao como ponto fixo

THE Elo Framework SHALL treat interaction registration as a fixed point shared by all instances.

**Criterios de aceitacao:**

- O fluxo de registrar interacao deve ser explicado como mecanismo comum.
- Manifestacao de Interesse deve ser o mecanismo comum de interacao nas tres instancias.
- O significado da manifestacao deve ser determinado pelo tipo de oferta da instancia.

### EF-004: Apto como instancia original

WHERE the Apto instance is used, THE SYSTEM SHALL preserve housing, coexistence profile, housing offers, interest registration, and coexistence compatibility.

**Criterios de aceitacao:**

- O comportamento atual do Apto nao deve ser removido.
- O matchmaking atual entre usuarios universitarios deve continuar funcionando.
- A instancia Apto deve ser descrita como especializacao do Elo Framework.

### EF-005: Study Buddy como instancia demonstravel

WHERE the Study Buddy instance is used, THE SYSTEM SHALL vary profile data to academic data, offer type to study group, and compatibility criteria to discipline, schedule, objective, and knowledge level.

**Criterios de aceitacao:**

- A instancia deve demonstrar perfil academico.
- A instancia deve demonstrar oferta de grupo de estudo.
- A instancia deve demonstrar criterio proprio de compatibilidade.

### EF-006: Mentor Match como instancia demonstravel

WHERE the Mentor Match instance is used, THE SYSTEM SHALL vary profile data to mentorship data, offer type to mentorship session, and compatibility criteria to area, objective, experience, and availability.

**Criterios de aceitacao:**

- A instancia deve demonstrar perfil de mentoria.
- A instancia deve demonstrar oferta de sessao ou programa de mentoria.
- A instancia deve demonstrar criterio proprio de compatibilidade.

### EF-007: Extensibilidade controlada

WHEN a new application instance is defined, THE SYSTEM SHALL allow the instance to specialize profile data, offer type, and compatibility criteria without changing the fixed interaction registration mechanism.

**Criterios de aceitacao:**

- O design deve separar contratos fixos de especializacoes.
- A inclusao de uma instancia deve exigir implementacao de pontos variaveis, nao alteracao do mecanismo fixo de interacao.

## Criterios Gerais de Aceitacao

- A especificacao deve estar alinhada aos slides da Fase 2.
- O backend deve continuar compilando e passando nos testes existentes.
- Mudancas devem ser pequenas e rastreaveis.
- Cada commit deve indicar o proposito academico da mudanca.
- Interacoes com LLM sobre backend devem ser registradas conforme o estudo.

