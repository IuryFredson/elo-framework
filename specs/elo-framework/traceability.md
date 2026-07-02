# Traceability: Elo Framework

## Objetivo

Relacionar requisitos, tarefas, arquivos, testes e commits da evolução do Apto e do Study Buddy para o Elo Framework.

## Matriz Final

| Requisito | Descrição | Arquivos principais | Testes | Status |
| --- | --- | --- | --- | --- |
| EF-001 | Núcleo reutilizável | `backend/elo-core/src/main/java/com/elo/**` | `DependenciaArquiteturalTest`, testes dos templates do core | Concluído |
| EF-002 | Pontos flexíveis limitados | `Perfil`, `Oferta`, `CompatibilidadeStrategy`, `PerfilConvivencia`, `Anuncio`, `CompatibilidadeDeterministicaCalculator` | `CompatibilidadeServiceTest`, `MatchingServiceTest`, `MatchmakingServiceTest` | Concluído |
| EF-003 | Manifestação de Interesse como ponto fixo | `ManifestacaoInteresseService`, `StatusManifestacaoInteresse`, `ManifestacaoInteresse` do Apto | `ManifestacaoInteresseServiceTest` no core e no Apto | Concluído |
| EF-004 | Apto instanciado no framework | services do Apto estendendo templates do core | suíte `apto-api` | Concluído |
| EF-005 | Matching com fallback | `MatchingService`, `ProvedorCompatibilidadeLlm`, `AptoCompatibilidadeLlmProvider` | `MatchingServiceTest`, `MatchmakingServiceTest` | Concluído |
| EF-006 | Denúncia e moderação | `DenunciaService`, `ModeracaoService`, `CriterioDenunciaApto` | `DenunciaServiceTest`, `ModeracaoServiceTest` | Concluído |
| EF-007 | Extensibilidade controlada | `spec.md`, `contracts.md`, `plan.md`, `data-model.md`, diagrama | testes fake no core | Concluído |
| EF-008 | Study Buddy instanciado no framework | `backend/study-buddy/src/main/java/com/studybuddy/**` | suíte `study-buddy` | Concluído |

## Commits Registrados

| Commit | Propósito | Observação |
| --- | --- | --- |
| `457cae1` | Extrair fluxo de Manifestação de Interesse | Etapa 05 |
| `ac7593b` | Registrar resumo da interação 22 | Documento de estudo |
| `9f37167` | Extrair fluxo de denúncia e moderação | Etapa 06 |
| `9128326` | Extrair fluxo de compatibilidade e matching | Etapa 07 |

As etapas da instância Study Buddy ainda podem ser commitadas juntas ou separadas, conforme decisão da equipe.

## Testes Registrados

| Etapa | Comando | Resultado |
| --- | --- | --- |
| 05 | `mvn test` em `backend` | `elo-core` e `apto-api` com sucesso |
| 06 | `mvn test` em `backend` | `elo-core` e `apto-api` com sucesso |
| 07 | `mvn test` em `backend` | `elo-core: 24`, `apto-api: 139`, sucesso |
| 08 | `mvn test` em `backend` | `elo-core: 24`, `apto-api: 136`, sucesso |
| 09 | `mvn test` em `backend` | `elo-core: 24`, `apto-api: 136`, sucesso |
| Study Buddy 08 | `mvn test` em `backend` | `elo-core: 24`, `apto-api: 136`, `study-buddy: 50`, sucesso |
| Study Buddy 09 | `mvn test` em `backend` | `elo-core: 24`, `apto-api: 136`, `study-buddy: 50`, sucesso |

A contagem do Apto caiu na Etapa 08 porque os testes de Observer foram removidos junto com o mecanismo.

## Decisões LLM e Equipe

| Tema | Decisão |
| --- | --- |
| Manifestação de Interesse | Mantida como ponto fixo, não como ponto flexível |
| Apto | Instância concreta principal do framework |
| Study Buddy | Segunda instância concreta do framework |
| Mentor Match | Exemplo futuro, sem implementação nesta entrega |
| Adapters | Evitados quando implementação direta nos contratos era simples |
| Observer | Removido na Etapa 08, substituído por chamadas diretas |
| Avaliação e reputação | Mantidas exclusivas do Apto |

## Evidências de Instanciação

- `PerfilConvivencia` implementa `Perfil`.
- `Anuncio` implementa `Oferta`.
- `ManifestacaoInteresse` implementa `ManifestacaoInteresse` do core.
- `Denuncia` implementa `Denuncia` do core.
- `CriterioDenunciaApto` implementa `CriterioDenuncia`.
- `CompatibilidadeDeterministicaCalculator` implementa `CompatibilidadeStrategy<PerfilConvivencia>`.
- Services do Apto estendem templates do core.
- `Estudante` implementa a especialização de usuário do Study Buddy.
- `PerfilAcademico` implementa `Perfil`.
- `GrupoEstudo` implementa `Oferta`.
- `ManifestacaoInteresseGrupo` implementa `ManifestacaoInteresse` do core.
- `CompatibilidadeAcademicaCalculator` implementa `CompatibilidadeStrategy<PerfilAcademico>`.
- `StudyBuddyMatchingService` estende `MatchingService`.
- Services do Study Buddy estendem templates do core.
- Testes do core usam implementações falsas independentes do Apto.
