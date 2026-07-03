# Compatibilidade e Matchmaking no Elo Framework

Este documento explica em detalhes como funcionam as features de **compatibilidade**
e **matchmaking** neste projeto — primeiro no núcleo reutilizável (`elo-core`) e
depois em cada instância concreta (`apto` e `study-buddy`).

O objetivo é mostrar exatamente:

- quais contratos o framework define e qual algoritmo genérico ele executa;
- quais pontos flexíveis cada instância precisa preencher;
- como a estratégia determinística e o provedor de LLM se combinam;
- como isso chega até a API REST e o frontend.

---

## 1. Visão geral

O projeto é um **framework Spring/JPA híbrido** (`elo-core`) instanciado por dois
domínios diferentes:

| Domínio | Instância | O que casa | Perfil usado |
| --- | --- | --- | --- |
| Moradia universitária | `apto` (`com.apto`) | colegas de moradia | `PerfilConvivencia` |
| Grupos de estudo | `study-buddy` (`com.studybuddy`) | parceiros de estudo | `PerfilAcademico` |

A dependência é **unidirecional**: `apto -> elo-core` e `study-buddy -> elo-core`.
O núcleo nunca referencia classes das instâncias — ele expõe **contratos**
(interfaces / classes abstratas / records) e um **Template Method**, e cada
instância fornece as partes concretas.

A feature tem duas camadas conceituais:

1. **Compatibilidade** — dado um par de perfis, produzir um *score* de 0 a 100
   com justificativa. É a "métrica".
2. **Matchmaking / Matching** — dado um solicitante, encontrar candidatos
   elegíveis, calcular a compatibilidade de cada um, ordenar e devolver o
   *top N*. É o "fluxo".

Há **duas fontes** possíveis para o score de compatibilidade, com precedência:

1. **LLM (Groq)** — quando disponível, é a fonte primária.
2. **Cálculo determinístico** — sempre existe e serve como *fallback* quando o
   LLM falha, está desligado ou não devolve resultado para um candidato.

---

## 2. O framework (`elo-core`)

Todo o código de compatibilidade do núcleo vive em
`backend/elo-core/src/main/java/com/elo/compatibilidade/`.

### 2.1 Contratos base

O framework é genérico sobre dois tipos:

- `U extends Usuario` — a entidade de usuário (`com.elo.usuario.Usuario`, entidade
  JPA abstrata com `id`, `nome`, `email`, `telefone`, `ativo`).
- `P extends Perfil` — os dados de perfil considerados no matching
  (`com.elo.perfil.Perfil`, uma interface com apenas `String tipoPerfil()`).

#### `CompatibilidadeStrategy<P>`

```java
public interface CompatibilidadeStrategy<P extends Perfil> {
    ResultadoCompatibilidade calcular(P solicitante, P candidato);
}
```

Contrato do **cálculo determinístico**. Recebe dois perfis e devolve um
`ResultadoCompatibilidade`. Cada instância implementa suas próprias regras.

#### `ProvedorCompatibilidadeLlm<U, P>`

```java
public interface ProvedorCompatibilidadeLlm<U extends Usuario, P extends Perfil> {
    Map<UUID, ResultadoCompatibilidade> calcular(U solicitante, List<U> candidatos);
}
```

Contrato da **fonte por LLM**. Recebe o solicitante e a lista de candidatos e
devolve um mapa `candidatoId -> ResultadoCompatibilidade`. Isso permite ao
provedor mandar **todos os candidatos em uma única chamada** ao modelo e
correlacionar as respostas por ID. É legítimo devolver um mapa parcial (ou
vazio): candidatos ausentes caem automaticamente no fallback.

#### `ResultadoCompatibilidade` (record)

```java
public record ResultadoCompatibilidade(
        UUID candidatoId,
        int percentual,          // 0..100
        String justificativa,
        List<String> criteriosAtendidos,
        OrigemCompatibilidade origem
) { ... }
```

