# Spec: Elo Framework

## Objetivo

Evoluir a aplicacao Apto, criada na Fase 1 da disciplina Projeto Detalhado de Software, para o **Elo Framework**.

O Elo Framework e um framework Spring/JPA hibrido para plataformas baseadas em usuarios, perfis, ofertas, manifestacoes de interesse, denuncia/moderacao e compatibilidade. O nucleo reutilizavel fica em `backend/elo-core` e controla fluxos comuns por Template Method. As instancias finais do projeto sao:

- `backend/apto`: moradias universitarias.
- `backend/study-buddy`: grupos de estudo.
- `backend/mentor-match`: mentorias.

## Escopo Final

### Dentro do escopo

- Preservar o Apto como aplicacao funcional.
- Extrair contratos e fluxos comuns para `elo-core`.
- Demonstrar o Apto como instancia principal do framework.
- Demonstrar Study Buddy como instancia concreta para grupos de estudo.
- Demonstrar Mentor Match como terceira instancia concreta para mentorias.
- Manter dependencia unidirecional das instancias para o core.
- Documentar hooks obrigatorios para novas instancias.
- Validar a arquitetura com testes do core e testes das instancias.
- Manter frontends separados para as tres instancias.

### Fora do escopo

- Autenticacao real.
- Deploy de producao.
- Empacotamento do `elo-core` como biblioteca externa publicada.
- Reescrita completa dos dominios.
- Generalizacao de avaliacao, reputacao ou moradia como pontos obrigatorios do framework.

## Pontos Fixos

Os pontos fixos sao comportamentos controlados pelo framework:

1. Cadastro e gestao de usuarios.
2. Cadastro e atualizacao de perfis.
3. Publicacao e gestao de ofertas.
4. Manifestacao de Interesse.
5. Denuncia e moderacao de ofertas, quando a instancia usa esse fluxo.
6. Calculo de compatibilidade e matching.

## Pontos Flexiveis

Os pontos flexiveis sao especializacoes fornecidas pela instancia:

1. Dados do perfil.
2. Tipo de oferta publicada.
3. Criterios de compatibilidade.
4. Criterio de denuncia, quando houver denuncia/moderacao.
5. Integracao LLM, quando a instancia quiser usar compatibilidade assistida.
6. DTOs, mappers, repositories, controllers e mensagens de erro.

## Regra Sobre Manifestacao de Interesse

Manifestacao de Interesse e ponto fixo.

O framework define o fluxo comum:

- criar manifestacao;
- validar oferta ativa;
- impedir interesse na propria oferta;
- impedir duplicidade ativa;
- aceitar, recusar e cancelar;
- listar por oferta ou interessado;
- cancelar manifestacoes pendentes quando uma oferta fica indisponivel.

O que varia entre instancias e a oferta alvo. No Apto, a manifestacao e feita em um anuncio de moradia ou vaga. No Study Buddy, em um grupo de estudo. No Mentor Match, em uma sessao ou solicitacao de mentoria conforme o fluxo da instancia.

## Arquitetura Final

### Nucleo: `elo-core`

O nucleo contem contratos e templates independentes das instancias:

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
- `com.elo.compatibilidade.llm.groq.AbstractGroqChatClient`

O core nao referencia `com.apto`, `com.studybuddy` ou `com.mentormatch`.

### Instancia: `apto`

O Apto fornece entidades, DTOs, repositories, mappers, controllers e regras especificas:

- `UsuarioUniversitario` e `Locador` especializam `Usuario`.
- `PerfilConvivencia` implementa `Perfil`.
- `Anuncio` implementa `Oferta`.
- `ManifestacaoInteresse` implementa o contrato fixo de manifestacao.
- `Denuncia` implementa o contrato de denuncia.
- `CriterioDenunciaApto` implementa `CriterioDenuncia`.
- `CompatibilidadeDeterministicaCalculator` implementa os criterios de convivencia.
- `AptoCompatibilidadeLlmProvider` integra Groq, prompt e parser do Apto.

### Instancia: `study-buddy`

Study Buddy fornece regras especificas para grupos de estudo:

- `Estudante` especializa `Usuario`.
- `PerfilAcademico` implementa `Perfil`.
- `GrupoEstudo` implementa `Oferta`.
- `ManifestacaoInteresseGrupo` implementa o contrato fixo de manifestacao.
- `DenunciaGrupoEstudo` implementa o contrato de denuncia.
- `CriterioDenunciaStudyBuddy` implementa `CriterioDenuncia`.
- `CompatibilidadeAcademicaCalculator` implementa os criterios academicos.
- `StudyBuddyCompatibilidadeLlmProvider` integra a porta LLM com Groq e fallback deterministico.
- Controllers da instancia ficam sob `/study-buddy`.

### Instancia: `mentor-match`

Mentor Match fornece regras especificas para mentorias:

- `Aluno` e `Mentor` especializam usuarios da plataforma.
- `PerfilMentoria` representa dados de perfil de mentoria.
- `SessaoMentoria` representa a oferta concreta de mentoria.
- `SolicitacaoMentoria` e `ParticipanteMentoria` representam o fluxo especifico de solicitacao/participacao.
- `DenunciaSessaoMentoria` e `CriterioDenunciaMentorMatch` instanciam denuncia/moderacao.
- `CompatibilidadeMentoriaCalculator` implementa criterios de compatibilidade de mentoria.
- `MentorMatchCompatibilidadeLlmProvider` integra Groq, prompt e parser da instancia.
- Controllers da instancia ficam sob `/mentor-match`.

## Requisitos

### EF-001: Nucleo reutilizavel

