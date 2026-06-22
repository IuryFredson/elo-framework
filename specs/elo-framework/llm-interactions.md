# Registro de Interações com LLM

Este arquivo local ajuda a organizar os prompts usados durante a evolução do Apto para o Elo Framework.

Para o estudo oficial, copiar cada interação para o Google Docs compartilhado da equipe, mantendo prompt e resposta integrais.

## Cabeçalho do Documento Oficial

```text
Registro de interações - Fase 2
Grupo: [preencher número do grupo]
Ferramenta: Codex
Integrantes:
- Iury Fredson Germano Miranda
- Gabriel Eugenio Vitalino da Silva
- Matheus Henrique Ferreira da Silva
```

## Modelo de Registro

```text
=== Interação X ===

Data:
[preencher data]

Tema:
[ex: Revisão da arquitetura do framework]

Prompt enviado:
[colar prompt integral]

Resposta da LLM:
[colar resposta integral]

Decisão da equipe:
[Aceito | Adaptado | Rejeitado]

Justificativa da decisão:
[explicar brevemente]

Adaptações feitas, se houver:
[descrever ajustes na especificação ou no código]

Commit relacionado, se houver:
[hash e mensagem]
```

## Interações

### Interação 1

Data: 2026-06-20

Tema: Revisão da arquitetura mínima para entrega parcial do Elo Framework

Prompt enviado:

```text
Você é um revisor técnico de arquitetura de software e Spec-Driven Development.

Use o contexto base do Elo Framework abaixo:
Estamos evoluindo a aplicação Apto, da Fase 1 da disciplina Projeto Detalhado de Software, para o Elo Framework na Fase 2.

O Elo Framework é um framework para Plataformas Baseadas em Perfis, Ofertas, Manifestação de Interesse e Compatibilidade.

Equipe: Iury Fredson Germano Miranda, Gabriel Eugenio Vitalino da Silva e Matheus Henrique Ferreira da Silva.

Pontos fixos do framework:
1. Cadastro e gestão de usuários.
2. Cadastro de perfis.
3. Publicação de ofertas.
4. Manifestação de Interesse.
5. Cálculo de compatibilidade.

Pontos flexíveis do framework:
1. Dados do perfil.
2. Tipo de oferta publicada.
3. Critérios de compatibilidade.

Regra importante:
Manifestação de Interesse é um mecanismo fixo de interação em todas as instâncias. No Apto, o usuário manifesta interesse em moradia ou vaga. No Study Buddy, o usuário manifesta interesse em grupo de estudo. No Mentor Match, o usuário manifesta interesse em sessão ou programa de mentoria. Manifestação de Interesse não deve ser tratada como ponto flexível.

Exigência parcial do professor:
Para a próxima semana, o grupo deve trazer um diagrama de classe do framework explicitando as classes/interfaces que são pontos flexíveis e o código da aplicação da Fase 1 já instanciada no framework. Ou seja, cada ponto flexível do framework deve ser estendido para contemplar a aplicação da Fase 1.

Prioridade atual:
Focar primeiro no Apto instanciado no framework. Study Buddy e Mentor Match podem ficar para etapa posterior, salvo quando forem necessários para demonstrar extensibilidade.

Não implemente autenticação real, deploy, frontend novo ou reescrita completa do domínio.

Tarefa:
Analise a arquitetura mínima necessária para atender à entrega parcial do professor e produza apenas um parecer técnico. Não use Aceito, Adaptado ou Rejeitado como veredito, pois essa decisão pertence exclusivamente à equipe no registro do estudo.

Quero que você responda:
1. Quais classes/interfaces devem pertencer ao núcleo do framework.
2. Quais classes/interfaces devem ser pontos flexíveis.
3. Como o Apto deve instanciar cada ponto flexível.
4. Quais classes atuais do Apto podem ser reaproveitadas.
5. Quais mudanças devem ser evitadas para não quebrar a aplicação existente.
6. Qual seria a menor implementação tecnicamente suficiente para demonstrar que o Apto está instanciado no framework.
7. Quais pontos da sua resposta exigem decisão posterior da equipe.

Não implemente código.
Não crie novas instâncias além do Apto nesta resposta.
Não trate Manifestação de Interesse como ponto flexível.
Não proponha uma reescrita completa do domínio.
Não classifique sua própria resposta como Aceita, Adaptada ou Rejeitada. Ao final, use apenas a seção "Parecer técnico" com recomendações; a decisão formal será feita pelos estudantes.

Leia as specs atuais nos arquivos locais:
- specs/elo-framework/spec.md
- specs/elo-framework/data-model.md
- specs/elo-framework/contracts.md
- specs/elo-framework/tasks.md
```

