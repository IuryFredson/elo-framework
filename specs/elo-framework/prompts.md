# Prompts para Desenvolvimento do Elo Framework

Este arquivo reúne prompts prontos para usar no estudo com LLM. Eles foram escritos para apoiar a evolução do Apto para o Elo Framework seguindo Spec-Driven Development.

Uso recomendado:

- copiar o prompt integral;
- referenciar os arquivos locais necessários do backend quando o prompt pedir contexto de código;
- registrar prompt e resposta integrais no Google Docs do estudo;
- registrar a decisão da equipe: Aceito, Adaptado ou Rejeitado;
- se adaptar sugestão de código, mencionar a adaptação no commit.

## Contexto base para todos os prompts

Use este contexto no início dos prompts quando a LLM não tiver memória do projeto.

```text
Estamos evoluindo a aplicação Apto, da Fase 1 da disciplina Projeto Detalhado de Software, para o Elo Framework na Fase 2.

O Elo Framework é um framework para Plataformas Baseadas em Perfis, Ofertas, Manifestação de Interesse e Compatibilidade.

Equipe:
- Iury Fredson Germano Miranda.
- Gabriel Eugenio Vitalino da Silva.
- Matheus Henrique Ferreira da Silva.

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
Manifestação de Interesse é um mecanismo fixo de interação em todas as instâncias.
No Apto, o usuário manifesta interesse em moradia ou vaga.
No Study Buddy, o usuário manifesta interesse em grupo de estudo.
No Mentor Match, o usuário manifesta interesse em sessão ou programa de mentoria.
Manifestação de Interesse não deve ser tratada como ponto flexível.

Exigência parcial do professor:
Para a próxima semana, o grupo deve trazer:
1. Diagrama de classe do framework explicitando as classes/interfaces que são pontos flexíveis.
2. Código da aplicação da Fase 1 já instanciada no framework, ou seja, cada ponto flexível do framework deve ser estendido para contemplar a aplicação da Fase 1 no framework.

Prioridade atual:
Focar primeiro no Apto instanciado no framework. Study Buddy e Mentor Match podem ficar para etapa posterior, salvo quando forem necessários para demonstrar a ideia de extensibilidade.

Não implemente autenticação real, deploy, frontend novo ou reescrita completa do domínio.
```

## Prompt 1: Revisar a arquitetura para a entrega parcial

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

## Prompt 2: Definir classes e interfaces do framework

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

## Prompt 3: Gerar diagrama de classe em PlantUML

```text
Você é um modelador UML.

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
Crie um diagrama de classes em PlantUML para a entrega parcial do professor.

O diagrama deve mostrar:
1. Classes/interfaces do núcleo do framework.
2. Quais interfaces/classes são pontos flexíveis.
3. Como o Apto instancia cada ponto flexível.
4. Manifestação de Interesse como ponto fixo.
5. Compatibilidade com critério variável, preferencialmente por Strategy ou contrato equivalente.

Pontos flexíveis obrigatórios:
- Dados do perfil.
- Tipo de oferta publicada.
- Critérios de compatibilidade.

Instanciação do Apto:
- PerfilConvivencia como dados de perfil.
- Anuncio/Moradia como oferta.
- Compatibilidade de convivência como critério de compatibilidade.
- ManifestacaoInteresse como uso do mecanismo fixo de Manifestação de Interesse.

Responda somente com:
1. Código PlantUML.
2. Uma explicação curta dos pontos flexíveis representados.

Não implemente código Java.
Não adicionar Study Buddy ou Mentor Match neste diagrama principal, a menos que seja apenas como nota externa de extensibilidade.
```

## Prompt 4: Planejar implementação em tarefas pequenas

```text
Você é um líder técnico planejando uma refatoração incremental em Java/Spring Boot.

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
Transforme a arquitetura do Elo Framework em tarefas pequenas para implementar o Apto como instância do framework.

Regras:
- Não quebrar endpoints existentes.
- Não reescrever o domínio.
- Não implementar frontend.
- Não implementar Study Buddy ou Mentor Match agora.
- Cada tarefa deve ter objetivo, arquivos prováveis, validação e risco.
- Cada tarefa deve se relacionar com um ponto flexível ou fixo.

Foque em:
1. Contrato de dados de perfil.
2. Contrato de oferta publicada.
3. Contrato/Strategy de critérios de compatibilidade.
4. Apto implementando esses contratos.
5. Testes para garantir que o Apto continua funcionando.
6. Diagrama de classes atualizado.

Responda com uma lista ordenada de tarefas.
Não implemente código.
```

## Prompt 5: Implementar apenas o contrato de compatibilidade

```text
Você é um desenvolvedor Java/Spring Boot.

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
Implemente apenas a primeira mudança de backend para explicitar o ponto flexível "Critérios de Compatibilidade".

Objetivo:
Criar um contrato ou Strategy de compatibilidade que permita ao Apto fornecer seus critérios de convivência sem alterar o comportamento existente.

Regras:
- Não alterar controllers.
- Não alterar DTOs públicos sem necessidade.
- Não alterar frontend.
- Não implementar Study Buddy ou Mentor Match.
- Não mudar a regra de Manifestação de Interesse.
- Preservar o funcionamento atual do MatchmakingService.

Antes de propor código, explique:
1. Quais arquivos devem ser alterados ou criados.
2. Por que a mudança atende ao ponto flexível.
3. Como validar com testes.

Depois, forneça o código necessário em blocos separados por arquivo.

Classes atuais:
backend/apto-api/src/main/java/com/apto/service/matchmaking/MatchmakingService.java
backend/apto-api/src/main/java/com/apto/service/matchmaking/CompatibilidadeDeterministicaCalculator.java
backend/apto-api/src/main/java/com/apto/service/matchmaking/ResultadoCompatibilidade.java
backend/apto-api/src/main/java/com/apto/service/matchmaking/OrigemCompatibilidade.java
backend/apto-api/src/main/java/com/apto/mapper/MatchmakingMapper.java
Procure testes atuais de matchmaking com rg --files backend/apto-api/src/test/java | rg "Match|Compatibilidade|matchmaking"
```

