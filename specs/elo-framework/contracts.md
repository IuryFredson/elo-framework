# Contracts: Elo Framework

## Objetivo

Definir contratos conceituais para separar pontos fixos e pontos variaveis do Elo Framework.

Este arquivo descreve contratos esperados. A forma final no codigo pode usar interfaces, classes abstratas, services, records ou adapters, desde que preserve a separacao de responsabilidades.

## Contratos Principais

### Perfil

Representa os dados usados para compatibilidade.

Responsabilidades:

- identificar o tipo de perfil;
- expor os atributos relevantes para o dominio;
- permitir que estrategias de compatibilidade comparem perfis.

Possivel contrato:

```java
public interface PerfilFramework {
    String tipoPerfil();
}
```

### Oferta

Representa algo publicado por um usuario.

Responsabilidades:

- identificar o tipo de oferta;
- expor dados basicos da publicacao;
- associar a oferta ao usuario publicador.

Possivel contrato:

```java
public interface OfertaFramework {
    String tipoOferta();
}
```

### Interacao

Representa o registro da Manifestacao de Interesse de um usuario em uma oferta.

Responsabilidades:

- registrar usuario interessado;
- registrar oferta alvo;
- manter status;
- manter datas relevantes;
- preservar historico de manifestacao/resposta.

Possivel contrato:

```java
public interface InteracaoFramework {
    String tipoFixo();
    String status();
}
```

Observacao:

- O contrato de interacao e ponto fixo.
- O tipo fixo da interacao deve ser Manifestacao de Interesse.
- O que varia entre instancias e a oferta alvo, nao o mecanismo de interacao.

### Compatibilidade

Representa o calculo de match entre perfis.

Responsabilidades:

- receber perfis comparaveis;
- aplicar criterios do dominio;
- retornar percentual e justificativa.

Possivel contrato:

```java
public interface CompatibilidadeStrategy<P extends PerfilFramework> {
    ResultadoCompatibilidade calcular(P solicitante, P candidato);
}
```

### Resultado de Compatibilidade

Representa a saida do calculo.

Campos esperados:

- percentual;
- justificativa;
- criterios atendidos;
- origem do calculo, se aplicavel.

Possivel contrato:

```java
public record ResultadoCompatibilidade(
    int percentual,
    String justificativa,
    List<String> criteriosAtendidos
) {}
```

## Contratos por Ponto Fixo

### Cadastro e Gestao de Usuarios

Contrato esperado:

- criar usuario;
- atualizar usuario;
- ativar/inativar usuario;
- buscar usuario.

No Apto atual:

- `UsuarioUniversitarioService`;
- `LocadorService`.

### Cadastro de Perfis

Contrato esperado:

- criar ou atualizar perfil;
- buscar perfil por usuario;
- validar dados obrigatorios do perfil.

No Apto atual:

- `PerfilService`;
- `PerfilConvivencia`.

### Publicacao de Ofertas

Contrato esperado:

- criar oferta;
- atualizar oferta;
- listar ofertas;
- alterar status.

No Apto atual:

- `AnuncioService`;
- `Anuncio`.

### Registro de Interacoes

Contrato esperado:

- criar interacao;
- aceitar;
- recusar;
- cancelar;
- listar por origem/destino/oferta.

No Apto atual:

- `ManifestacaoInteresseService`;
- `ManifestacaoInteresse`.

### Calculo de Compatibilidade

Contrato esperado:

- calcular compatibilidade entre perfis;
- ordenar resultados;
- retornar justificativa.

No Apto atual:

- `MatchmakingService`;
- `CompatibilidadeDeterministicaCalculator`;
- `MatchmakingPromptBuilder`;
- `MatchmakingLlmParser`.

## Pontos de Extensao

### Dados do Perfil

Cada instancia define seu proprio perfil.

Exemplos:

- Apto: `PerfilConvivencia`.
- Study Buddy: `PerfilAcademico`.
- Mentor Match: `PerfilMentoria`.

### Tipo de Oferta Publicada

Cada instancia define sua propria oferta.

Exemplos:

- Apto: `Anuncio` de moradia ou vaga.
- Study Buddy: grupo de estudo.
- Mentor Match: sessao de mentoria.

### Criterios de Compatibilidade

Cada instancia define sua propria estrategia.

Exemplos:

- Apto: rotina e convivencia.
- Study Buddy: disciplina e horario.
- Mentor Match: area e objetivo.

## Anti-Contratos

Estes itens nao devem virar ponto de extensao principal:

- autenticacao real;
- deploy;
- reputacao generica;
- moderacao generica;
- notificacoes genericas;
- acao de interacao como variacao principal.

Eles podem existir no Apto, mas nao precisam compor o nucleo do framework da Fase 2.