Há um construtor de conveniência que preenche `candidatoId = null` e
`origem = FALLBACK_DETERMINISTICO` — usado pelas estratégias determinísticas, que
não precisam saber o ID (o `MatchingService` o injeta depois).

#### `OrigemCompatibilidade` (enum)

```java
public enum OrigemCompatibilidade { LLM, FALLBACK_DETERMINISTICO }
```

Marca de onde veio cada score. Trafega até o DTO de resposta, então o cliente
sabe se aquele match foi avaliado pelo modelo ou pelo cálculo local.

#### `ResultadoMatching<C>` (record)

```java
public record ResultadoMatching<C>(C candidato, ResultadoCompatibilidade compatibilidade) {}
```

Casa a entidade candidata com seu resultado de compatibilidade — é o que o
`MatchingService` devolve.

### 2.2 O Template Method: `MatchingService<U, P>`

`MatchingService` é uma **classe abstrata** que implementa o fluxo fixo de
matchmaking e delega os pontos variáveis a métodos abstratos. É o coração da
feature. Ela recebe no construtor:

- uma `CompatibilidadeStrategy<P>` (o fallback determinístico);
- um `ProvedorCompatibilidadeLlm<U, P>` (a fonte por LLM).

Ambos são obrigatórios (`Objects.requireNonNull`).

#### Método público — `calcularMatches(UUID solicitanteId, int topN)`

O algoritmo, passo a passo (ver `MatchingService.java:28`):

1. **Validação de entrada** — `topN < 0` lança `IllegalArgumentException`.
2. **Carrega o solicitante** via `buscarSolicitante(solicitanteId)` (abstrato).
3. **Valida o perfil do solicitante** via `validarPerfilSolicitante(...)`
   (abstrato) — normalmente garante que existe perfil cadastrado.
4. **Busca candidatos** via `buscarCandidatos(solicitanteId)` (abstrato) e
   **filtra por elegibilidade** com `elegivel(solicitante, candidato)` (abstrato).
5. **Curto-circuito**: se não sobrou candidato ou `topN == 0`, devolve
   `List.of()` — e assim **nem chama o LLM**.
6. **Tenta o LLM** via `calcularComLlm(...)` (ver abaixo), obtendo um mapa
   `candidatoId -> ResultadoCompatibilidade`.
7. **Monta o resultado de cada candidato** com `calcularResultado(...)`:
   - se o mapa do LLM tem entrada para aquele `candidato.getId()`, usa-a e a
     **normaliza** com origem `LLM`;
   - senão, calcula o **fallback determinístico** e normaliza com origem
     `FALLBACK_DETERMINISTICO`.
8. **Ordena** por `percentual` **decrescente** e aplica `.limit(topN)`.

> Observação importante: a decisão LLM-vs-fallback é **por candidato**. Se o
> modelo devolveu 8 dos 10 candidatos, esses 8 saem como `LLM` e os 2 restantes
> como `FALLBACK_DETERMINISTICO` — tudo na mesma resposta.

#### Tratamento de LLM e fallback — `calcularComLlm(...)`

```java
try {
    Map<UUID, ResultadoCompatibilidade> resultados = provedorLlm.calcular(solicitante, candidatos);
    if (resultados == null || resultados.isEmpty()) {
        return Map.of();          // sem hook aoUsarLlm
    }
    aoUsarLlm(solicitante, candidatos);
    return resultados;
} catch (RuntimeException e) {
    aoUsarFallback(solicitante, e);   // qualquer falha do provedor -> fallback global
    return Map.of();
}
```

Regras:

- Se o provedor devolve `null`/vazio, o mapa fica vazio → **todos** os candidatos
  caem no determinístico (mas sem disparar o hook de fallback, pois não houve
  erro).
- Se o provedor **lança** qualquer `RuntimeException` (ex.: Groq fora do ar, JSON
  inválido), a exceção é capturada, o hook `aoUsarFallback` é chamado e o mapa
  vazio faz **todos** caírem no determinístico. Ou seja, uma falha do LLM
  **nunca derruba** o matchmaking.

