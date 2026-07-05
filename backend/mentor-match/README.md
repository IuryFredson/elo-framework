# Mentor Match

Terceira instância backend do Elo Framework. A API é exposta sob `/mentor-match` e, por padrão,
inicia na porta `8082`.

## Configuração

- `DB_URL` — padrão `jdbc:postgresql://localhost:5432/apto`.
- `DB_USERNAME` — padrão `apto`.
- `DB_PASSWORD` — padrão `apto`.
- `DDL_AUTO` — padrão `update`.
- `MENTOR_MATCH_PORT` — padrão `8082`.
- `GROQ_API_KEY` — opcional; sem a chave, o matching usa o fallback determinístico.

## Rotas

- `/mentor-match/mentores` e `/mentor-match/alunos` — usuários, status e perfil.
- `/mentor-match/sessoes` — gestão das ofertas.
- `/mentor-match/sessoes/busca` — busca por `area`, `modalidade`, `nivel` e `periodo`.
- `/mentor-match/solicitacoes` — criação, consulta e transições das solicitações.
- `/mentor-match/matching` — recomendação de mentores por aluno.
- `/mentor-match/denuncias` e `/mentor-match/moderacoes/denuncias` — denúncia e moderação.

## Limites desta versão

Não inclui autenticação real, frontend, agendamento após o aceite, notificações, avaliações ou reputação.
