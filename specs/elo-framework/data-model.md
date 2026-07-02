# Data Model: Elo Framework

## Objetivo

Descrever o modelo final do Elo Framework e como Apto e Study Buddy instanciam esse modelo.

Este documento não define todas as tabelas do banco. Ele descreve os conceitos do framework, os contratos do core e as entidades concretas das instâncias.

## Modelo Conceitual do Framework

```text
Usuario
  possui Perfil

Usuario
  publica Oferta

Usuario
  manifesta interesse em Oferta

Usuario
  denuncia Oferta

Moderacao
  avalia Denuncia e pode alterar Oferta

Matching
  compara Perfis usando criterios da instancia
```

## Conceitos do Core

### Usuario

Representa um participante da plataforma.

Atributos no core:

- id;
- nome;
- email;
- telefone;
- ativo.

No Apto:

- `UsuarioUniversitario`;
- `Locador`.

No Study Buddy:

- `Estudante`.

### Perfil

Representa dados da instância usados por busca, recomendação ou compatibilidade.

Contrato no core:

- `Perfil`.

No Apto:

- `PerfilConvivencia`.

No Study Buddy:

- `PerfilAcademico`.

Ponto flexível:

- dados do perfil.

### Oferta

Representa algo publicado por um usuário.

Contrato no core:

- `Oferta`.

No Apto:

- `Anuncio`.

A oferta concreta do Apto usa:

- `PerfilAnunciante` como publicador;
- `Moradia` como dados específicos da moradia;
- `TipoAnuncio`;
- `StatusAnuncio`;
- valor mensal e data de publicação.

Ponto flexível:

- tipo de oferta publicada.

No Study Buddy:

- `GrupoEstudo`, publicado por `Estudante`.

### Manifestação de Interesse

Representa o mecanismo fixo de interesse em uma oferta.

Contrato no core:

- `ManifestacaoInteresse`.

Estados no core:

- `PENDENTE`;
- `ACEITA`;
- `RECUSADA`;
- `CANCELADA`.

No Apto:

- `ManifestacaoInteresse` entre `UsuarioUniversitario` interessado e `Anuncio`.

No Study Buddy:

- `ManifestacaoInteresseGrupo` entre `Estudante` interessado e `GrupoEstudo`.

Ponto fixo:

- Manifestação de Interesse não varia como mecanismo.

### Denúncia

Representa denúncia de uma oferta.

Contrato no core:

- `Denuncia`.

Estados no core:

- `PENDENTE`;
- `EM_ANALISE`;
- `PROCEDENTE`;
- `IMPROCEDENTE`;
- `ARQUIVADA`.

No Apto:

- `Denuncia` associada a `Anuncio` e `Usuario` denunciante.
- `CriterioDenunciaApto` define critérios específicos.

### Compatibilidade e Matching

Representa comparação entre perfis e ordenação de candidatos.

Contratos no core:

- `CompatibilidadeStrategy<P>`;
- `MatchingService<U, P>`;
- `ProvedorCompatibilidadeLlm<U, P>`;
- `ResultadoCompatibilidade`;
- `ResultadoMatching`.

No Apto:

- `CompatibilidadeDeterministicaCalculator` calcula compatibilidade por convivência;
- `AptoCompatibilidadeLlmProvider` integra Groq, prompt e parser;
- `MatchmakingService` mapeia o resultado para DTOs públicos.

No Study Buddy:

- `CompatibilidadeAcademicaCalculator` calcula compatibilidade por disciplina, disponibilidade, objetivo, nível e modalidade;
- `StudyBuddyCompatibilidadeLlmProvider` mantém a porta LLM opcional sem tornar LLM obrigatória;
- `StudyBuddyMatchingService` mapeia o resultado para DTOs públicos da instância.

## Modelo Atual do Apto

### Usuários

`Usuario` está no core como entidade abstrata JPA.

Especializações no Apto:

- `UsuarioUniversitario`;
- `Locador`.