Resposta da LLM:

```text
Parecer Técnico

A arquitetura mínima deve concentrar a entrega parcial em uma coisa: tornar visível, no código e no diagrama, que o Apto instancia o Elo Framework. Para isso, não é necessário implementar Study Buddy e Mentor Match agora. Eles devem permanecer como evidência conceitual de extensibilidade, mas a entrega da próxima semana deve provar que a aplicação da Fase 1 já estende os pontos flexíveis do framework.

1. Classes/Interfaces Do Núcleo

O núcleo do framework deveria ter poucos contratos, cada um ligado diretamente a um ponto fixo ou flexível. Uma estrutura mínima defensável seria:

PerfilFramework
OfertaFramework
ManifestacaoInteresseFramework
CompatibilidadeStrategy

PerfilFramework representa o ponto flexível Dados do Perfil. O framework sabe que existe perfil, mas não sabe quais dados cada domínio coleta.

OfertaFramework representa o ponto flexível Tipo de Oferta Publicada. O framework sabe que usuários publicam ofertas, mas não fixa se a oferta é moradia, grupo de estudo ou mentoria.

ManifestacaoInteresseFramework representa o ponto fixo Manifestação de Interesse. Aqui o contrato não deve criar variação de ação; ele só formaliza o mecanismo comum de interesse em uma oferta.

CompatibilidadeStrategy representa o ponto flexível Critérios de Compatibilidade. O framework sabe que compatibilidade é calculada, mas cada instância define os critérios.

Eu evitaria criar contratos para tudo agora, como UsuarioFramework, CadastroUsuarioFramework ou serviços genéricos de CRUD. O código atual já evidencia cadastro/gestão de usuários por Usuario, UsuarioUniversitario, Locador e seus services. Generalizar isso agora aumenta risco e pouco ajuda na entrega parcial.

2. Pontos Flexíveis

Os pontos flexíveis devem continuar exatamente estes:

Dados do perfil
Tipo de oferta publicada
Critérios de compatibilidade

Mapeamento recomendado:

Dados do perfil -> PerfilFramework
Tipo de oferta publicada -> OfertaFramework
Critérios de compatibilidade -> CompatibilidadeStrategy

Manifestação de Interesse deve aparecer no diagrama como ponto fixo. Ela pode ter um contrato, mas esse contrato não é ponto flexível.

3. Apto Instanciando Cada Ponto Flexível

O Apto deve aparecer como instância concreta assim:

PerfilConvivencia -> implementa/adapta PerfilFramework
Anuncio, com Moradia associada -> implementa/adapta OfertaFramework
CompatibilidadeDeterministicaCalculator ou AptoCompatibilidadeStrategy -> implementa CompatibilidadeStrategy
ManifestacaoInteresse -> implementa/adapta ManifestacaoInteresseFramework

A melhor opção para compatibilidade é criar uma CompatibilidadeStrategy e fazer a regra atual do Apto se encaixar nela. Isso pode ser feito renomeando conceitualmente o papel de CompatibilidadeDeterministicaCalculator ou criando uma classe fina como AptoCompatibilidadeStrategy que delega para ele. A segunda opção tende a ser melhor para apresentação, porque deixa explícito o nome da instância.

Para perfil e oferta, eu seria conservador. Se implementar interfaces diretamente nas entidades JPA for simples, pode funcionar. Se começar a poluir entidade com métodos artificiais, melhor usar adapters:

PerfilConvivenciaAdapter implements PerfilFramework
AnuncioOfertaAdapter implements OfertaFramework

4. Classes Atuais Reaproveitáveis

Reaproveitamento direto:

Usuario
UsuarioUniversitario
Locador
PerfilConvivencia
Anuncio
Moradia
ManifestacaoInteresse
MatchmakingService
CompatibilidadeDeterministicaCalculator
ResultadoCompatibilidade
OrigemCompatibilidade

Papel de cada uma na arquitetura:

Usuario / UsuarioUniversitario / Locador evidenciam o ponto fixo de cadastro e gestão de usuários.

PerfilConvivencia instancia o ponto flexível Dados do Perfil no Apto.

Anuncio / Moradia instanciam o ponto flexível Tipo de Oferta Publicada no Apto.

ManifestacaoInteresse concretiza o mecanismo fixo de Manifestação de Interesse.

CompatibilidadeDeterministicaCalculator contém os critérios de compatibilidade do Apto.

MatchmakingService orquestra o cálculo de compatibilidade usando a regra do Apto.

5. Mudanças A Evitar

Evitar nesta entrega:

Reescrever entidades JPA.
Mudar endpoints públicos.
Alterar DTOs do frontend.
Mover muitos pacotes.
Criar CRUD genérico para todos os conceitos.
Criar Study Buddy e Mentor Match antes do Apto estar instanciado.
Criar outro tipo de interação além de Manifestação de Interesse.
Transformar reputação, denúncia, moderação ou notificação em parte obrigatória do framework.

O critério para aceitar uma mudança deve ser: ela ajuda o professor a ver, no código e no diagrama, que o Apto estende um ponto flexível do framework?

Se não ajuda, deve ficar fora da entrega parcial.

6. Menor Implementação Tecnicamente Suficiente

A menor implementação suficiente seria:

1. Criar pacote de framework, por exemplo:

com.apto.framework

ou, se quiser deixar mais claro academicamente:

com.apto.framework.core
com.apto.framework.apto

2. Criar contratos mínimos:

PerfilFramework
OfertaFramework
ManifestacaoInteresseFramework
CompatibilidadeStrategy

3. Criar a instância Apto dos pontos flexíveis:

AptoPerfilConvivenciaAdapter ou PerfilConvivencia implements PerfilFramework
AptoOfertaAdapter ou Anuncio implements OfertaFramework
AptoCompatibilidadeStrategy implements CompatibilidadeStrategy

4. Manter Manifestação de Interesse como fixo:

ManifestacaoInteresse implements ManifestacaoInteresseFramework

ou adapter equivalente, se não quiser tocar na entidade.

5. Ajustar MatchmakingService para depender do contrato de compatibilidade, sem alterar resposta pública:

MatchmakingService -> CompatibilidadeStrategy

6. Criar ou atualizar testes pequenos:

Teste da estratégia de compatibilidade do Apto.
Teste garantindo que MatchmakingService continua ordenando/retornando matches.

7. Criar diagrama de classes em PlantUML ou imagem equivalente mostrando:

Framework core:
PerfilFramework
OfertaFramework
ManifestacaoInteresseFramework
CompatibilidadeStrategy

Instância Apto:
PerfilConvivencia
Anuncio/Moradia
ManifestacaoInteresse
AptoCompatibilidadeStrategy

7. Pontos Que Exigem Decisão Da Equipe

A equipe precisa decidir antes de implementar:

Usar interfaces diretamente nas entidades ou adapters?

Minha recomendação técnica: usar adapters quando a interface exigir métodos artificiais; implementar diretamente só se ficar natural.

Criar AptoCompatibilidadeStrategy ou adaptar CompatibilidadeDeterministicaCalculator diretamente?

Minha recomendação técnica: criar AptoCompatibilidadeStrategy delegando para a calculadora atual, porque isso deixa a instanciação mais clara no diagrama.

O diagrama terá apenas Apto ou mostrará Study Buddy/Mentor Match como exemplos futuros?

Minha recomendação técnica: diagrama principal com framework + Apto. Study Buddy e Mentor Match, se aparecerem, devem ser notas ou caixas externas de extensibilidade futura.

Atualizar README agora ou só depois da implementação?

Minha recomendação técnica: atualizar depois da implementação mínima, para documentar o que realmente ficou no código.

Parecer técnico

A arquitetura deve seguir com contratos pequenos e focados nos três pontos flexíveis. A prioridade técnica mais segura é começar por compatibilidade, porque o ponto variável é claro e o código já está relativamente isolado em service/matchmaking. Depois, perfil e oferta podem ser ligados ao framework por interfaces ou adapters, com o menor impacto possível nas entidades JPA.

Para a entrega parcial, o objetivo não é “completar” o Elo Framework inteiro. O objetivo é mostrar que o núcleo tem pontos flexíveis explícitos e que o Apto, como aplicação da Fase 1, já os instancia no código e no diagrama.
```

