# Data Model: Elo Framework

## Objetivo

Descrever o modelo final do Elo Framework e como Apto, Study Buddy e Mentor Match instanciam esse modelo.

Este documento nao define todas as tabelas do banco. Ele descreve os conceitos do framework, os contratos do core e as entidades concretas das instancias.

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

Instancias:

- Apto: `UsuarioUniversitario`, `Locador`.
- Study Buddy: `Estudante`.
- Mentor Match: `Aluno`, `Mentor`.

### Perfil

Representa dados da instancia usados por busca, recomendacao ou compatibilidade.

Contrato no core:

- `Perfil`.

Instancias:

- Apto: `PerfilConvivencia`.
- Study Buddy: `PerfilAcademico`.
- Mentor Match: `PerfilMentoria`.

Ponto flexivel:

- dados do perfil.

### Oferta

Representa algo publicado por um usuario.

Contrato no core:

- `Oferta`.

Instancias:

- Apto: `Anuncio`, associado a `Moradia`.
- Study Buddy: `GrupoEstudo`, publicado por `Estudante`.
- Mentor Match: `SessaoMentoria`, publicada no dominio de mentorias.

Ponto flexivel:

- tipo de oferta publicada.

### Manifestacao de Interesse

Representa o mecanismo fixo de interesse em uma oferta.

Contrato no core:

- `ManifestacaoInteresse`.

Estados no core:

- `PENDENTE`;
- `ACEITA`;
- `RECUSADA`;
- `CANCELADA`.

Instancias:

- Apto: `ManifestacaoInteresse` entre `UsuarioUniversitario` interessado e `Anuncio`.
- Study Buddy: `ManifestacaoInteresseGrupo` entre `Estudante` interessado e `GrupoEstudo`.
- Mentor Match: solicitacoes e participantes aplicam o mecanismo de interesse/participacao no dominio de mentoria.

Ponto fixo:

- Manifestacao de Interesse nao varia como mecanismo.

### Denuncia

Representa denuncia de uma oferta.

Contrato no core:

- `Denuncia`.

Estados no core:

- `PENDENTE`;
- `EM_ANALISE`;
- `PROCEDENTE`;
- `IMPROCEDENTE`;
- `ARQUIVADA`.

Instancias:

- Apto: `Denuncia` associada a `Anuncio`, com `CriterioDenunciaApto`.
- Study Buddy: `DenunciaGrupoEstudo` associada a `GrupoEstudo`, com `CriterioDenunciaStudyBuddy`.
- Mentor Match: `DenunciaSessaoMentoria` associada a `SessaoMentoria`, com `CriterioDenunciaMentorMatch`.

### Compatibilidade e Matching

Representa comparacao entre perfis e ordenacao de candidatos.

Contratos no core:

- `CompatibilidadeStrategy<P>`;
- `MatchingService<U, P>`;
- `ProvedorCompatibilidadeLlm<U, P>`;
- `ResultadoCompatibilidade`;
- `ResultadoMatching`.

Instancias:

- Apto: compatibilidade por convivencia.
- Study Buddy: compatibilidade por disciplina, disponibilidade, objetivo, nivel e modalidade.
- Mentor Match: compatibilidade por area, objetivo, experiencia, disponibilidade e modalidade de mentoria.

## Modelo Atual do Apto

### Usuarios

Especializacoes:

- `UsuarioUniversitario`;
- `Locador`.

### Perfil de Convivencia

`PerfilConvivencia` implementa `Perfil` e contem dados como horario de sono, nivel de barulho, visitas, organizacao, rotina de estudos, alcool, fumante, animais, preferencia de genero e descricao.

### Perfil Anunciante

`PerfilAnunciante` e especifico do Apto e representa o papel de publicador de anuncios.

### Anuncio e Moradia

`Anuncio` implementa `Oferta`.

`Moradia` contem dados especificos do dominio: tipo, bairro, endereco resumido, mobiliado, aceita animais, quantidade de vagas e regras.

### Manifestacao, Denuncia, Avaliacao e Reputacao

- `ManifestacaoInteresse` implementa o contrato fixo do core.
- `Denuncia` implementa `Denuncia` do core.
- `CriterioDenunciaApto` define criterios especificos.
- `Avaliacao` e `ReputacaoAnunciante` sao especificas do Apto e nao viraram contratos do framework.

## Modelo Atual do Study Buddy

### Estudante

`Estudante` estende `Usuario` e contem matricula e instituicao.

### Perfil Academico

`PerfilAcademico` implementa `Perfil` e contem curso, disciplinas de interesse, disponibilidade, objetivo de estudo, nivel de conhecimento, modalidade preferida e descricao.

### Grupo de Estudo

`GrupoEstudo` implementa `Oferta` e contem titulo, descricao, disciplina, publicador, quantidade de vagas, modalidade, periodo, status e data de publicacao.

### Manifestacao, Denuncia e Moderacao

- `ManifestacaoInteresseGrupo` implementa o contrato fixo do core.
- `DenunciaGrupoEstudo` implementa `Denuncia` do core.
- `CriterioDenunciaStudyBuddy` define criterios especificos para grupos.
- `ModeracaoGrupoEstudoService` aplica decisoes sobre grupos denunciados.

### Compatibilidade Academica

`CompatibilidadeAcademicaCalculator` implementa `CompatibilidadeStrategy<PerfilAcademico>`.

Criterios minimos:

- disciplinas em comum;
- disponibilidade compativel;
- objetivo de estudo igual ou complementar;
- nivel de conhecimento proximo;
- modalidade compativel.

## Modelo Atual do Mentor Match

### Participantes

Mentor Match possui participantes como alunos e mentores, com services, repositories, DTOs e mappers proprios.

### Perfil de Mentoria

`PerfilMentoria` representa os dados de interesse, conhecimento, area, objetivo e disponibilidade usados no matching da instancia.

### Sessao de Mentoria

`SessaoMentoria` representa a oferta concreta do dominio de mentoria.

### Solicitacoes e Participantes

`SolicitacaoMentoria` e `ParticipanteMentoria` representam o fluxo especifico de solicitacao e participacao em sessoes.

### Denuncia e Moderacao

`DenunciaSessaoMentoria` e `CriterioDenunciaMentorMatch` instanciam denuncia/moderacao no dominio de mentoria.

### Compatibilidade de Mentoria

`CompatibilidadeMentoriaCalculator` implementa criterios de compatibilidade da instancia, considerando area, objetivo, nivel de conhecimento, disponibilidade e modalidade.

## Notificacoes e Observers

O mecanismo de Observer/Event Publisher e as notificacoes de anuncio indisponivel foram removidos.

Cancelamentos e recalculos passaram a ser chamadas diretas:

- services de oferta/moderacao cancelam manifestacoes pendentes quando necessario;
- `AvaliacaoService` recalcula reputacao diretamente no Apto.

## Persistencia

A persistencia concreta pertence a cada instancia.

O core usa `RepositorioBase` como porta minima para templates e nao define tabelas especificas de moradia, avaliacao, reputacao, grupos de estudo ou mentorias.
