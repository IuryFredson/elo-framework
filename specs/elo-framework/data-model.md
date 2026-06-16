# Data Model: Elo Framework

## Objetivo

Descrever o modelo conceitual do Elo Framework e o mapeamento das instancias Apto, Study Buddy e Mentor Match.

Este arquivo nao define obrigatoriamente tabelas novas. Ele define os conceitos que devem orientar o design e a implementacao.

## Modelo Conceitual do Framework

```text
Usuario
  possui Perfil

Usuario
  publica Oferta

Usuario
  manifesta interesse em Oferta

Compatibilidade
  compara Perfis usando Criterios do dominio
```

## Entidades Conceituais

### Usuario

Representa um participante da plataforma.

Atributos conceituais:

- id;
- nome;
- email;
- telefone;
- status ativo/inativo.

No Apto atual:

- `Usuario`;
- `UsuarioUniversitario`;
- `Locador`.

### Perfil

Representa os dados usados para busca, recomendacao ou compatibilidade.

Atributos conceituais:

- id;
- usuario;
- tipo de perfil;
- atributos especificos do dominio.

Varia por instancia:

- Apto: perfil de convivencia.
- Study Buddy: perfil academico.
- Mentor Match: perfil de mentoria.

### Oferta

Representa uma oportunidade publicada por um usuario.

Atributos conceituais:

- id;
- publicador;
- titulo;
- descricao;
- status;
- data de publicacao;
- dados especificos do dominio.

Varia por instancia:

- Apto: moradia ou vaga.
- Study Buddy: grupo de estudo.
- Mentor Match: sessao ou programa de mentoria.

### Interacao

Representa o registro da **Manifestacao de Interesse** de um usuario em uma oferta.

Atributos conceituais:

- id;
- interessado;
- oferta relacionada;
- status;
- mensagem;
- data de criacao;
- data de resposta.

Ponto importante:

- A Manifestacao de Interesse e o mecanismo fixo de interacao.
- O que varia e o tipo de oferta pela qual o usuario manifesta interesse.

### Compatibilidade

Representa o resultado da comparacao entre perfis.

Atributos conceituais:

- percentual;
- justificativa;
- criterios atendidos;
- origem do calculo, quando aplicavel.

Varia por instancia:

- Apto: rotina e convivencia.
- Study Buddy: disciplina e horario.
- Mentor Match: area e objetivo.

## Mapeamento por Instancia

| Conceito | Apto | Study Buddy | Mentor Match |
| --- | --- | --- | --- |
| Usuario | Universitario ou locador | Estudante | Mentor ou mentorado |
| Perfil | Perfil de convivencia | Perfil academico | Perfil de mentoria |
| Oferta | Moradia ou vaga | Grupo de estudo | Sessao de mentoria |
| Interacao | Manifestacao de interesse em moradia ou vaga | Manifestacao de interesse em grupo de estudo | Manifestacao de interesse em sessao de mentoria |
| Compatibilidade | Rotina e convivencia | Disciplina, horario, objetivo e nivel | Area, objetivo, experiencia e disponibilidade |

## Modelo Atual do Apto

### Usuario

`Usuario` e uma entidade abstrata.

Especializacoes:

- `UsuarioUniversitario`;
- `Locador`.

### Perfil

`UsuarioUniversitario` possui `PerfilConvivencia`.

`PerfilConvivencia` contem:

- horario de sono;
- nivel de barulho aceitavel;
- frequencia de visitas;
- nivel de organizacao;
- rotina de estudos;
- consumo de alcool;
- fumante;
- aceita animais;
- preferencia de genero;
- descricao livre.

### Oferta

`Anuncio` representa a oferta publicada.

`Anuncio` esta associado a:

- `PerfilAnunciante`;
- `Moradia`;
- tipo de anuncio;
- status;
- valor mensal.

### Interacao

`ManifestacaoInteresse` representa a interacao entre interessado e anuncio.

Ela possui:

- anuncio;
- usuario interessado;
- status;
- mensagem;
- data de manifestacao;
- data de resposta.

### Compatibilidade

`MatchmakingService` busca colegas compativeis.

`CompatibilidadeDeterministicaCalculator` calcula compatibilidade por:

- horario de sono;
- nivel de barulho;
- organizacao;
- frequencia de visitas;
- rotina de estudos;
- fumante;
- alcool;
- animais;
- preferencia de genero.

## Modelo Proposto para Study Buddy

### Perfil Academico

Atributos conceituais:

- curso;
- disciplina;
- disponibilidade;
- objetivo de estudo;
- nivel de conhecimento.

### Oferta de Grupo de Estudo

Atributos conceituais:

- titulo;
- disciplina;
- horario;
- quantidade de vagas;
- formato.

### Compatibilidade Academica

Criterios:

- mesma disciplina;
- horario compativel;
- objetivo semelhante;
- nivel de conhecimento compativel.

## Modelo Proposto para Mentor Match

### Perfil de Mentoria

Atributos conceituais:

- papel;
- area de interesse;
- objetivo;
- experiencia;
- disponibilidade.

### Oferta de Mentoria

Atributos conceituais:

- titulo;
- area;
- formato;
- duracao;
- disponibilidade.

### Compatibilidade de Mentoria

Criterios:

- papeis complementares;
- mesma area;
- objetivo alinhado;
- experiencia adequada;
- disponibilidade compativel.

## Observacao Sobre Persistencia

Este modelo nao exige que Study Buddy e Mentor Match tenham persistencia completa em banco.

Para demonstracao academica, as instancias podem ser implementadas inicialmente como:

- objetos de dominio simples;
- DTOs;
- estrategias de compatibilidade;
- testes unitarios;
- endpoints demonstrativos, se necessario.

Persistencia deve ser adicionada apenas se for requisito da apresentacao ou da avaliacao.