Decisão da equipe:

```text
[Aceito | Adaptado | Rejeitado]
```

Justificativa da decisão:

```text

```

Adaptações feitas, se houver:

```text

```

Commit relacionado, se houver:

```text

```

### Interação 2

Data: 2026-06-22

Tema: Definição das classes e interfaces mínimas do Elo Framework

Prompt enviado:


```text
Você é um arquiteto Java/Spring Boot revisando uma aplicação acadêmica que será evoluída para framework.

Use o contexto base do Elo Framework abaixo:
Estamos evoluindo a aplicação Apto, da Fase 1 da disciplina Projeto Detalhado de Software, para o Elo Framework na Fase 2.

O Elo Framework é um framework para Plataformas Baseadas em Perfis, Ofertas, Manifestação de Interesse e Compatibilidade.

Equipe: Iury Fredson Germano Miranda, Gabriel Eugenio Vitalino da Silva e Matheus Henrique Ferreira da Silva.

Pontos fixos do framework:
1. Cadastro e gestão de usuários.
2. Cadastro de perfis.
3. Publicação de ofertas.
4. Manifestação de Interesse.
5. Cálculo de compatibilidade.

Pontos flexíveis do framework:
1. Dados do perfil.
2. Tipo de oferta publicada.
3. Critérios de compatibilidade.

Regra importante:
Manifestação de Interesse é um mecanismo fixo de interação em todas as instâncias. No Apto, o usuário manifesta interesse em moradia ou vaga. No Study Buddy, o usuário manifesta interesse em grupo de estudo. No Mentor Match, o usuário manifesta interesse em sessão ou programa de mentoria. Manifestação de Interesse não deve ser tratada como ponto flexível.

Exigência parcial do professor:
Para a próxima semana, o grupo deve trazer um diagrama de classe do framework explicitando as classes/interfaces que são pontos flexíveis e o código da aplicação da Fase 1 já instanciada no framework. Ou seja, cada ponto flexível do framework deve ser estendido para contemplar a aplicação da Fase 1.

Prioridade atual:
Focar primeiro no Apto instanciado no framework. Study Buddy e Mentor Match podem ficar para etapa posterior, salvo quando forem necessários para demonstrar extensibilidade.

Não implemente autenticação real, deploy, frontend novo ou reescrita completa do domínio.

Tarefa:
Proponha as classes e interfaces mínimas do framework para representar os pontos fixos e flexíveis.

Obrigatório:
- Mostrar quais elementos são fixos.
- Mostrar quais elementos são pontos flexíveis.
- Mostrar como o Apto implementa ou estende cada ponto flexível.
- Preservar o comportamento atual do Apto.
- Manter o escopo pequeno.

Pontos flexíveis:
1. Dados do perfil.
2. Tipo de oferta publicada.
3. Critérios de compatibilidade.

Manifestação de Interesse é ponto fixo, não flexível.

Responda com:
1. Lista de interfaces/classes propostas.
2. Responsabilidade de cada uma.
3. Relação com classes atuais do Apto.
4. Riscos da proposta.
5. Alternativas consideradas e por que foram descartadas.

Não implemente código ainda.

Leia as classes atuais relevantes nos arquivos locais:
- backend/apto-api/src/main/java/com/apto/model/entity/Usuario.java
- backend/apto-api/src/main/java/com/apto/model/entity/UsuarioUniversitario.java
- backend/apto-api/src/main/java/com/apto/model/entity/PerfilConvivencia.java
- backend/apto-api/src/main/java/com/apto/model/entity/Anuncio.java
- backend/apto-api/src/main/java/com/apto/model/entity/ManifestacaoInteresse.java
- backend/apto-api/src/main/java/com/apto/service/matchmaking/MatchmakingService.java
- backend/apto-api/src/main/java/com/apto/service/matchmaking/CompatibilidadeDeterministicaCalculator.java
```