#### Normalização — `normalizarResultado(...)`

Independente da fonte, o resultado final é reconstruído garantindo que
`candidatoId` seja o ID real do candidato e que `origem` seja coerente (`LLM` ou
`FALLBACK_DETERMINISTICO`). Isso evita confiar no `candidatoId` que o LLM
devolveu para preencher o campo — o framework reescreve com o ID confiável.

#### Pontos de extensão (métodos abstratos e hooks)

Métodos **abstratos** (cada instância obriga-se a implementar):

| Método | Responsabilidade |
| --- | --- |
| `U buscarSolicitante(UUID)` | Carregar o usuário solicitante (ou lançar exceção de "não encontrado"). |
| `List<U> buscarCandidatos(UUID)` | Trazer o universo de candidatos (normalmente já filtrando ativos/ com perfil no repositório). |
| `P perfil(U usuario)` | Extrair o `Perfil` de um usuário (usado no fallback). |
| `boolean elegivel(U solicitante, U candidato)` | Regra de elegibilidade dura — quem nem entra no cálculo. |
| `void validarPerfilSolicitante(U)` | Garantir pré-condições do solicitante. |

Métodos **hook** (opcionais, vazios por padrão — normalmente para logging):

- `void aoUsarLlm(U solicitante, List<U> candidatos)`
- `void aoUsarFallback(U solicitante, RuntimeException causa)`

### 2.3 O serviço auxiliar `CompatibilidadeService<P>`

Além do Template Method, o núcleo oferece um serviço **stateless e apenas
determinístico**: `CompatibilidadeService<P>`. Ele recebe uma
`CompatibilidadeStrategy<P>` e expõe:

```java
List<ResultadoCompatibilidade> calcularCompatibilidades(
        P solicitante, List<P> candidatos, BiPredicate<P, P> elegivel, int topN);
```

Diferenças em relação ao `MatchingService`:

- **Não** usa LLM — só a estratégia determinística.
- Trabalha diretamente com **perfis** (`P`), não com usuários (`U`).
- A elegibilidade vem como um `BiPredicate` passado no momento da chamada, em vez
  de um método abstrato.

É um utilitário mais simples para cálculo direto de compatibilidade entre perfis
quando não se precisa do fluxo completo de matchmaking com LLM. As instâncias
atuais usam o `MatchingService`; o `CompatibilidadeService` fica disponível como
contrato do núcleo.

### 2.4 Resumo do que o framework fixa vs. deixa flexível

| Fixo no `elo-core` | Flexível (por instância) |
| --- | --- |
| Ordem do fluxo (validar → filtrar → LLM → fallback → ordenar → top N) | Como carregar solicitante/candidatos |
| Precedência LLM sobre determinístico, por candidato | Regra de elegibilidade |
| Captura de falha do LLM e fallback global | Regras do cálculo determinístico |
| Normalização de `candidatoId`/`origem` | Se/como integrar um LLM |
| Ordenação decrescente e corte em `topN` | Formato do prompt e parsing da resposta |

---

## 3. Instância Apto (`com.apto`)

Domínio: encontrar **colegas de moradia** compatíveis. Pacote:
`backend/apto/src/main/java/com/apto/service/matchmaking/`.

O Apto é a instância **completa**: usa LLM (Groq) como fonte primária e cálculo
determinístico como fallback.

### 3.1 Perfil e universo de candidatos

- Perfil: **`PerfilConvivencia`** (implementa `Perfil`, `tipoPerfil()` =
  `"PERFIL_CONVIVENCIA"`). Campos considerados: `horarioSono`,
  `nivelBarulhoAceitavel`, `frequenciaVisitas`, `nivelOrganizacao`,
  `rotinaEstudos`, `consomeAlcool`, `fumante`, `aceitaAnimais`,
  `preferenciaGeneroConvivencia` e um `descricaoLivre` (texto opcional do usuário).
