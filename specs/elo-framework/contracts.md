# Contracts: Elo Framework

## Objetivo

Registrar os contratos publicos e templates do Elo Framework apos a instanciacao de Apto, Study Buddy e Mentor Match.

O core fica em `backend/elo-core` e nao referencia `com.apto`, `com.studybuddy` ou `com.mentormatch`. As instancias fornecem entidades, DTOs, repositories, mappers, controllers, excecoes e regras especificas.

## Contratos de Dominio

### `Usuario`

Local:

- `backend/elo-core/src/main/java/com/elo/usuario/Usuario.java`

Responsabilidade:

- representar o usuario base do framework;
- manter `id`, `nome`, `email`, `telefone` e `ativo`;
- permitir especializacoes por instancia.

Instancias:

- Apto: `UsuarioUniversitario`, `Locador`.
- Study Buddy: `Estudante`.
- Mentor Match: `Aluno`, `Mentor`.

### `Perfil`

Local:

- `backend/elo-core/src/main/java/com/elo/perfil/Perfil.java`

Responsabilidade:

- representar dados usados por busca, recomendacao ou compatibilidade;
- expor `tipoPerfil()`.

Instancias:

- Apto: `PerfilConvivencia`.
- Study Buddy: `PerfilAcademico`.
- Mentor Match: `PerfilMentoria`.

Ponto flexivel:

- dados do perfil.

### `Oferta`

Local:

- `backend/elo-core/src/main/java/com/elo/oferta/Oferta.java`

Responsabilidade:

- representar uma oferta publicada;
- expor `getId()`, `getPublicadorId()`, `tipoOferta()` e `isAtiva()`.

Instancias:

- Apto: `Anuncio`, associado a `Moradia` e `PerfilAnunciante`.
- Study Buddy: `GrupoEstudo`, associado ao `Estudante` publicador.
- Mentor Match: `SessaoMentoria`, associada a mentorias.

Ponto flexivel:

- tipo de oferta publicada.

### `ManifestacaoInteresse`

Local:

- `backend/elo-core/src/main/java/com/elo/manifestacao/ManifestacaoInteresse.java`
- `backend/elo-core/src/main/java/com/elo/manifestacao/StatusManifestacaoInteresse.java`

Responsabilidade:

- representar o mecanismo fixo de interesse em uma oferta;
- expor interessado, oferta e status.

Instancias:

- Apto: `ManifestacaoInteresse`.
- Study Buddy: `ManifestacaoInteresseGrupo`.
- Mentor Match: fluxo de solicitacoes/participantes associado a sessoes de mentoria.

Ponto fixo:

- Manifestacao de Interesse nao e variacao principal do framework.

### `Denuncia`

Local:

- `backend/elo-core/src/main/java/com/elo/denuncia/Denuncia.java`
- `backend/elo-core/src/main/java/com/elo/denuncia/StatusDenuncia.java`
- `backend/elo-core/src/main/java/com/elo/denuncia/CriterioDenuncia.java`

Responsabilidade:

- representar denuncia de uma oferta;
- expor denunciante, oferta, status e criterio.

Instancias:

- Apto: `Denuncia`, `CriterioDenunciaApto`.
- Study Buddy: `DenunciaGrupoEstudo`, `CriterioDenunciaStudyBuddy`.
- Mentor Match: `DenunciaSessaoMentoria`, `CriterioDenunciaMentorMatch`.

Ponto flexivel secundario:

- criterio de denuncia.

## Templates do Core

### `UsuarioService<T extends Usuario, C, A, R>`

Fluxo fixo:

- criar;
- listar;
- buscar;
- atualizar;
- ativar/inativar;
- excluir.

Hooks da instancia:

- construir entidade;
- aplicar atualizacao;
- validar regras especificas;
- persistir;
- mapear resposta;
- executar pos-criacao.

### `PerfilService`

Fluxo fixo:

- buscar perfil por usuario;
- criar ou atualizar perfil.

Hooks da instancia:

- buscar usuario;
- buscar perfil existente;
- construir perfil;
- aplicar dados especificos;
- mapear resposta.

### `OfertaService`

Fluxo fixo:

