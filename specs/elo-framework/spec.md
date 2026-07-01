# Spec: Elo Framework

## Objetivo

Evoluir o Apto, aplicação da Fase 1 da disciplina Projeto Detalhado de Software, para o **Elo Framework** na Fase 2.

O Elo Framework é um framework Spring/JPA híbrido para plataformas baseadas em usuários, perfis, ofertas, manifestação de interesse e compatibilidade. O núcleo fica em `backend/elo-core` e controla fluxos comuns por Template Method. A aplicação Apto fica em `backend/apto` e instancia os pontos flexíveis com regras do domínio de moradias universitárias.

## Escopo Final

### Dentro do escopo

- Preservar o Apto como aplicação funcional.
- Extrair contratos e fluxos comuns para `elo-core`.
- Demonstrar, no código e no diagrama, que o Apto instancia o framework.
- Manter dependência unidirecional: `apto -> elo-core`.
- Documentar hooks obrigatórios para uma futura instância.
- Validar a arquitetura com testes do core usando implementações falsas e testes de regressão do Apto.

### Fora do escopo

- Autenticação real.
- Deploy de produção.
- Novo frontend.
- Reescrita completa do domínio.
- Implementação de Study Buddy.
- Implementação de Mentor Match.
- Generalização de avaliação, reputação ou moradia como pontos obrigatórios do framework.

## Pontos Fixos

Os pontos fixos são comportamentos controlados pelo framework:

1. Cadastro e gestão de usuários.
2. Cadastro e atualização de perfis.
3. Publicação e gestão de ofertas.
4. Manifestação de Interesse.
5. Denúncia e moderação de ofertas.
6. Cálculo de compatibilidade e matching.

## Pontos Flexíveis

Os pontos flexíveis são especializações fornecidas pela instância:

1. Dados do perfil.
2. Tipo de oferta publicada.
3. Critérios de compatibilidade.
4. Critério de denúncia.
5. Integração LLM, quando a instância quiser usar compatibilidade assistida.
6. DTOs, mappers, repositories, controllers e mensagens de erro.

## Regra Sobre Manifestação de Interesse

Manifestação de Interesse é ponto fixo.

O framework define o fluxo comum:

- criar manifestação;
- validar oferta ativa;
- impedir interesse na própria oferta;
- impedir duplicidade ativa;
- aceitar, recusar e cancelar;
- listar por oferta ou interessado;
- cancelar manifestações pendentes quando uma oferta fica indisponível.

O que varia entre instâncias é a oferta alvo. No Apto, a manifestação é feita em um anúncio de moradia ou vaga.

## Arquitetura Final

### Núcleo: `elo-core`

O núcleo contém contratos e templates independentes de `com.apto`:

- `com.elo.usuario.Usuario`
- `com.elo.usuario.UsuarioService`
- `com.elo.perfil.Perfil`
- `com.elo.perfil.PerfilService`
- `com.elo.oferta.Oferta`
- `com.elo.oferta.OfertaService`
- `com.elo.manifestacao.ManifestacaoInteresse`
- `com.elo.manifestacao.ManifestacaoInteresseService`
- `com.elo.denuncia.Denuncia`
- `com.elo.denuncia.DenunciaService`
- `com.elo.denuncia.CriterioDenuncia`
- `com.elo.moderacao.ModeracaoService`
- `com.elo.compatibilidade.CompatibilidadeStrategy`
- `com.elo.compatibilidade.MatchingService`
- `com.elo.compatibilidade.ProvedorCompatibilidadeLlm`

Os testes do core usam implementações falsas para provar que os templates funcionam sem depender do Apto.

### Instância: `apto`

O Apto fornece entidades, DTOs, repositories, mappers, controllers e regras específicas:

- `UsuarioUniversitario` e `Locador` especializam `Usuario`.
- `PerfilConvivencia` implementa `Perfil`.
- `Anuncio` implementa `Oferta`.
- `ManifestacaoInteresse` implementa o contrato fixo de manifestação.
- `Denuncia` implementa o contrato de denúncia.
- `CriterioDenunciaApto` implementa `CriterioDenuncia`.
- `CompatibilidadeDeterministicaCalculator` implementa os critérios de convivência.
- `AptoCompatibilidadeLlmProvider` fornece a porta LLM usando Groq, prompt e parser do Apto.

## Requisitos

### EF-001: Núcleo reutilizável

THE Elo Framework SHALL provide reusable templates for user management, profile management, offer publication, interest manifestation, complaint/moderation, and compatibility/matching.