THE Elo Framework SHALL provide reusable templates for user management, profile management, offer publication, interest manifestation, complaint/moderation, and compatibility/matching.

Criterios de aceitacao:

- `elo-core` nao deve depender das instancias.
- Os fluxos comuns devem estar em templates ou contratos do core.
- As instancias devem estender os templates sem reimplementar o algoritmo fixo inteiro.

### EF-002: Pontos flexiveis limitados

THE Elo Framework SHALL keep the primary variation points limited to profile data, published offer type, and compatibility criteria.

Criterios de aceitacao:

- Apto, Study Buddy e Mentor Match devem fornecer perfis concretos.
- Apto, Study Buddy e Mentor Match devem fornecer ofertas concretas.
- Cada instancia deve fornecer criterios proprios de compatibilidade.

### EF-003: Manifestacao de Interesse como ponto fixo

THE Elo Framework SHALL treat interest manifestation as a fixed interaction mechanism.

Criterios de aceitacao:

- Manifestacao de Interesse nao deve ser ponto flexivel.
- O core deve controlar criacao, resposta, cancelamento, autorizacao, duplicidade e transicoes.
- Cada instancia deve fornecer persistencia, mapeamento e excecoes.

### EF-004: Apto instanciado no framework

WHERE the Apto instance is used, THE SYSTEM SHALL preserve existing housing behavior while extending the framework contracts and templates.

Criterios de aceitacao:

- Controllers e DTOs publicos do Apto devem continuar compativeis.
- Testes do Apto devem passar.
- O diagrama deve mostrar classes do Apto implementando/extending contratos do `elo-core`.

### EF-005: Matching com fallback

THE Elo Framework SHALL provide a matching flow with eligibility, LLM result usage, deterministic fallback, ordering and `topN`.

Criterios de aceitacao:

- O core deve identificar diretamente o candidato no resultado.
- Falha de LLM ou resultado ausente deve acionar fallback deterministico.
- Cada instancia deve manter prompt, parser, Groq e mapper como detalhes proprios.

### EF-006: Denuncia e moderacao no framework

THE Elo Framework SHALL provide fixed complaint and moderation flows while allowing instance-specific complaint criteria.

Criterios de aceitacao:

- Estados de denuncia e maquina de estados devem estar no core.
- Acoes de pausar/encerrar oferta devem ser aplicadas por porta/hook.
- Cada instancia com denuncia deve fornecer criterio proprio.

### EF-007: Extensibilidade controlada

WHEN a new application instance is created, THE SYSTEM SHALL require implementation of hooks/contracts instead of changes in fixed framework algorithms.

Criterios de aceitacao:

- A documentacao deve listar hooks obrigatorios.
- Apto, Study Buddy e Mentor Match devem aparecer como instancias concretas separadas.
- O core deve permanecer independente das instancias.

### EF-008: Study Buddy instanciado no framework

WHERE the Study Buddy instance is used, THE SYSTEM SHALL reuse Elo Framework templates for users, profiles, offers, interest manifestation, complaint/moderation and matching.

Criterios de aceitacao:

- Services de usuario, perfil, oferta, manifestacao, denuncia/moderacao e matching devem reutilizar templates/contratos do core.
- Controllers da instancia devem ficar sob `/study-buddy`.
- O core nao deve depender de `com.studybuddy`.

### EF-009: Mentor Match instanciado no framework

WHERE the Mentor Match instance is used, THE SYSTEM SHALL reuse Elo Framework contracts for mentoring users, profiles, mentoring sessions, requests, complaint/moderation and matching.

Criterios de aceitacao:

- Mentor Match deve depender de `elo-core`.
- Controllers da instancia devem ficar sob `/mentor-match`.
- Matching deve usar criterios de mentoria e fallback deterministico.
- O core nao deve depender de `com.mentormatch`.

## Hooks Obrigatorios para uma Nova Instancia

Uma nova instancia deve fornecer:

- entidade concreta de usuario, se precisar especializar `Usuario`;
- perfil concreto implementando `Perfil`;
- oferta concreta implementando `Oferta`;
- manifestacao concreta implementando `ManifestacaoInteresse`, quando usar manifestacao;
- denuncia concreta implementando `Denuncia`, se usar denuncia/moderacao;
- criterio de denuncia implementando `CriterioDenuncia`, se usar denuncia/moderacao;
- estrategia de compatibilidade implementando `CompatibilidadeStrategy<P>`;
- provedor LLM implementando `ProvedorCompatibilidadeLlm<U, P>`, se usar LLM;
- services concretos estendendo os templates do core;
- repositories compativeis com `RepositorioBase`;
- DTOs, mappers, controllers e excecoes da instancia.

## Instancias Concretas

Apto varia perfil de convivencia, anuncio de moradia/vaga, criterios de convivencia e criterios de denuncia de anuncio.

Study Buddy varia perfil academico, grupo de estudo, criterios academicos e criterios de denuncia de grupo.

Mentor Match varia perfil de mentoria, sessao de mentoria, criterios de mentoria e criterios de denuncia de sessao.

## Criterios Gerais de Aceitacao

- `mvn test` no reactor `backend` deve passar.
- `elo-core` deve permanecer livre de dependencias para as instancias.
- Apto deve preservar seus endpoints e comportamentos essenciais.
- Study Buddy deve expor endpoints sob `/study-buddy`.
- Mentor Match deve expor endpoints sob `/mentor-match`.
- Diagramas, contratos, modelo, tarefas, plano, traceability e README devem refletir a arquitetura final.
- Interacoes com LLM devem continuar registradas nos documentos do estudo.