- Usuário: **`UsuarioUniversitario`**.
- Candidatos: `UsuarioUniversitarioRepository.buscarCandidatosMatchmaking(...)`
  já filtra no banco `perfilConvivencia IS NOT NULL AND ativo = true AND id <> solicitante`.

### 3.2 `MatchmakingService extends MatchingService<UsuarioUniversitario, PerfilConvivencia>`

Implementa os pontos abstratos:

- `buscarSolicitante` → `repository.findById(...)`, senão
  `UsuarioNaoEncontradoException`.
- `buscarCandidatos` → query `buscarCandidatosMatchmaking`.
- `perfil` → `usuario.getPerfilConvivencia()`.
- `validarPerfilSolicitante` → lança `PerfilConvivenciaAusenteException` se o
  solicitante não tem perfil.
- `elegivel` → delega a
  `compatibilidadeDeterministicaCalculator.preferenciaGeneroCompativel(...)` — ou
  seja, a **compatibilidade de gênero é uma barreira dura**: candidatos cuja
  preferência de gênero é incompatível nem entram no cálculo (nem para o LLM).

Hooks sobrescritos apenas para **log** (`aoUsarLlm`, `aoUsarFallback`).

O método público `buscarColegasCompativeis(solicitanteId, topN)` chama
`calcularMatches(...)` do framework, mapeia cada `ResultadoMatching` para
`MatchColegaResponseDTO` e embrulha em `MatchmakingResponseDTO`
(`solicitanteId`, `total`, `candidatos`).

### 3.3 Cálculo determinístico — `CompatibilidadeDeterministicaCalculator`

Implementa `CompatibilidadeStrategy<PerfilConvivencia>`. Score = **média dos
critérios**, em 0..100.

Cada critério recebe um sub-score:

- **Enums ordinais** (`horarioSono`, `nivelBarulhoAceitavel`,
  `nivelOrganizacao`, `frequenciaVisitas`) via `scoreOrdinal`, baseado na
  **distância entre os `ordinal()`**:
  - distância 0 → `100`
  - distância 1 → `50`
  - distância ≥ 2 → `0`
  - (ex.: `HorarioSono` tem ordem `CEDO, MEDIO, TARDE`, então `CEDO` vs `TARDE`
    dá distância 2 = 0 pontos).
- **`rotinaEstudos`** via `scoreRotinaEstudos` (regra especial):
  - igual → `100`; um dos dois é `MISTA` → `75`; caso contrário → `25`.
- **Booleanos** (`fumante`, `consomeAlcool`, `aceitaAnimais`) via `scoreBooleano`:
  iguais → `100`, diferentes → `0`.
- Qualquer valor `null` em um critério → aquele critério pontua `0` (mas ainda
  conta no divisor).

Depois da média:

- **Penalidade "A combinar"**: se qualquer um dos perfis tem
  `preferenciaGeneroConvivencia == A_COMBINAR`, subtrai `5` do percentual.
- O resultado final é **clampado** em `[0, 100]`.

A justificativa é fixa (`"Compatibilidade calculada por critérios
determinísticos."`) e `criteriosAtendidos` fica vazio — no Apto o texto rico vem
do LLM; o determinístico é só o número de fallback.

Além disso, o método `preferenciaGeneroCompativel(a, b)` (usado na elegibilidade)
exige **compatibilidade mútua**: cada usuário precisa aceitar o gênero do outro,
conforme a preferência:

- `SEM_PREFERENCIA` / `A_COMBINAR` → aceita qualquer gênero;
- `APENAS_MULHERES` → aceita só gênero `FEMININO`;
- `APENAS_HOMENS` → aceita só gênero `MASCULINO`.

Se faltar gênero ou perfil em qualquer lado, retorna `false` (não elegível).

### 3.4 Fonte por LLM — `AptoCompatibilidadeLlmProvider` + Groq

Implementa `ProvedorCompatibilidadeLlm<UsuarioUniversitario, PerfilConvivencia>`.
O fluxo de uma chamada:

