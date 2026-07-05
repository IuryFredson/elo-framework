# Plano de Implementação: Mentor Match

## Resumo

Criar o módulo Maven `backend/mentor-match`, dependente apenas de `elo-core`, seguindo a estrutura do Study Buddy. O `elo-core`, Apto e Study Buddy não serão alterados.

Mentores e alunos serão entidades distintas, com um perfil de mentoria de estrutura comum. Sessões serão ofertas agendáveis; o agendamento efetivo após o aceite ficará fora do escopo.

## Modelo e implementação

- Criar `ParticipanteMentoria extends Usuario` como base abstrata, associada a `PerfilMentoria`.
- Criar as entidades concretas `Mentor` e `Aluno`, com services separados estendendo `UsuarioService`.
- `PerfilMentoria implements Perfil` conterá:
  - áreas;
  - objetivos;
  - nível de conhecimento;
  - modalidades;
  - períodos de disponibilidade;
  - idiomas;
  - descrição.
- Os mesmos campos representam competências no perfil do mentor e interesses/preferências no perfil do aluno.
- Criar `SessaoMentoria implements Oferta`, publicada somente por mentor, com:
  - título, descrição e área;
  - nível atendido;
  - modalidade e período;
  - capacidade;
  - status `ATIVA`, `PAUSADA` ou `ENCERRADA`;
  - data de publicação.
- `SessaoMentoriaService extends OfertaService`, permitindo criação, consulta, atualização, alteração de status e exclusão.
- Adicionar busca de sessões ativas por área, modalidade, nível e disponibilidade, ordenadas da publicação mais recente para a mais antiga.
- Criar `SolicitacaoMentoria implements ManifestacaoInteresse`, vinculando aluno e sessão.
- `SolicitacaoMentoriaService extends ManifestacaoInteresseService`, reutilizando criação, prevenção de interesse próprio/duplicado, aceite, recusa, cancelamento e autorização.
- O aceite não agenda horário; apenas confirma a solicitação. Agendamento e realização da mentoria ficam fora desta versão.

## Matching e LLM

- `MentorMatchingService extends MatchingService<ParticipanteMentoria, PerfilMentoria>`.
- Validar que o solicitante é um aluno ativo com perfil e considerar somente mentores ativos com perfil.
- Criar fallback determinístico com pesos:
  - áreas em comum: 35%;
  - objetivos compatíveis: 20%;
  - disponibilidade: 20%;
  - modalidade: 15%;
  - nível compatível: 10%.
- Retornar mentores ordenados por percentual, com justificativa, critérios atendidos e origem do resultado.
- Replicar o padrão LLM do Study Buddy:
  - `MentorMatchCompatibilidadeLlmProvider`;
  - prompt builder e parser próprios;
  - `GroqClient` baseado em `AbstractGroqChatClient`;
  - propriedades `mentor-match.groq.*`;
  - fallback determinístico em falha, resposta vazia ou candidato ausente.
- Não incorporar recomendação, reputação, avaliação ou outras funcionalidades exclusivas do Apto.

## Denúncia e moderação

- Criar denúncia vinculada à sessão, reutilizando `DenunciaService`.
- Permitir denúncia por qualquer participante.
- Critérios: `CONTEUDO_INADEQUADO`, `INFORMACAO_FALSA`, `COMPORTAMENTO_INADEQUADO`, `FRAUDE`, `USO_INDEVIDO` e `OUTRO`.
- Reutilizar `ModeracaoService` com ações `NENHUMA`, `PAUSAR_SESSAO` e `ENCERRAR_SESSAO`.
- Pausar ou encerrar uma sessão cancelará suas solicitações pendentes.

## API REST

Todos os controllers serão finos, herdarão os controllers do core e usarão o prefixo `/mentor-match`.

- `/mentores`: CRUD, status e perfil.
- `/alunos`: CRUD, status e perfil.
- `/sessoes`: CRUD, status e busca filtrada.
- `/solicitacoes`: criar, consultar, aceitar, recusar, cancelar e listar por sessão/aluno.
- `/matching?solicitanteId={alunoId}&topN={n}`: recomendações de mentores.
- `/denuncias`: criação e consultas.
- `/moderacoes/denuncias/{id}`: tratamento da denúncia e ação sobre a sessão.

Serão criados DTOs, mappers, repositories, exceções e um handler global próprios do módulo, mantendo classes da instância fora do core.

## Configuração e limites

- Registrar `mentor-match` no reactor Maven e criar `MentorMatchApplication`.
- Usar Spring Boot, JPA, PostgreSQL, Validation, Web, RestClient e Lombok nas mesmas versões das outras instâncias.
- Manter autenticação real, frontend, agendamento pós-aceite, notificações, avaliações e reputação fora do escopo.
- Conforme solicitado, não incluir etapas de testes ou validação no desenvolvimento planejado.
