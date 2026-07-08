# Plan: Elo Framework

## Estado Final

O Elo Framework foi implementado como um framework Spring/JPA hibrido.

A arquitetura final e:

- `backend/elo-core`: nucleo reutilizavel com contratos, estados, portas e templates.
- `backend/apto`: instancia concreta para moradias universitarias.
- `backend/study-buddy`: instancia concreta para grupos de estudo.
- `backend/mentor-match`: instancia concreta para mentorias.
- `frontend`, `frontend-study-buddy` e `frontend-mentor-match`: frontends separados para as instancias.

A dependencia permanece unidirecional:

```text
apto         -> elo-core
study-buddy  -> elo-core
mentor-match -> elo-core
```

`elo-core` nao referencia `com.apto`, `com.studybuddy` ou `com.mentormatch`.

## Estrategia Executada

A evolucao foi incremental para preservar o Apto e evitar reescrita completa.

Ordem executada:

1. Estabilizar fronteira do core.
2. Migrar `Usuario` e criar template de usuario.
3. Extrair fluxo de perfil.
4. Extrair publicacao e gestao de ofertas.
5. Extrair Manifestacao de Interesse.
6. Extrair denuncia e moderacao.
7. Completar compatibilidade e matching.
8. Remover Observer e isolar funcionalidades especificas do Apto.
9. Implementar Study Buddy como segunda instancia.
10. Implementar Mentor Match como terceira instancia.
11. Atualizar documentacao, diagramas e validacao final.

## Arquitetura Final

### Nucleo do Framework

O core controla os fluxos fixos por Template Method:

- `UsuarioService`
- `PerfilService`
- `OfertaService`
- `ManifestacaoInteresseService`
- `DenunciaService`
- `ModeracaoService`
- `MatchingService`

O core tambem define contratos:

- `Usuario`
- `Perfil`
- `Oferta`
- `ManifestacaoInteresse`
- `Denuncia`
- `CriterioDenuncia`
- `CompatibilidadeStrategy`
- `ProvedorCompatibilidadeLlm`
- `RepositorioBase`

O cliente base da Groq foi generalizado em `com.elo.compatibilidade.llm.groq`, enquanto cada instancia mantem suas excecoes, propriedades, prompt e parser.

### Instancia Apto

O Apto instancia os pontos flexiveis:

- dados do perfil: `PerfilConvivencia`;
- tipo de oferta publicada: `Anuncio` associado a `Moradia`;
- criterios de compatibilidade: `CompatibilidadeDeterministicaCalculator`;
- criterio de denuncia: `CriterioDenunciaApto`;
- integracao LLM: `AptoCompatibilidadeLlmProvider`, `GroqClient`, prompt e parser.

O Apto mantem como especificos:

- DTOs, controllers, mappers, repositories e excecoes;
- avaliacao;
- reputacao;
- moradia;
- perfil anunciante.

### Instancia Study Buddy

Study Buddy instancia os pontos flexiveis:

- dados do perfil: `PerfilAcademico`;
- tipo de oferta publicada: `GrupoEstudo`;
- criterios de compatibilidade: `CompatibilidadeAcademicaCalculator`;
- criterio de denuncia: `CriterioDenunciaStudyBuddy`;
- integracao LLM: `StudyBuddyCompatibilidadeLlmProvider`, `GroqClient`, prompt e parser.

A instancia possui backend em `backend/study-buddy` e frontend em `frontend-study-buddy`.

### Instancia Mentor Match

Mentor Match instancia os pontos flexiveis:

- dados do perfil: `PerfilMentoria`;
- tipo de oferta publicada: `SessaoMentoria`;
- criterios de compatibilidade: `CompatibilidadeMentoriaCalculator`;
- criterio de denuncia: `CriterioDenunciaMentorMatch`;
- integracao LLM: `MentorMatchCompatibilidadeLlmProvider`, `GroqClient`, prompt e parser.

A instancia possui backend em `backend/mentor-match` e frontend em `frontend-mentor-match`.

## Decisoes Finais

### Manifestacao de Interesse

Manifestacao de Interesse e ponto fixo.

A instancia nao muda o mecanismo. Ela muda apenas a oferta na qual o usuario manifesta interesse ou o fluxo especifico associado a essa oferta.

### Observer

O mecanismo de Observer/Event Publisher foi removido.

Substituicoes:

- cancelamento de manifestacoes: chamada direta em services de oferta/moderacao;
- recalculo de reputacao no Apto: chamada direta em `AvaliacaoService`.

### Avaliacao e Reputacao

Avaliacao e reputacao continuam exclusivas do Apto.

Elas nao viraram contratos do framework.

### Frontends

A entrega final possui tres frontends separados, um por instancia. Nao foi criado um frontend multi-instancia unico.

## Hooks Obrigatorios para Futura Instancia

Uma futura instancia deve implementar ou fornecer:

- usuario concreto, quando precisar especializar `Usuario`;
- perfil concreto implementando `Perfil`;
- oferta concreta implementando `Oferta`;
- manifestacao concreta implementando `ManifestacaoInteresse`, se usar manifestacao;
- denuncia concreta implementando `Denuncia`, se usar denuncia;
- criterio de denuncia implementando `CriterioDenuncia`, se usar denuncia;
- estrategia de compatibilidade implementando `CompatibilidadeStrategy<P>`;
- provedor LLM implementando `ProvedorCompatibilidadeLlm<U, P>`, se usar LLM;
- services concretos que estendem templates do core;
- repositories compativeis com `RepositorioBase`;
- DTOs, mappers, controllers e excecoes da instancia.

## Validacao Tecnica

Comando principal:

```bash
cd backend
mvn test
```

Validacoes esperadas:

- testes do `elo-core` passando;
- testes do `apto` passando;
- testes do `study-buddy` passando;
- testes do `mentor-match`, quando presentes, passando;
- teste arquitetural garantindo independencia do core;
- inicializacao JPA das instancias funcionando conforme configuracao local.

## Proximos Passos Fora do Plano Atual

As atividades abaixo nao fazem parte desta entrega:

- criar autenticacao real;
- criar frontend multi-instancia unico;
- empacotar/publicar o `elo-core` como biblioteca externa;
- preparar deploy de producao;
- generalizar avaliacao/reputacao para o core.