Resposta da LLM:

```text
1. Classes e interfaces propostas

Núcleo do framework:

- UsuarioFramework: interface fixa que representa o usuário participante da plataforma.
- PerfilFramework: interface flexível para os perfis das instâncias.
- OfertaFramework: interface flexível para as ofertas publicadas.
- ManifestacaoInteresseFramework: interface fixa para o mecanismo comum de interesse.
- CompatibilidadeStrategy<T>: interface flexível para elegibilidade e cálculo.
- ResultadoCompatibilidade: classe ou record fixo para o resultado padronizado.
- CompatibilidadeService<T>: classe fixa que executa o fluxo usando uma estratégia.

Também podem existir contratos pequenos de serviço para os comportamentos fixos: GestaoUsuario<U>, GestaoPerfil<U, P>, PublicacaoOferta<O> e GestaoManifestacaoInteresse<I>. Eles podem ser implementados por adapters que reutilizem os serviços atuais do Apto.

2. Responsabilidades

UsuarioFramework

Representa os dados comuns mínimos de um usuário: identificador, nome e estado ativo ou inativo. Não deve conter campos específicos do Apto, como curso ou e-mail institucional.

PerfilFramework

Marca um objeto como perfil válido para uma instância do Elo. Não é recomendável criar getters genéricos para todos os atributos, porque os dados variam por domínio. Inicialmente, o contrato pode expor somente uma identificação do tipo de perfil.

OfertaFramework

Representa algo publicado por um usuário. Suas responsabilidades mínimas são identificação da oferta, identificação do publicador, estado da publicação e tipo da oferta. Campos como valor mensal, moradia e tipo de anúncio permanecem específicos do Apto.

ManifestacaoInteresseFramework

Define o conceito fixo de um usuário manifestando interesse em uma oferta: interessado, oferta alvo, status, data da manifestação e resposta ou cancelamento.

O nome InteracaoFramework, presente nas specs atuais, é genérico demais. Como a decisão conceitual já estabelece Manifestação de Interesse em todas as instâncias, o nome explícito reduz ambiguidade.

CompatibilidadeStrategy<T>

Deve concentrar o ponto flexível “critérios de compatibilidade”.

Contrato conceitual mínimo:

boolean elegivel(T solicitante, T candidato);
ResultadoCompatibilidade calcular(T solicitante, T candidato);

O método elegivel é necessário porque o Apto atualmente verifica preferência de gênero antes do cálculo percentual.

O tipo genérico não deve ser limitado imediatamente a PerfilFramework, pois a compatibilidade atual também usa Genero, que pertence a UsuarioUniversitario, e não a PerfilConvivencia.

ResultadoCompatibilidade

Pode reutilizar a classe existente. Deve continuar contendo, no mínimo, percentual, justificativa e origem do cálculo. Adicionar critérios atendidos pode ficar para uma evolução posterior.

CompatibilidadeService<T>

Representa o fluxo fixo de receber participantes, aplicar elegibilidade, executar a estratégia, ordenar resultados e limitar a quantidade retornada. A estratégia decide os critérios; o serviço controla o processo.

3. Relação com o Apto

Dados do perfil:

PerfilFramework
    └── PerfilConvivencia

PerfilConvivencia é a implementação concreta do perfil da instância Apto. Para evitar impacto nas entidades JPA, pode implementar diretamente PerfilFramework ou ser envolvido por AptoPerfilAdapter. A implementação direta é menor, desde que a interface não exija métodos artificiais.

Tipo de oferta:

OfertaFramework
    └── Anuncio
            └── contém Moradia

Anuncio representa a publicação, enquanto Moradia contém os dados específicos do imóvel. Portanto, Anuncio deve ser a oferta concreta do Apto. Um AptoOfertaAdapter pode ser usado caso o contrato do framework não combine naturalmente com os getters da entidade.

Critérios de compatibilidade:

CompatibilidadeStrategy<UsuarioUniversitario>
    └── AptoCompatibilidadeStrategy
            └── delega para CompatibilidadeDeterministicaCalculator

AptoCompatibilidadeStrategy seria a extensão mais explícita para o diagrama e para a entrega acadêmica. Ela pode delegar elegivel para preferenciaGeneroCompativel e calcular para CompatibilidadeDeterministicaCalculator.calcular, preservando o algoritmo atual.

Manifestação de Interesse:

ManifestacaoInteresseFramework
    └── ManifestacaoInteresse

Essa relação demonstra reutilização do mecanismo fixo. Ela não representa um novo ponto flexível.

Serviços atuais:

- UsuarioUniversitarioService e LocadorService instanciam a gestão de usuários.
- PerfilService instancia a gestão de perfis.
- AnuncioService instancia a publicação de ofertas.
- ManifestacaoInteresseService instancia o mecanismo fixo de interesse.
- MatchmakingService instancia o fluxo de compatibilidade.
- CompatibilidadeDeterministicaCalculator preserva os critérios concretos do Apto.

Os controllers, DTOs e endpoints podem continuar inalterados.

4. Riscos

1. Interfaces sem responsabilidade observável podem existir apenas para satisfazer o diagrama.
2. Fazer entidades JPA implementarem contratos grandes pode introduzir métodos artificiais e dificultar a persistência.
3. Limitar o contrato de compatibilidade a perfil não representa corretamente o código atual, pois a elegibilidade usa UsuarioUniversitario.
4. O framework não deve duplicar ResultadoCompatibilidade.
5. Modelar Study Buddy e Mentor Match agora pode gerar generalização prematura.
6. A extração da estratégia não deve remover o fluxo por LLM e o fallback determinístico do MatchmakingService.
7. Manter InteracaoFramework pode sugerir diferentes tipos de interação, contrariando a decisão atual.

5. Alternativas descartadas

Classe abstrata para perfil e oferta

Foi descartada porque PerfilConvivencia e Anuncio já são entidades JPA. Introduzir herança agora aumentaria o risco sobre o mapeamento das tabelas.

Framework baseado somente em entidades genéricas

Uma entidade genérica com campos dinâmicos, JSON ou mapas enfraqueceria a tipagem e exigiria reescrita do domínio existente.

Tornar Manifestação de Interesse uma Strategy

Foi descartado porque a manifestação é um mecanismo fixo. O que varia é a oferta concreta que recebe o interesse.

Alterar diretamente o calculador atual

Fazer CompatibilidadeDeterministicaCalculator implementar a interface seria menor em quantidade de classes, mas deixaria menos explícita a instanciação do Apto. Um adapter AptoCompatibilidadeStrategy produz uma demonstração acadêmica mais clara e preserva o calculador.

Criar interfaces CRUD completas

Contratos com muitos métodos forçariam alterações nos serviços existentes. Para a entrega parcial, os contratos devem apresentar apenas as operações necessárias para demonstrar os pontos fixos.

Parecer técnico

A estrutura mínima deve combinar contratos pequenos no núcleo com implementações ou adapters do Apto. A relação principal a ser mostrada no diagrama é:

PerfilFramework <- PerfilConvivencia
OfertaFramework <- Anuncio
CompatibilidadeStrategy <- AptoCompatibilidadeStrategy
ManifestacaoInteresseFramework <- ManifestacaoInteresse

Antes da implementação, a equipe precisa decidir:

- implementação direta ou adapters para PerfilConvivencia e Anuncio;
- substituição de InteracaoFramework por ManifestacaoInteresseFramework;
- criação de AptoCompatibilidadeStrategy ou implementação direta no calculador;
- quais contratos de serviço fixos precisam aparecer no código da entrega parcial.
```

Decisão da equipe:

```text
Adaptado
```

Justificativa da decisão:

```text
A proposta foi adaptada para utilizar implementação direta dos contratos pelas classes atuais do Apto, evitando adapters desnecessários e mantendo a estrutura menor para a entrega parcial.
```

Adaptações feitas, se houver:

```text
PerfilConvivencia implementará PerfilFramework e Anuncio implementará OfertaFramework diretamente. CompatibilidadeDeterministicaCalculator implementará CompatibilidadeStrategy diretamente, preservando o comportamento atual e sem criar AptoCompatibilidadeStrategy.
```

Commit relacionado, se houver:

```text

```