### Perfil de Convivência

`PerfilConvivencia` implementa `Perfil` e contém:

- horário de sono;
- nível de barulho aceitável;
- frequência de visitas;
- nível de organização;
- rotina de estudos;
- consumo de álcool;
- fumante;
- aceita animais;
- preferência de gênero;
- descrição livre.

### Perfil Anunciante

`PerfilAnunciante` é específico do Apto.

Responsabilidade:

- representar o papel de publicador de anúncios;
- permitir que `Locador` e `UsuarioUniversitario` publiquem anúncios.

Esse conceito não é parte obrigatória do core.

### Anúncio e Moradia

`Anuncio` implementa `Oferta`.

`Moradia` contém os dados específicos do domínio:

- tipo;
- bairro;
- endereço resumido;
- mobiliado;
- aceita animais;
- quantidade de vagas;
- regras.

### Manifestação de Interesse

`ManifestacaoInteresse` implementa o contrato fixo do core.

Ela possui:

- anúncio;
- interessado;
- status;
- mensagem;
- data de manifestação;
- data de resposta.

### Denúncia e Moderação

`Denuncia` implementa `Denuncia` do core.

`CriterioDenunciaApto` implementa `CriterioDenuncia` com:

- `ANUNCIO_ENGANOSO`;
- `PRECO_ABUSIVO`;
- `IMOVEL_INEXISTENTE`;
- `CONTEUDO_INAPROPRIADO`;
- `OUTRO`.

`ModeracaoService` aplica decisões sobre denúncia e pode pausar ou encerrar o anúncio.

### Avaliação e Reputação

Avaliação e reputação são específicas do Apto.

Entidades:

- `Avaliacao`;
- `ReputacaoAnunciante`.

Essas entidades não são contratos do framework.

## Modelo Atual do Study Buddy

### Estudante

`Estudante` estende `Usuario` e contém:

- matrícula;
- instituição.

### Perfil Acadêmico

`PerfilAcademico` implementa `Perfil` e contém:

- curso;
- disciplinas de interesse;
- disponibilidade;
- objetivo de estudo;
- nível de conhecimento;
- modalidade preferida;
- descrição.

### Grupo de Estudo

`GrupoEstudo` implementa `Oferta` e contém:

- título;
- descrição;
- disciplina;
- publicador;
- quantidade de vagas;
- modalidade;
- período;
- status;
- data de publicação.

### Manifestação de Interesse em Grupo

`ManifestacaoInteresseGrupo` implementa o contrato fixo do core.

Ela possui:

- grupo;
- interessado;
- status;
- mensagem;
- data de manifestação;
- data de resposta.

### Compatibilidade Acadêmica

`CompatibilidadeAcademicaCalculator` implementa `CompatibilidadeStrategy<PerfilAcademico>`.

Os critérios mínimos são:

- disciplinas em comum;
- disponibilidade compatível;
- objetivo de estudo igual ou complementar;
- nível de conhecimento próximo;
- modalidade compatível.

### Notificações e Observers

O mecanismo de Observer/Event Publisher e as notificações de anúncio indisponível foram removidos.

Cancelamentos e recálculos passaram a ser chamadas diretas:

- `AnuncioService` e `ModeracaoService` cancelam manifestações pendentes;
- `AvaliacaoService` recalcula reputação diretamente.

## Exemplo Futuro Fora do Escopo

Uma futura instância Mentor Match poderia usar:

- perfil de mentoria;
- oferta de sessão ou programa de mentoria;
- compatibilidade por área, objetivo, experiência e disponibilidade.

Esses exemplos não possuem implementação nesta entrega.

## Persistência

A persistência concreta pertence a cada instância.

No Apto:

- repositories concretos ficam em `com.apto.repository`;
- entidades específicas ficam em `com.apto.model.entity`;
- o core usa `RepositorioBase` como porta mínima para templates.

O core não define tabelas específicas de moradia, avaliação, reputação ou notificação.
