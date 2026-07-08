# Research: Elo Framework

## Objetivo

Registrar investigacoes, decisoes e alternativas avaliadas durante a evolucao do Apto para o Elo Framework.

## Estado Final do Projeto

Estrutura principal:

- `backend/elo-core`: nucleo reutilizavel do framework.
- `backend/apto`: instancia Apto e API Spring Boot.
- `backend/study-buddy`: instancia Study Buddy e API Spring Boot.
- `backend/mentor-match`: instancia Mentor Match e API Spring Boot.
- `frontend`: aplicacao React/Vite do Apto.
- `frontend-study-buddy`: aplicacao React/Vite do Study Buddy.
- `frontend-mentor-match`: aplicacao React/Vite do Mentor Match.
- `specs`: documentacao SDD, contratos, modelo, tarefas, rastreabilidade e diagramas.

## Elementos do Apto que Instanciam o Framework

### Usuarios

O core possui `Usuario` e `UsuarioService`.

O Apto especializa:

- `UsuarioUniversitario`;
- `Locador`.

### Perfis

O core possui `Perfil` e `PerfilService`.

O Apto instancia:

- `PerfilConvivencia`.

### Ofertas

O core possui `Oferta` e `OfertaService`.

O Apto instancia:

- `Anuncio`, associado a `Moradia`.

### Manifestacao de Interesse

O core possui `ManifestacaoInteresse`, `StatusManifestacaoInteresse` e `ManifestacaoInteresseService`.

O Apto instancia:

- `ManifestacaoInteresse` entre interessado e anuncio.

### Denuncia e Moderacao

O core possui `Denuncia`, `StatusDenuncia`, `CriterioDenuncia`, `DenunciaService` e `ModeracaoService`.

O Apto instancia:

- `Denuncia`;
- `CriterioDenunciaApto`;
- moderacao de anuncios.

### Compatibilidade

O core possui `CompatibilidadeStrategy`, `MatchingService`, `ProvedorCompatibilidadeLlm`, `ResultadoCompatibilidade` e `ResultadoMatching`.

O Apto instancia:

- `CompatibilidadeDeterministicaCalculator`;
- `AptoCompatibilidadeLlmProvider`;
- `MatchmakingService`.

## Elementos do Study Buddy que Instanciam o Framework

### Usuarios e Perfis

Study Buddy instancia:

- `Estudante`;
- `EstudanteService`;
- `PerfilAcademico`;
- `PerfilAcademicoService`.

### Ofertas e Manifestacoes

Study Buddy instancia:

- `GrupoEstudo`;
- `GrupoEstudoService`;
- `ManifestacaoInteresseGrupo`;
- `ManifestacaoInteresseGrupoService`.

### Denuncia, Moderacao e Compatibilidade

Study Buddy instancia:

- `DenunciaGrupoEstudo`;
- `CriterioDenunciaStudyBuddy`;
- `DenunciaGrupoEstudoService`;
- `ModeracaoGrupoEstudoService`;
- `CompatibilidadeAcademicaCalculator`;
- `StudyBuddyCompatibilidadeLlmProvider`;
- `StudyBuddyMatchingService`.

## Elementos do Mentor Match que Instanciam o Framework

### Participantes e Perfis

Mentor Match instancia:

- `Aluno`;
- `Mentor`;
- `PerfilMentoria`;
- services, repositories, mappers e controllers especificos.

### Ofertas, Solicitacoes e Participantes

Mentor Match instancia:

- `SessaoMentoria` como oferta concreta;
- `SolicitacaoMentoria`;
- `ParticipanteMentoria`.

### Denuncia, Moderacao e Compatibilidade

Mentor Match instancia:

- `DenunciaSessaoMentoria`;
- `CriterioDenunciaMentorMatch`;
- moderacao de sessoes;
- `CompatibilidadeMentoriaCalculator`;
- `MentorMatchCompatibilidadeLlmProvider`;
- `MentorMatchingService`.

## Decisoes

### Decisao 1: Preservar o Apto como instancia original

Motivo:

- O Apto e o produto da Fase 1.
- A Fase 2 deve demonstrar evolucao, nao substituicao.
- Os endpoints e fluxos publicos precisavam continuar funcionando.

Consequencia:

- Refatoracoes foram incrementais.
- DTOs, controllers, mappers e repositories concretos permaneceram no Apto.

### Decisao 2: Usar Template Method no core

Motivo:

- O framework deve controlar fluxos fixos.
- A instancia deve fornecer hooks especificos.
- Evita que o algoritmo comum continue espalhado no Apto.

Consequencia:

- Services das instancias passaram a estender templates do core.
- Testes do core usam implementacoes falsas para provar independencia.

### Decisao 3: Manifestacao de Interesse e ponto fixo

Motivo:

- A orientacao conceitual do projeto define Manifestacao de Interesse como mecanismo comum.
- O que muda e o tipo de oferta alvo.

Consequencia:

- Manifestacao de Interesse nao virou ponto flexivel.
- O core controla transicoes, autorizacao, duplicidade e cancelamento de pendentes.

### Decisao 4: Study Buddy como segunda instancia concreta

Motivo:

- Demonstrar reutilizacao real do framework em outro dominio.
- Manter a instancia separada do Apto e dependente apenas de `elo-core`.

Consequencia:

- Study Buddy foi implementado em `backend/study-buddy`.
- Frontend dedicado foi criado em `frontend-study-buddy`.
- O core nao depende de `com.studybuddy`.

### Decisao 5: Mentor Match como terceira instancia concreta

Motivo:

- Demonstrar extensibilidade adicional do framework em um terceiro dominio.
- Reutilizar os mesmos contratos para usuarios, perfis, ofertas, denuncia/moderacao e matching.

Consequencia:

- Mentor Match foi implementado em `backend/mentor-match`.
- Frontend dedicado foi criado em `frontend-mentor-match`.
- O core nao depende de `com.mentormatch`.

### Decisao 6: Remover Observer/Event Publisher

Motivo:

- O mecanismo adicionava indirecao sem ser parte essencial do framework.
- A Etapa 08 exigiu isolamento de funcionalidades do Apto.

Consequencia:

- Cancelamento de manifestacoes passou a ser chamada direta.
- Reputacao passou a ser recalculada diretamente.
- Notificacoes de anuncio indisponivel foram removidas.

### Decisao 7: Generalizar apenas a base Groq

Motivo:

- As tres instancias usam Groq, mas cada uma tem excecoes, propriedades, prompt e parser proprios.

Consequencia:

- `AbstractGroqChatClient` ficou no core.
- `GroqClient` concreto permaneceu em cada instancia.

## Alternativas Rejeitadas

### Generalizar avaliacao e reputacao

Rejeitada porque avaliacao e reputacao sao especificas do Apto nesta entrega.

### Criar um frontend multi-instancia unico

Rejeitada porque os frontends separados mantem o escopo mais claro e reduzem acoplamento entre dominios.

### Usar adapters para tudo

Rejeitada quando a implementacao direta do contrato era natural. Exemplo: perfis e ofertas implementando contratos do core diretamente.

## Recomendacoes Futuras

Trabalhos futuros podem:

- publicar `elo-core` como biblioteca independente;
- criar autenticacao real;
- criar frontend multi-instancia;
- preparar deploy de producao;
- avaliar se reputacao deve virar ponto opcional do framework.

Essas recomendacoes nao fazem parte da entrega atual.