1. **`MatchmakingPromptBuilder.montarSystemPrompt()`** — instrução ao modelo:
   ele age como "assessor de compatibilidade" entre colegas de moradia, deve usar
   os **campos estruturados como base** e a `descricaoLivre` apenas como contexto
   qualitativo (ignorando-a se ausente/irrelevante/ofensiva), e deve devolver
   **apenas JSON** no formato
   `{"matches":[{"candidatoId","percentual":0-100,"justificativa"}]}`, com todos
   os candidatos, ordenado por percentual decrescente, justificativa de 2–4
   frases (250–500 chars), sem citar nomes nem dados sensíveis.
2. **`montarUserPrompt(solicitante, candidatos)`** — serializa via Jackson um JSON
   com `solicitante` e `candidatos`; cada candidato inclui `candidatoId`. Só os
   campos de convivência (mais `genero` e a `descricaoLivre`, quando não vazia)
   são enviados.
3. **`GroqClient.completarChat(system, user, true)`** — POST para
   `/chat/completions` da Groq, com `response_format = {"type":"json_object"}`.
   Requer `GROQ_API_KEY`; sem chave, ou em erro HTTP/rede, lança
   `GroqIntegracaoException`.
4. **`MatchmakingLlmParser.parse(json)`** — lê o campo `matches`, valida cada item
   (`candidatoId` presente e `percentual` em 0..100; itens inválidos são
   ignorados com log) e devolve `Map<UUID, ResultadoCompatibilidade>` com origem
   `LLM`. Se não houver campo `matches` válido ou o JSON for inválido, lança
   `GroqIntegracaoException`.

Como todas essas falhas são `RuntimeException`, elas são capturadas pelo
`MatchingService` e convertidas em **fallback determinístico** — a feature nunca
quebra por causa do LLM. Como diz o README: *sem chave ou em caso de falha, o
matching usa fallback determinístico.*

### 3.5 Adaptação de tipos e resposta REST

O Apto define **seus próprios** `OrigemCompatibilidade` e
`ResultadoCompatibilidade` (em `com.apto.service.matchmaking`) para desacoplar o
DTO de resposta dos tipos do núcleo. O record local
`ResultadoCompatibilidade.fromFramework(...)` converte o
`com.elo.compatibilidade.ResultadoCompatibilidade` do framework, traduzindo o
enum de origem.

Camada REST:

- **`MatchmakingController`** → `GET /matchmaking/colegas/{solicitanteId}?topN=10`
  (`topN` validado entre 1 e 50).
- **`MatchmakingMapper`** monta `MatchColegaResponseDTO` (`id`, `nome`, `curso`,
  `genero`, `percentualCompatibilidade`, `justificativa`, `origem`).
- Frontend: `frontend/src/api/matchmaking.ts` chama esse endpoint; as páginas
  `Matchmaking.tsx` / `Matches.tsx` exibem os resultados.

---

## 4. Instância Study Buddy (`com.studybuddy`)

Domínio: encontrar **parceiros de estudo** compatíveis. Pacote:
`backend/study-buddy/src/main/java/com/studybuddy/service/matching/`.

O Study Buddy reusa exatamente o mesmo `MatchingService` e integra a Groq como
fonte primária, com o cálculo acadêmico determinístico como fallback.

### 4.1 Perfil e universo de candidatos

- Perfil: **`PerfilAcademico`** (`tipoPerfil()` = `"PERFIL_ACADEMICO"`). Campos:
  `curso`, `disciplinasInteresse` (set), `disponibilidade` (set de
  `PeriodoDisponibilidade`), `objetivoEstudo`, `nivelConhecimento`,
  `modalidadePreferida`, `descricao`.
- Usuário: **`Estudante`**.
- Candidatos: `EstudanteRepository.buscarCandidatosMatching(...)` — mesma ideia do
  Apto (`perfilAcademico IS NOT NULL AND ativo = true AND id <> solicitante`).