## Prompt 6: Implementar dados de perfil como ponto flexível

```text
Você é um desenvolvedor Java/Spring Boot.

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
Implemente ou adapte apenas o ponto flexível "Dados do Perfil" para mostrar que o Apto instancia esse ponto com PerfilConvivencia.

Regras:
- Não reescrever PerfilConvivencia.
- Não quebrar PerfilService.
- Não alterar endpoints existentes sem necessidade.
- Não implementar outras instâncias ainda.

Quero que você:
1. Identifique o menor contrato ou adapter necessário.
2. Mostre como PerfilConvivencia representa os dados de perfil da instância Apto.
3. Indique testes ou validações.
4. Forneça código apenas se for realmente necessário.

Classes atuais:
backend/apto-api/src/main/java/com/apto/model/entity/PerfilConvivencia.java
backend/apto-api/src/main/java/com/apto/service/PerfilService.java
backend/apto-api/src/main/java/com/apto/mapper/PerfilMapper.java
Leia os DTOs relevantes em backend/apto-api/src/main/java/com/apto/dto/request/AtualizarPerfilRequestDTO.java e backend/apto-api/src/main/java/com/apto/dto/response/PerfilResponseDTO.java
```

## Prompt 7: Implementar oferta como ponto flexível

```text
Você é um desenvolvedor Java/Spring Boot.

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
Implemente ou adapte apenas o ponto flexível "Tipo de Oferta Publicada" para mostrar que o Apto instancia esse ponto com Anuncio/Moradia.

Regras:
- Não quebrar AnuncioService.
- Não quebrar busca de anúncios.
- Não alterar frontend.
- Não transformar Manifestação de Interesse em ponto flexível.

Quero que você:
1. Identifique o menor contrato ou adapter necessário.
2. Mostre como Anuncio/Moradia representam a oferta da instância Apto.
3. Indique testes ou validações.
4. Forneça código apenas se for realmente necessário.

Classes atuais:
backend/apto-api/src/main/java/com/apto/model/entity/Anuncio.java
backend/apto-api/src/main/java/com/apto/model/entity/Moradia.java
backend/apto-api/src/main/java/com/apto/service/AnuncioService.java
backend/apto-api/src/main/java/com/apto/mapper/AnuncioMapper.java
Leia os DTOs relevantes em backend/apto-api/src/main/java/com/apto/dto/request/CriarAnuncioRequestDTO.java, backend/apto-api/src/main/java/com/apto/dto/request/AtualizarAnuncioRequestDTO.java e backend/apto-api/src/main/java/com/apto/dto/response/AnuncioResponseDTO.java
```

## Prompt 8: Revisar implementação do Apto instanciado

```text
Você é um revisor técnico de arquitetura e código.

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
Revise a implementação atual e diga se o Apto está realmente instanciado no Elo Framework conforme a exigência do professor.

Verifique:
1. Se cada ponto flexível foi estendido para contemplar o Apto.
2. Se os pontos fixos continuam claros.
3. Se Manifestação de Interesse ficou fixa.
4. Se houve refatoração excessiva.
5. Se o código preserva o comportamento da Fase 1.
6. Se os testes cobrem o suficiente para a entrega parcial.
7. Se o diagrama de classe está alinhado ao código.

Responda em formato de code review:
- Problemas por severidade.
- Ajustes recomendados.
- Evidências de que o Apto instancia o framework.
- Parecer técnico: adequado, adequado com ajustes ou inadequado.

Não implemente código.

Arquivos alterados:
Use git diff e git status para identificar os arquivos modificados

Diagrama:
Leia o arquivo do diagrama gerado no projeto, se existir
```

## Prompt 9: Preparar texto para explicar ao professor

```text
Você é um orientador ajudando a equipe a explicar uma entrega parcial acadêmica.

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
Crie uma explicação curta para apresentar ao professor como o Apto foi instanciado no Elo Framework.

A explicação deve cobrir:
1. O que é fixo no framework.
2. O que é flexível no framework.
3. Como o Apto implementa cada ponto flexível.
4. Por que Manifestação de Interesse é fixa.
5. Como o diagrama de classe evidencia isso.
6. Como o código preserva a aplicação da Fase 1.

Tom:
- acadêmico;
- objetivo;
- sem exagerar no escopo;
- adequado para apresentação oral.

Não implemente código.
```

## Prompt 10: Registrar decisão da equipe após resposta da LLM

Use este modelo no Google Docs após cada interação.

```text
=== Interação X ===

Prompt enviado:
Copiar automaticamente do prompt executado nesta interação

Resposta da LLM:
Copiar automaticamente da resposta recebida nesta interação

Decisão da equipe:
[Aceito | Adaptado | Rejeitado]

Justificativa:
[explicar por que a equipe aceitou, adaptou ou rejeitou]

Adaptações feitas, se houver:
[descrever mudanças feitas no código ou na especificação]

Commit relacionado, se houver:
[hash e mensagem do commit]
```

