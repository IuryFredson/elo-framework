# Plan: Elo Framework

## Estado Final

O Elo Framework foi implementado como um framework Spring/JPA híbrido.

A arquitetura final é:

- `backend/elo-core`: núcleo reutilizável com contratos, estados, portas e templates.
- `backend/apto`: instância concreta do framework para moradias universitárias.
- `frontend`: aplicação web do Apto, sem alteração estrutural para múltiplas instâncias.

A dependência permanece unidirecional:

```text
apto -> elo-core
```

`elo-core` não referencia `com.apto`.

## Estratégia Executada

A evolução foi incremental para preservar o Apto e evitar reescrita completa.

Ordem executada:

1. Estabilizar fronteira do core.
2. Migrar `Usuario` e criar template de usuário.
3. Extrair fluxo de perfil.
4. Extrair publicação e gestão de ofertas.
5. Extrair Manifestação de Interesse.
6. Extrair denúncia e moderação.
7. Completar compatibilidade e matching.
8. Remover Observer e isolar funcionalidades do Apto.
9. Atualizar documentação e validar instanciação.

## Arquitetura Final

### Núcleo do Framework

O core controla os fluxos fixos por Template Method:

- `UsuarioService`
- `PerfilService`
- `OfertaService`
- `ManifestacaoInteresseService`
- `DenunciaService`
- `ModeracaoService`
- `MatchingService`

O core também define contratos:

- `Usuario`
- `Perfil`
- `Oferta`
- `ManifestacaoInteresse`
- `Denuncia`
- `CriterioDenuncia`
- `CompatibilidadeStrategy`
- `ProvedorCompatibilidadeLlm`
- `RepositorioBase`

### Instância Apto

O Apto instancia os pontos flexíveis:

- dados do perfil: `PerfilConvivencia`;
- tipo de oferta publicada: `Anuncio` associado a `Moradia`;
- critérios de compatibilidade: `CompatibilidadeDeterministicaCalculator`;
- critério de denúncia: `CriterioDenunciaApto`;
- integração LLM: `AptoCompatibilidadeLlmProvider`, `GroqClient`, prompt e parser.

O Apto mantém como específicos:

- DTOs;
- controllers;
- mappers;
- repositories;
- exceções;
- avaliação;
- reputação;
- moradia;
- perfil anunciante.

## Decisões Finais

### Manifestação de Interesse

Manifestação de Interesse é ponto fixo.

A instância não muda o mecanismo. Ela muda apenas a oferta na qual o usuário manifesta interesse.

### Study Buddy e Mentor Match

Study Buddy e Mentor Match não serão implementados nesta entrega.

Eles permanecem apenas como exemplos conceituais de futuras instâncias:

- Study Buddy poderia variar perfil acadêmico, grupo de estudo e compatibilidade acadêmica.
- Mentor Match poderia variar perfil de mentoria, sessão de mentoria e compatibilidade de mentoria.

### Observer

O mecanismo de Observer/Event Publisher foi removido.

Substituições:

- cancelamento de manifestações: chamada direta em `AnuncioService` e `ModeracaoService`;
- recálculo de reputação: chamada direta em `AvaliacaoService`.

### Avaliação e Reputação

Avaliação e reputação continuam exclusivas do Apto.

Elas não viraram contratos do framework.

## Hooks Obrigatórios para Futura Instância

Uma futura instância deve implementar ou fornecer:

- usuário concreto, quando precisar especializar `Usuario`;
- perfil concreto implementando `Perfil`;
- oferta concreta implementando `Oferta`;
- manifestação concreta implementando `ManifestacaoInteresse`;
- denúncia concreta implementando `Denuncia`, se usar denúncia;
- critério de denúncia implementando `CriterioDenuncia`, se usar denúncia;
- estratégia de compatibilidade implementando `CompatibilidadeStrategy<P>`;
- provedor LLM implementando `ProvedorCompatibilidadeLlm<U, P>`, se usar LLM;
- services concretos que estendem templates do core;
- repositories compatíveis com `RepositorioBase`;
- DTOs, mappers, controllers e exceções da instância.

## Validação Técnica

Comando principal:

```bash
cd backend
mvn test
```

Validações esperadas:

- testes do `elo-core` passando;
- testes do `apto-api` passando;
- teste arquitetural garantindo independência do core;
- inicialização JPA do Apto funcionando.

## Próximos Passos Fora do Plano Atual

As atividades abaixo não fazem parte desta entrega:

- implementar Study Buddy;
- implementar Mentor Match;
- criar frontend multi-instância;
- empacotar/publicar o `elo-core` como biblioteca externa;
- adicionar autenticação real;
- preparar deploy de produção.
