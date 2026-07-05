# Plano De Execucao: Camada Web Backend Do Elo Framework

## Summary

Criar uma camada REST reutilizavel dentro de `elo-core`, em `com.elo.web`, para transformar os fluxos HTTP fixos do framework em pontos reutilizaveis: usuario, perfil, oferta, manifestacao de interesse, denuncia, moderacao, matching e tratamento de erros.

A refatoracao deve padronizar os endpoints agora, migrando primeiro Study Buddy e depois Apto.

## Key Changes

- Adicionar dependencia `spring-web` ao `backend/elo-core/pom.xml`.
- Criar em `elo-core` o pacote `com.elo.web` com:
  - controllers abstratos: `UsuarioRestController`, `PerfilRestController`, `OfertaRestController`, `ManifestacaoInteresseRestController`, `DenunciaRestController`, `ModeracaoRestController`, `MatchingRestController`;
  - DTOs comuns: `ErroResponseDTO`, `AlterarAtivoRequestDTO`, `PaginaResponseDTO`;
  - handler base: `EloRestExceptionHandler`.
- Manter DTOs especificos de dominio nas instancias: `CriarGrupoEstudoRequestDTO`, `GrupoEstudoResponseDTO`, `CriarAnuncioRequestDTO`, `AnuncioResponseDTO`, etc.
- Fazer excecoes especificas de Apto e Study Buddy herdarem das excecoes base do core quando aplicavel:
  - nao encontrado -> `EntidadeNaoEncontradaException`;
  - regra de negocio/conflito -> `RegraNegocioException`;
  - transicao invalida -> `TransicaoInvalidaException`;
  - acesso negado -> nova excecao base `AcessoNegadoFrameworkException` em `elo-core`.

## Endpoint Standard

Aplicar o padrao abaixo nas instancias migradas.

### Usuarios

- `POST /.../usuarios`
- `GET /.../usuarios`
- `GET /.../usuarios/{id}`
- `PUT /.../usuarios/{id}`
- `PATCH /.../usuarios/{id}/status` com `{ "ativo": true|false }`
- `DELETE /.../usuarios/{id}`

### Perfil

- `GET /.../usuarios/{id}/perfil`
- `PUT /.../usuarios/{id}/perfil`

### Ofertas

- `POST /.../ofertas`
- `GET /.../ofertas`
- `GET /.../ofertas/{id}`
- `PUT /.../ofertas/{id}?publicadorId=...`
- `PATCH /.../ofertas/{id}/status` com enum no body
- `DELETE /.../ofertas/{id}`

### Manifestacao De Interesse

- `POST /.../manifestacoes`
- `GET /.../manifestacoes/{id}?solicitanteId=...`
- `PATCH /.../manifestacoes/{id}/aceitar?publicadorId=...`
- `PATCH /.../manifestacoes/{id}/recusar?publicadorId=...`
- `PATCH /.../manifestacoes/{id}/cancelar?interessadoId=...`
- `GET /.../manifestacoes/oferta/{ofertaId}?publicadorId=...`
- `GET /.../manifestacoes/interessado/{interessadoId}`

### Matching

- `GET /.../matching?solicitanteId=...&topN=10`

Study Buddy deve usar prefixo `/study-buddy`. Apto, por enquanto, pode usar prefixo vazio, mas deve migrar nomes conceituais para o padrao: `/ofertas`, `/manifestacoes`, `/matching`.

## Implementation Changes

1. Implementar `com.elo.web` no `elo-core`.
   - Controllers abstratos devem conter apenas logica HTTP comum e delegar para os services do core.
   - Cada controller concreto da instancia deve apenas informar o service concreto e, quando necessario, adaptar nomes de metodos especificos.

2. Migrar Study Buddy primeiro.
   - `EstudanteController` passa a estender `UsuarioRestController`.
   - `PerfilAcademicoController` passa a estender `PerfilRestController`.
   - `GrupoEstudoController` passa a estender `OfertaRestController`.
   - `ManifestacaoInteresseGrupoController` passa a estender `ManifestacaoInteresseRestController`.
   - `StudyBuddyMatchingController` passa a estender `MatchingRestController`.
   - Denuncia e moderacao de grupo passam a usar os controllers abstratos correspondentes.

3. Migrar Apto depois.
   - `UsuarioUniversitarioController` e `LocadorController` usam `UsuarioRestController`.
   - `PerfilController` usa `PerfilRestController`.
   - `AnuncioController` usa `OfertaRestController` para CRUD/status/delete e mantem busca filtrada como endpoint especifico.
   - `ManifestacaoInteresseController`, `DenunciaController`, `ModeracaoController` e `MatchmakingController` passam para os controllers abstratos.
   - Moradia, avaliacao, reputacao, perfil anunciante e diagnostico Groq continuam especificos do Apto.

4. Padronizar erros.
   - Remover repeticao dos `GlobalExceptionHandler` das instancias.
   - Manter handlers locais apenas para excecoes realmente especificas que nao encaixem na hierarquia do core.
   - Resposta padrao: `{ "erro": "...", "codigo": "...", "status": 400 }`.

## Test Plan

- Rodar `mvn test` em `backend` apos cada migracao.
- Ajustar testes de controller existentes para as novas rotas padronizadas.
- Criar ou atualizar cenarios minimos:
  - CRUD de usuario via controller abstrato;
  - buscar/atualizar perfil;
  - CRUD/status/delete de oferta;
  - criar, aceitar, recusar, cancelar e listar manifestacao;
  - matching com `solicitanteId` e `topN`;
  - erro de validacao retorna `400`;
  - entidade inexistente retorna `404`;
  - regra de negocio retorna `409`;
  - acesso negado retorna `403`.

## Assumptions

- A padronizacao de endpoints pode quebrar clientes atuais e testes antigos; isso e aceito neste plano.
- `elo-core` passara a conter tambem a camada web REST, conforme decisao do projeto.
- Study Buddy sera a primeira instancia migrada por ser menor e mais proxima dos fluxos fixos.
- Apto sera migrado depois, mantendo endpoints especificos apenas onde o dominio exige.
