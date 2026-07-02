# Contracts: Elo Framework

## Objetivo

Registrar os contratos públicos e templates do Elo Framework após a instanciação do Apto.

O core fica em `backend/elo-core` e não referencia `com.apto`. A instância Apto fica em `backend/apto` e fornece entidades, DTOs, repositories, mappers, controllers, exceções e regras específicas.

## Contratos de Domínio

### `Usuario`

Local:

- `backend/elo-core/src/main/java/com/elo/usuario/Usuario.java`

Responsabilidade:

- representar o usuário base do framework;
- manter `id`, `nome`, `email`, `telefone` e `ativo`;
- permitir especializações por instância.

No Apto:

- `UsuarioUniversitario`;
- `Locador`.

### `Perfil`

Local:

- `backend/elo-core/src/main/java/com/elo/perfil/Perfil.java`

Responsabilidade:

- representar dados usados por busca, recomendação ou compatibilidade;
- expor `tipoPerfil()`.

No Apto:

- `PerfilConvivencia`.

Ponto flexível:

- dados do perfil.

### `Oferta`

Local:

- `backend/elo-core/src/main/java/com/elo/oferta/Oferta.java`

Responsabilidade:

- representar uma oferta publicada;
- expor `getId()`, `getPublicadorId()`, `tipoOferta()` e `isAtiva()`.

No Apto:

- `Anuncio`, associado a `Moradia` e `PerfilAnunciante`.

Ponto flexível:

- tipo de oferta publicada.

### `ManifestacaoInteresse`

Local:

- `backend/elo-core/src/main/java/com/elo/manifestacao/ManifestacaoInteresse.java`
- `backend/elo-core/src/main/java/com/elo/manifestacao/StatusManifestacaoInteresse.java`

Responsabilidade:

- representar o mecanismo fixo de interesse em uma oferta;
- expor interessado, oferta e status.

No Apto:

- `ManifestacaoInteresse`.

Ponto fixo:

- Manifestação de Interesse não é variação principal do framework.

### `Denuncia`

Local:

- `backend/elo-core/src/main/java/com/elo/denuncia/Denuncia.java`
- `backend/elo-core/src/main/java/com/elo/denuncia/StatusDenuncia.java`
- `backend/elo-core/src/main/java/com/elo/denuncia/CriterioDenuncia.java`

Responsabilidade:

- representar denúncia de uma oferta;
- expor denunciante, oferta, status e critério.

No Apto:

- `Denuncia`;
- `CriterioDenunciaApto`.

Ponto flexível secundário:

- critério de denúncia.

## Templates do Core

### `UsuarioService<T extends Usuario, C, A, R>`

Fluxo fixo:

- criar;
- listar;
- buscar;
- atualizar;
- ativar/inativar;
- excluir.

Hooks da instância:

- construir entidade;
- aplicar atualização;
- validar regras específicas;
- persistir;
- mapear resposta;
- executar pós-criação.

### `PerfilService`

Fluxo fixo:

- buscar perfil por usuário;
- criar ou atualizar perfil.

Hooks da instância:

- buscar usuário;
- buscar perfil existente;
- construir perfil;
- aplicar dados específicos;
- mapear resposta.

### `OfertaService`

Fluxo fixo:

- criar oferta;
- listar;
- buscar;
- atualizar;
- alterar status;
- excluir física ou logicamente.

Hooks da instância:

- construir oferta;
- validar publicador;
- aplicar atualização;
- obter/aplicar status;
- decidir exclusão física;
- executar ação após indisponibilização.

### `ManifestacaoInteresseService`

Fluxo fixo:

- criar manifestação;
- validar oferta ativa;
- impedir interesse próprio;
- impedir duplicidade ativa;
- aceitar;
- recusar;
- cancelar;
- listar por oferta;
- listar por interessado;
- buscar com autorização;
- cancelar pendentes da oferta.

Hooks da instância:

- buscar oferta;
- buscar interessado;
- construir manifestação;
- aplicar criação e resposta;
- consultar duplicidade;
- mapear respostas;
- fornecer exceções específicas.

### `DenunciaService`

Fluxo fixo:

- listar;
- criar;
- aplicar status inicial;
- atualizar status;
- validar máquina de estados;
- excluir;
- buscar por id, oferta, denunciante e status.

Hooks da instância:

- buscar denunciante;
- buscar oferta;
- construir denúncia;
- aplicar dados específicos;
- mapear resposta;
- fornecer exceções específicas.

### `ModeracaoService`

Fluxo fixo:

- buscar denúncia;
- obter oferta denunciada;
- validar decisão;
- aplicar status da denúncia;
- aplicar ação na oferta;
- salvar denúncia e oferta;
- mapear resposta.

Hooks da instância:

- converter ação da instância para ação do core;
- pausar oferta;
- encerrar oferta;
- mapear resposta;
- fornecer exceções específicas.

### `MatchingService<U extends Usuario, P extends Perfil>`

Fluxo fixo:

- validar `topN`;
- buscar solicitante;
- validar perfil do solicitante;
- buscar candidatos;
- filtrar elegíveis;
- tentar resultados LLM;
- aplicar fallback determinístico quando a LLM falha ou omite candidato;
- associar resultado diretamente ao candidato;
- ordenar por percentual;
- limitar por `topN`.

Hooks da instância:

- buscar solicitante;
- buscar candidatos;
- obter perfil;
- validar perfil;
- aplicar elegibilidade;
- prover LLM;
- mapear resposta final.

## Estratégias e Portas

### `CompatibilidadeStrategy<P extends Perfil>`

Ponto flexível principal para critérios de compatibilidade.

No Apto:

- `CompatibilidadeDeterministicaCalculator`.

### `ProvedorCompatibilidadeLlm<U, P>`

Porta opcional para compatibilidade assistida por LLM.

No Apto:

- `AptoCompatibilidadeLlmProvider`;
- `GroqClient`;
- `MatchmakingPromptBuilder`;
- `MatchmakingLlmParser`.

### `RepositorioBase<T, ID>`

Porta de persistência usada pelos templates do core para evitar dependência direta de repositories do Apto.

## Anti-Contratos

Não fazem parte do núcleo reutilizável nesta entrega:

- autenticação real;
- frontend;
- deploy;
- avaliação genérica;
- reputação genérica;
- moradia genérica;
- notificação genérica;
- observer/event publisher;
- Study Buddy implementado;
- Mentor Match implementado.

Avaliação e reputação continuam exclusivas do Apto.