- criar oferta;
- listar;
- buscar;
- atualizar;
- alterar status;
- excluir fisica ou logicamente.

Hooks da instancia:

- construir oferta;
- validar publicador;
- aplicar atualizacao;
- obter/aplicar status;
- decidir exclusao fisica;
- executar acao apos indisponibilizacao.

### `ManifestacaoInteresseService`

Fluxo fixo:

- criar manifestacao;
- validar oferta ativa;
- impedir interesse proprio;
- impedir duplicidade ativa;
- aceitar;
- recusar;
- cancelar;
- listar por oferta;
- listar por interessado;
- buscar com autorizacao;
- cancelar pendentes da oferta.

Hooks da instancia:

- buscar oferta;
- buscar interessado;
- construir manifestacao;
- aplicar criacao e resposta;
- consultar duplicidade;
- mapear respostas;
- fornecer excecoes especificas.

### `DenunciaService`

Fluxo fixo:

- listar;
- criar;
- aplicar status inicial;
- atualizar status;
- validar maquina de estados;
- excluir;
- buscar por id, oferta, denunciante e status.

Hooks da instancia:

- buscar denunciante;
- buscar oferta;
- construir denuncia;
- aplicar dados especificos;
- mapear resposta;
- fornecer excecoes especificas.

### `ModeracaoService`

Fluxo fixo:

- buscar denuncia;
- obter oferta denunciada;
- validar decisao;
- aplicar status da denuncia;
- aplicar acao na oferta;
- salvar denuncia e oferta;
- mapear resposta.

Hooks da instancia:

- converter acao da instancia para acao do core;
- pausar oferta;
- encerrar oferta;
- mapear resposta;
- fornecer excecoes especificas.

### `MatchingService<U extends Usuario, P extends Perfil>`

Fluxo fixo:

- validar `topN`;
- buscar solicitante;
- validar perfil do solicitante;
- buscar candidatos;
- filtrar elegiveis;
- tentar resultados LLM;
- aplicar fallback deterministico quando a LLM falha ou omite candidato;
- associar resultado diretamente ao candidato;
- ordenar por percentual;
- limitar por `topN`.

Hooks da instancia:

- buscar solicitante;
- buscar candidatos;
- obter perfil;
- validar perfil;
- aplicar elegibilidade;
- prover LLM;
- mapear resposta final.

## Estrategias e Portas

### `CompatibilidadeStrategy<P extends Perfil>`

Ponto flexivel principal para criterios de compatibilidade.

Instancias:

- Apto: `CompatibilidadeDeterministicaCalculator`.
- Study Buddy: `CompatibilidadeAcademicaCalculator`.
- Mentor Match: `CompatibilidadeMentoriaCalculator`.

### `ProvedorCompatibilidadeLlm<U, P>`

Porta opcional para compatibilidade assistida por LLM.

Instancias:

- Apto: `AptoCompatibilidadeLlmProvider`, `GroqClient`, `MatchmakingPromptBuilder`, `MatchmakingLlmParser`.
- Study Buddy: `StudyBuddyCompatibilidadeLlmProvider`, `GroqClient`, `StudyBuddyMatchingPromptBuilder`, `StudyBuddyMatchingLlmParser`.
- Mentor Match: `MentorMatchCompatibilidadeLlmProvider`, `GroqClient`, `MentorMatchPromptBuilder`, `MentorMatchLlmParser`.

### `AbstractGroqChatClient`

Cliente base OpenAI-compatible para Groq.

Responsabilidade:

- montar request de chat completion;
- validar chave;
- chamar `/chat/completions`;
- interpretar `choices`;
- delegar erros especificos para cada instancia.

### `RepositorioBase<T, ID>`

Porta de persistencia usada pelos templates do core para evitar dependencia direta de repositories das instancias.

## Anti-Contratos

Nao fazem parte do nucleo reutilizavel nesta entrega:

- autenticacao real;
- frontend generico multi-instancia;
- deploy;
- avaliacao generica;
- reputacao generica;
- moradia generica;
- notificacao generica;
- observer/event publisher.

Avaliacao e reputacao continuam exclusivas do Apto.