### 4.2 `StudyBuddyMatchingService extends MatchingService<Estudante, PerfilAcademico>`

Implementações dos pontos abstratos:

- `buscarSolicitante` → `findById`, senão `EstudanteNaoEncontradoException`.
- `buscarCandidatos` → `buscarCandidatosMatching`.
- `perfil` → `estudante.getPerfilAcademico()`.
- `validarPerfilSolicitante` → `PerfilAcademicoAusenteException` se ausente.
- `elegivel` → `candidato.isAtivo() && candidato.getPerfilAcademico() != null`
  (mais simples que o Apto — **sem** barreira de gênero).
- Não sobrescreve os hooks `aoUsarLlm`/`aoUsarFallback`.

Método público `buscarEstudantesCompativeis(estudanteId, topN)` → mapeia para
`MatchEstudanteResponseDTO` dentro de `StudyBuddyMatchingResponseDTO`.

### 4.3 Cálculo determinístico — `CompatibilidadeAcademicaCalculator`

Implementa `CompatibilidadeStrategy<PerfilAcademico>`. Aqui o modelo de score é
**diferente** do Apto: em vez de média, é **soma ponderada de pontos** (pesos
somando 100 no máximo), e o resultado é clampado em `[0, 100]`:

| Critério | Método | Pontuação |
| --- | --- | --- |
| Disciplinas em comum | `scoreDisciplinas` | `35` se há interseção (normalizada, case-insensitive, trim), senão `0` |
| Disponibilidade | `scoreDisponibilidade` | `25` se algum lado é `FLEXIVEL` ou há período em comum, senão `0` |
| Objetivo de estudo | `scoreObjetivo` | `15` se igual; `10` se complementar (PROVA↔REFORCO, PROJETO↔TRABALHO); senão `0` |
| Nível de conhecimento | `scoreNivel` | distância de `ordinal()`: 0→`15`, 1→`8`, ≥2→`0` |
| Modalidade preferida | `scoreModalidade` | `10` se igual; `7` se algum é `HIBRIDO`; senão `0` |

Diferente do Apto, o Study Buddy **preenche `criteriosAtendidos`**: cada critério
que pontua adiciona um rótulo legível (ex.: `"disciplinas de interesse em
comum"`), que trafega até o DTO de resposta. A justificativa é fixa
(`"Compatibilidade calculada por criterios academicos deterministicos."`).

### 4.4 Fonte por LLM — `StudyBuddyCompatibilidadeLlmProvider` + Groq

O provider monta um prompt específico do domínio acadêmico, chama a Groq em modo
JSON e interpreta a resposta como um mapa `candidatoId -> ResultadoCompatibilidade`.
O payload contém apenas curso, disciplinas de interesse, disponibilidade, objetivo
de estudo, nível de conhecimento, modalidade preferida, descrição opcional e o ID
necessário para correlacionar cada candidato. Nome, e-mail, telefone e matrícula não
são enviados.

O formato esperado é
`{"matches":[{"candidatoId","percentual":0-100,"justificativa"}]}`. Itens com ID
ou percentual inválido são ignorados, permitindo fallback por candidato. JSON
estruturalmente inválido, ausência de `GROQ_API_KEY` ou falhas HTTP/rede geram uma
exceção capturada pelo `MatchingService`, que então usa o fallback determinístico.
Como no Apto, resultados da LLM deixam `criteriosAtendidos` vazio.

### 4.5 Resposta REST

- **`StudyBuddyMatchingController`** →
  `GET /study-buddy/matching?estudanteId=...&topN=10`.
- **`StudyBuddyMatchingMapper`** monta `MatchEstudanteResponseDTO` (dados do
  estudante + `percentualCompatibilidade`, `justificativa`, `criteriosAtendidos`,
  `origem`). Aqui o DTO usa **diretamente** o `OrigemCompatibilidade` do núcleo
  (`com.elo.compatibilidade`), sem o adaptador local que o Apto criou.

---

