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

## Seed de demonstração

Com o profile `dev` ativo, o `DevDataSeeder` popula um banco limpo (`ddl-auto=create-drop`)
com dados mínimos para demonstrar o fluxo completo: 3 mentores e 4 alunos com perfis, 5 sessões
(4 ativas e 1 pausada, exercitando a busca e o matching), solicitações em estados variados
(pendente, aceita, recusada) e duas denúncias (pendente e procedente) para a moderação.

```powershell
# Windows / PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"; .\mvnw.cmd -pl mentor-match spring-boot:run
```

```bash
# Linux / macOS
SPRING_PROFILES_ACTIVE=dev ./mvnw -pl mentor-match spring-boot:run
```

O seed é ignorado se já houver mentores ou alunos cadastrados.

## Limites desta versão

Não inclui autenticação real, frontend, agendamento após o aceite, notificações, avaliações ou reputação.