Critérios de aceitação:

- `elo-core` não deve depender de `com.apto`.
- Os fluxos comuns devem estar em templates ou contratos do core.
- O Apto deve estender os templates sem reimplementar o algoritmo fixo inteiro.

### EF-002: Pontos flexíveis limitados

THE Elo Framework SHALL keep the primary variation points limited to profile data, published offer type, and compatibility criteria.

Critérios de aceitação:

- `PerfilConvivencia` deve representar os dados variáveis do perfil no Apto.
- `Anuncio` e `Moradia` devem representar a oferta concreta do Apto.
- `CompatibilidadeDeterministicaCalculator` deve representar os critérios de compatibilidade do Apto.

### EF-003: Manifestação de Interesse como ponto fixo

THE Elo Framework SHALL treat interest manifestation as a fixed interaction mechanism.

Critérios de aceitação:

- Manifestação de Interesse não deve ser ponto flexível.
- O core deve controlar criação, resposta, cancelamento, autorização, duplicidade e transições.
- O Apto deve fornecer persistência, mapeamento e exceções.

### EF-004: Apto instanciado no framework

WHERE the Apto instance is used, THE SYSTEM SHALL preserve existing housing behavior while extending the framework contracts and templates.

Critérios de aceitação:

- Controllers e DTOs públicos do Apto devem continuar compatíveis.
- Testes do Apto devem passar.
- O diagrama deve mostrar classes do Apto implementando/extending contratos do `elo-core`.

### EF-005: Matching com fallback

THE Elo Framework SHALL provide a matching flow with eligibility, LLM result usage, deterministic fallback, ordering and `topN`.

Critérios de aceitação:

- O core deve identificar diretamente o candidato no resultado.
- Falha de LLM ou resultado ausente deve acionar fallback determinístico.
- O Apto deve manter prompt, parser, Groq e mapper como detalhes da instância.

### EF-006: Denúncia e moderação no framework

THE Elo Framework SHALL provide fixed complaint and moderation flows while allowing instance-specific complaint criteria.

Critérios de aceitação:

- Estados de denúncia e máquina de estados devem estar no core.
- Ações de pausar/encerrar oferta devem ser aplicadas por porta/hook.
- `CriterioDenunciaApto` deve representar critérios específicos do Apto.

### EF-007: Extensibilidade controlada

WHEN a future application instance is created, THE SYSTEM SHALL require implementation of hooks/contracts instead of changes in fixed framework algorithms.

Critérios de aceitação:

- A documentação deve listar hooks obrigatórios.
- Study Buddy e Mentor Match devem aparecer apenas como exemplos futuros, sem plano de implementação nesta entrega.

## Hooks Obrigatórios para uma Futura Instância

Uma futura instância deve fornecer:

- Entidade concreta de usuário, se precisar especializar `Usuario`.
- Perfil concreto implementando `Perfil`.
- Oferta concreta implementando `Oferta`.
- Manifestação concreta implementando `ManifestacaoInteresse`.
- Denúncia concreta implementando `Denuncia`, se usar denúncia/moderação.
- Critério de denúncia implementando `CriterioDenuncia`, se usar denúncia/moderação.
- Estratégia de compatibilidade implementando `CompatibilidadeStrategy<P>`.
- Provedor LLM implementando `ProvedorCompatibilidadeLlm<U, P>`, se usar LLM.
- Services concretos estendendo os templates do core.
- Repositories compatíveis com `RepositorioBase`.
- DTOs, mappers, controllers e exceções da instância.

## Exemplos Futuros Fora do Escopo

Study Buddy poderia variar:

- Perfil: dados acadêmicos.
- Oferta: grupo de estudo.
- Compatibilidade: disciplina, horário, objetivo e nível.

Mentor Match poderia variar:

- Perfil: dados de mentoria.
- Oferta: sessão ou programa de mentoria.
- Compatibilidade: área, objetivo, experiência e disponibilidade.

Esses exemplos demonstram extensibilidade conceitual, mas não fazem parte da implementação atual.

## Critérios Gerais de Aceitação

- `mvn test` no reactor `backend` deve passar.
- `elo-core` deve permanecer livre de dependências para `com.apto`.
- Apto deve preservar seus endpoints e comportamentos essenciais.
- Diagrama, contratos, modelo, tarefas, plano e README devem refletir a arquitetura final.
- Interações com LLM devem continuar registradas nos documentos do estudo.
