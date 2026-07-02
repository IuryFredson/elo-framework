# Research: Elo Framework

## Objetivo

Registrar investigações, decisões e alternativas avaliadas durante a evolução do Apto para o Elo Framework.

## Estado Final do Projeto

Estrutura principal:

- `backend/elo-core`: núcleo reutilizável do framework.
- `backend/apto`: instância Apto e API Spring Boot.
- `backend/study-buddy`: instância Study Buddy e API Spring Boot.
- `frontend`: aplicação React/Vite do Apto.
- `specs`: documentação SDD, contratos, modelo, tarefas, rastreabilidade e diagrama.

## Elementos do Apto que Instanciam o Framework

### Usuários

O core possui `Usuario` e `UsuarioService`.

O Apto especializa:

- `UsuarioUniversitario`;
- `Locador`.

### Perfis

O core possui `Perfil` e `PerfilService`.

O Apto instancia:

- `PerfilConvivencia`.

### Ofertas

O core possui `Oferta` e `OfertaService`.

O Apto instancia:

- `Anuncio`, associado a `Moradia`.

### Manifestação de Interesse

O core possui `ManifestacaoInteresse`, `StatusManifestacaoInteresse` e `ManifestacaoInteresseService`.

O Apto instancia:

- `ManifestacaoInteresse` entre interessado e anúncio.

### Denúncia e Moderação

O core possui `Denuncia`, `StatusDenuncia`, `CriterioDenuncia`, `DenunciaService` e `ModeracaoService`.

O Apto instancia:

- `Denuncia`;
- `CriterioDenunciaApto`;
- `ModeracaoService` concreto.

### Compatibilidade

O core possui `CompatibilidadeStrategy`, `MatchingService`, `ProvedorCompatibilidadeLlm`, `ResultadoCompatibilidade` e `ResultadoMatching`.

O Apto instancia:

- `CompatibilidadeDeterministicaCalculator`;
- `AptoCompatibilidadeLlmProvider`;
- `MatchmakingService`.

## Elementos do Study Buddy que Instanciam o Framework

### Usuários

O Study Buddy instancia:

- `Estudante`;
- `EstudanteService`.

### Perfis

O Study Buddy instancia:

- `PerfilAcademico`;
- `PerfilAcademicoService`.

### Ofertas

O Study Buddy instancia:

- `GrupoEstudo`;
- `GrupoEstudoService`.

### Manifestação de Interesse

O Study Buddy instancia:

- `ManifestacaoInteresseGrupo`;
- `ManifestacaoInteresseGrupoService`.

### Compatibilidade

O Study Buddy instancia:

- `CompatibilidadeAcademicaCalculator`;
- `StudyBuddyCompatibilidadeLlmProvider`;
- `StudyBuddyMatchingService`.

## Decisões

### Decisão 1: Preservar o Apto como instância original

Motivo:

- O Apto é o produto da Fase 1.
- A Fase 2 deve demonstrar evolução, não substituição.
- Os endpoints e fluxos públicos precisavam continuar funcionando.

Consequência:

- Refatorações foram incrementais.
- DTOs, controllers, mappers e repositories concretos permaneceram no Apto.

### Decisão 2: Usar Template Method no core

Motivo:

- O framework deve controlar fluxos fixos.
- A instância deve fornecer hooks específicos.
- Evita que o algoritmo comum continue espalhado no Apto.

Consequência:

- Services do Apto passaram a estender templates do core.
- Testes do core usam implementações falsas para provar independência.

### Decisão 3: Manifestação de Interesse é ponto fixo

Motivo:

- A orientação conceitual do projeto define Manifestação de Interesse como mecanismo comum.
- O que muda é o tipo de oferta alvo.

Consequência:

- Manifestação de Interesse não virou ponto flexível.
- O core controla transições, autorização, duplicidade e cancelamento de pendentes.

### Decisão 4: Study Buddy como segunda instância concreta

Motivo:

- Após o Apto estar instanciado, Study Buddy foi escolhido para demonstrar reutilização real do framework em outro domínio.
- A instância precisava ficar separada do Apto e depender apenas de `elo-core`.

Consequência:

- Study Buddy foi implementado em `backend/study-buddy`.
- O core não depende de `com.studybuddy`.
- Mentor Match permanece como exemplo conceitual de extensibilidade futura.

### Decisão 5: Remover Observer/Event Publisher

Motivo:

- O mecanismo adicionava indireção sem ser parte essencial do framework.
- A Etapa 08 exigiu isolamento de funcionalidades do Apto.

Consequência:

- Cancelamento de manifestações passou a ser chamada direta.
- Reputação passou a ser recalculada diretamente.
- Notificações de anúncio indisponível foram removidas.

## Alternativas Rejeitadas

### Generalizar avaliação e reputação

Rejeitada porque avaliação e reputação são específicas do Apto nesta entrega.

### Implementar Mentor Match agora

Rejeitada porque Study Buddy já demonstra reutilização concreta do framework em uma segunda instância.

### Usar adapters para tudo

Rejeitada quando a implementação direta do contrato era natural. Exemplo: `PerfilConvivencia` implementando `Perfil` e `Anuncio` implementando `Oferta`.

## Recomendações Futuras

Trabalhos futuros podem:

- implementar Mentor Match como nova instância;
- publicar `elo-core` como biblioteca independente;
- criar autenticação real;
- criar frontend multi-instância.

Essas recomendações não fazem parte da entrega atual.
