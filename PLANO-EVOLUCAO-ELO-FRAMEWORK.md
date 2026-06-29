# Plano de evolução do Elo Framework

## Resumo

Transformar `elo-core` em um framework Spring/JPA híbrido: o núcleo controla os fluxos comuns por Template Method e chama hooks/portas implementados pelo Apto. A dependência permanece unidirecional (`apto → elo-core`).

`Usuario` e estados fixos serão movidos ao core. Entidades persistentes específicas, DTOs, mappers, repositories, controllers e regras de moradia permanecem no Apto. Avaliação e reputação continuam exclusivas do Apto. Nenhuma outra instância será implementada.

Cada etapa abaixo deve ser entregue isoladamente, terminando com o reactor Maven compilando e todos os testes passando.

## Contratos públicos do framework

- Substituir os contratos provisórios por `Usuario`, `Perfil`, `Oferta`, `ManifestacaoInteresse` e `Denuncia`.
- Mover para o core os estados fixos de manifestação e denúncia.
- Criar `CriterioDenuncia`, implementado por cada instância.
- Criar os Template Methods genéricos:
  - `UsuarioService<T extends Usuario, C, A, R>`;
  - `PerfilService`;
  - `OfertaService`;
  - `ManifestacaoInteresseService`;
  - `DenunciaService`;
  - `ModeracaoService`.
- Manter `CompatibilidadeStrategy`, acrescentando uma porta para LLM e um fluxo fixo de matching com fallback determinístico.
- Os services do Apto passam a estender essas classes abstratas; não serão criadas interfaces de service que deixem todo o algoritmo novamente no Apto.

## Etapas de execução

### 1. Estabilizar a fronteira do core

- Ajustar dependências do `elo-core` para JPA, Validation e transações Spring.
- Definir os contratos, estados, exceções comuns e portas independentes de DTO/repository do Apto.
- Remover nomes provisórios como `*Framework` e `STATUS_INTERACAO`.
- Adicionar teste arquitetural garantindo que `elo-core` não referencia `com.apto`.

### 2. Migrar `Usuario` e implementar seu Template Method

- Mover a entidade abstrata `Usuario` para `com.elo.usuario`, preservando `usuarios`, UUID e herança `JOINED`.
- Configurar o Apto para descobrir entidades de `com.elo` e `com.apto`.
- Implementar no core o fluxo comum de criar, listar, buscar, atualizar, ativar/inativar e excluir.
- Deixar como hooks: construção da entidade, DTO, mapper, validações específicas, persistência e pós-criação.
- Fazer `LocadorService` e `UsuarioUniversitarioService` estenderem o template.
- Manter validação específica de documento e e-mail institucional.
- Manter no hook do locador a criação transacional do `PerfilAnunciante`.

### 3. Extrair o fluxo de perfil

- Tornar `Perfil` o contrato variável do core.
- Implementar busca e criação/atualização do perfil como Template Method.
- Adaptar `PerfilConvivencia` e `PerfilService`.
- Manter campos, validações, DTOs e mapper de convivência no Apto.

### 4. Extrair publicação e gestão de ofertas

- Tornar `Oferta` o contrato do core e fazer `Anuncio` implementá-lo.
- Implementar no `OfertaService` o ciclo comum de criação, consulta, atualização, alteração de status e remoção.
- Deixar em hooks as regras de `PerfilAnunciante`, `Moradia`, filtros e mapeamento.
- Fazer `AnuncioService` estender o template, mantendo endpoints e busca paginada atuais.

### 5. Extrair manifestação de interesse

- Mover `StatusManifestacaoInteresse` para o core.
- Implementar no template as regras fixas: oferta ativa, proibição de interesse próprio, duplicidade ativa, autorização e transições aceitar/recusar/cancelar.
- Acrescentar operação comum para cancelar manifestações pendentes quando uma oferta ficar indisponível.
- Adaptar a entidade e o service do Apto; eliminar o adapter `ManifestacaoInteresseFrameworkAdapter`.

### 6. Extrair denúncia e moderação

- Mover `StatusDenuncia` e sua máquina de estados para o core.
- Implementar criação, consultas, transições e exclusão em `DenunciaService`.
- Implementar em `ModeracaoService` a validação fixa da decisão e a aplicação de pausar/encerrar oferta por porta.
- Criar `CriterioDenunciaApto` com:
  `ANUNCIO_ENGANOSO`, `PRECO_ABUSIVO`, `IMOVEL_INEXISTENTE`, `CONTEUDO_INAPROPRIADO` e `OUTRO`.
- Adicionar `criterio` ao request, entidade e response do backend. Ausência no payload será convertida para `OUTRO`, preservando o frontend atual.
- Manter `titulo` e `corpo`; o frontend não será alterado.
- Permitir coluna legada nula, mas aplicar `OUTRO` em novas inclusões e atualizações.

### 7. Completar compatibilidade e matching

- Fazer o core controlar elegibilidade, cálculo, ordenação, `topN`, origem do resultado e fallback.
- Corrigir o resultado para identificar diretamente o candidato, eliminando os `IdentityHashMap` do `MatchmakingService`.
- Manter como extensões do Apto:
  - critérios de convivência determinísticos;
  - elegibilidade por gênero;
  - prompt, parser e integração Groq;
  - mapper dos DTOs.
- Em sucesso, usar resultados LLM; em falha ou resultado ausente, calcular deterministicamente.

### 8. Remover o Observer e isolar funcionalidades do Apto

- Remover publisher, observers, eventos e respectivos testes.
- `AnuncioService` e `ModeracaoService` chamarão explicitamente o cancelamento de manifestações em uma transação.
- `AvaliacaoService` chamará diretamente `ReputacaoCalculoService` após criar, alterar, excluir ou reativar avaliação.
- Remover notificações de anúncio indisponível, incluindo entidade e repository sem consumidores.
- Manter avaliação, reputação e seus modelos exclusivamente em `com.apto`.

### 9. Atualizar documentação e validar a instanciação

- Reescrever especificação, contratos, modelo, diagrama, tarefas e README conforme a arquitetura final.
- Remover do plano atual Study Buddy, Mentor Match e qualquer implementação futura.
- Documentar os hooks obrigatórios para uma futura instância.
- Criar implementações falsas somente nos testes do core para provar que os templates funcionam sem depender do Apto.

## Testes e aceitação

- Executar `.\mvnw.cmd test` ao final de cada etapa; a linha de base atual é de 139 testes aprovados.
- Cobrir no core a ordem dos Template Methods, hooks, validações comuns e falhas das portas.
- Cobrir no Apto:
  - usuários e criação automática do perfil anunciante;
  - manifestação, autorizações e transições;
  - critérios de denúncia, fallback `OUTRO` e moderação;
  - cancelamento ao pausar, encerrar, moderar ou remover oferta;
  - LLM, fallback determinístico, ordenação e `topN`;
  - recálculo direto de reputação;
  - inicialização JPA com `Usuario` no core.
- Considerar concluído quando o Apto preservar seus endpoints e comportamentos essenciais, e o core controlar os fluxos sem qualquer dependência de `com.apto`.

## Premissas

- O framework continuará baseado em Spring Boot/JPA.
- DTOs, controllers, mappers e repositories concretos pertencem a cada instância.
- O schema e os nomes JSON atuais serão preservados, exceto pelo campo aditivo `criterio`.
- O frontend fica fora desta sequência, mas continuará enviando denúncias graças ao default `OUTRO`.
- Tabelas antigas de notificação podem permanecer em bancos existentes sob `ddl-auto=update`; ambientes novos não as criarão.