## 5. Apto vs. Study Buddy lado a lado

| Aspecto | Apto | Study Buddy |
| --- | --- | --- |
| Usuário / Perfil | `UsuarioUniversitario` / `PerfilConvivencia` | `Estudante` / `PerfilAcademico` |
| Serviço de matching | `MatchmakingService` | `StudyBuddyMatchingService` |
| Estratégia determinística | `CompatibilidadeDeterministicaCalculator` (média de critérios) | `CompatibilidadeAcademicaCalculator` (soma ponderada) |
| Fonte por LLM | `AptoCompatibilidadeLlmProvider` (Groq) | `StudyBuddyCompatibilidadeLlmProvider` (Groq) |
| Elegibilidade | compatibilidade de gênero mútua | ativo + tem perfil |
| `criteriosAtendidos` | não preenchido | preenchido |
| Origem no DTO | enum próprio do Apto (adaptado) | enum do núcleo diretamente |
| Endpoint | `GET /matchmaking/colegas/{id}?topN` | `GET /study-buddy/matching?estudanteId&topN` |

Ambos compartilham **exatamente** o mesmo motor de fluxo (`MatchingService`): a
diferença está só nos pontos flexíveis.

---

## 6. Fluxo end-to-end (resumo)

```
Cliente (frontend)
   │  GET /matchmaking/colegas/{id}?topN=10
   ▼
Controller da instância
   │  buscarColegasCompativeis(id, topN)
   ▼
Service da instância  ──(extends)──►  MatchingService (elo-core)
   │
   │  1. buscarSolicitante + validarPerfilSolicitante
   │  2. buscarCandidatos + filtro elegivel(...)
   │  3. provedorLlm.calcular(...)  ── sucesso ─► scores LLM (por candidato)
   │                                └─ falha/vazio ─► aoUsarFallback / mapa vazio
   │  4. para cada candidato: score LLM  ▼ OU  estrategiaDeterministica.calcular(...)
   │  5. ordena por percentual desc + limit(topN)
   ▼
Mapper → DTO de resposta (percentual, justificativa, origem[, criteriosAtendidos])
   ▼
Cliente exibe os matches
```

Pontos-chave para lembrar:

- **LLM é preferencial, determinístico é garantido.** A decisão é por candidato.
- **Falha de LLM nunca quebra a feature** — cai no determinístico.
- **O núcleo não conhece as instâncias.** Adicionar um novo domínio (ex.: "Mentor
  Match") é estender `MatchingService`, implementar uma `CompatibilidadeStrategy`
  e um `ProvedorCompatibilidadeLlm` — sem alterar `elo-core`.

---

## 7. Onde olhar no código

Framework (`backend/elo-core/src/main/java/com/elo/compatibilidade/`):
`MatchingService.java`, `CompatibilidadeStrategy.java`,
`ProvedorCompatibilidadeLlm.java`, `ResultadoCompatibilidade.java`,
`ResultadoMatching.java`, `OrigemCompatibilidade.java`,
`CompatibilidadeService.java`.

Apto (`backend/apto/src/main/java/com/apto/`):
`service/matchmaking/MatchmakingService.java`,
`service/matchmaking/CompatibilidadeDeterministicaCalculator.java`,
`service/matchmaking/AptoCompatibilidadeLlmProvider.java`,
`service/matchmaking/MatchmakingPromptBuilder.java`,
`service/matchmaking/MatchmakingLlmParser.java`,
`integration/llm/GroqClient.java`, `controller/MatchmakingController.java`,
`mapper/MatchmakingMapper.java`.

Study Buddy (`backend/study-buddy/src/main/java/com/studybuddy/`):
`service/matching/StudyBuddyMatchingService.java`,
`service/matching/CompatibilidadeAcademicaCalculator.java`,
`service/matching/StudyBuddyCompatibilidadeLlmProvider.java`,
`controller/StudyBuddyMatchingController.java`,
`mapper/StudyBuddyMatchingMapper.java`.
