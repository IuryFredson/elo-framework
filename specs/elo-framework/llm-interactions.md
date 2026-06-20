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
