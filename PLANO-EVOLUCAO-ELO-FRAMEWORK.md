# Plano de evolução do Elo Framework

## Resumo

Transformar `elo-core` em um framework Spring/JPA híbrido: o núcleo controla fluxos comuns por Template Method e chama hooks/portas implementados pelo Apto. A dependência permanece unidirecional:

```text
apto -> elo-core
```

`Usuario` e estados fixos foram movidos ao core. Entidades persistentes específicas, DTOs, mappers, repositories, controllers e regras de moradia permanecem no Apto. Avaliação e reputação continuam exclusivas do Apto. Nenhuma outra instância foi implementada nesta entrega.

Cada etapa foi entregue isoladamente, com o reactor Maven compilando e testes passando.

## Contratos públicos do framework

Contratos e templates finais:

- `Usuario`;
- `Perfil`;
- `Oferta`;
- `ManifestacaoInteresse`;
- `Denuncia`;
- `CriterioDenuncia`;
- `CompatibilidadeStrategy`;
- `ProvedorCompatibilidadeLlm`;
- `RepositorioBase`;
- `UsuarioService<T extends Usuario, C, A, R>`;
- `PerfilService`;
- `OfertaService`;
- `ManifestacaoInteresseService`;
- `DenunciaService`;
- `ModeracaoService`;
- `MatchingService`.

Os services do Apto estendem essas classes abstratas. Não foram criadas interfaces de service que deixassem o algoritmo fixo novamente no Apto.

## Etapas executadas

### 1. Estabilizar a fronteira do core

Status: concluída.

Resultado:

- Dependências do `elo-core` ajustadas para JPA, Validation e transações Spring.
- Contratos, estados, exceções comuns e portas independentes do Apto definidos.
- Nomes provisórios como `*Framework` e `STATUS_INTERACAO` removidos.
- Teste arquitetural garante que `elo-core` não referencia `com.apto`.

### 2. Migrar `Usuario` e implementar seu Template Method

Status: concluída.

Resultado:

- `Usuario` movido para `com.elo.usuario`.
- `UsuarioService` controla o fluxo comum de criação, listagem, busca, atualização, ativação/inativação e exclusão.
- `LocadorService` e `UsuarioUniversitarioService` estendem o template.
- Validações específicas permanecem no Apto.

### 3. Extrair o fluxo de perfil

Status: concluída.

Resultado:

- `Perfil` virou contrato variável do core.
- `PerfilService` controla busca e criação/atualização do perfil.
- `PerfilConvivencia` e `PerfilService` do Apto instanciam esse ponto flexível.

### 4. Extrair publicação e gestão de ofertas

Status: concluída.

Resultado:

- `Oferta` virou contrato do core.
- `OfertaService` controla criação, consulta, atualização, status e remoção.
- `AnuncioService` estende o template.
- `Anuncio` instancia a oferta do Apto.

### 5. Extrair manifestação de interesse

Status: concluída.

Resultado:

- `StatusManifestacaoInteresse` movido para o core.
- `ManifestacaoInteresseService` controla oferta ativa, interesse próprio, duplicidade, autorização e transições.
- Operação comum `cancelarPendentesDaOferta` adicionada.
- Adapter provisório eliminado.

### 6. Extrair denúncia e moderação

Status: concluída.

Resultado:

- `StatusDenuncia` e máquina de estados movidos para o core.
- `DenunciaService` controla criação, consultas, transições e exclusão.
- `ModeracaoService` controla validação fixa da decisão e aplicação de ação na oferta por hook.
- `CriterioDenunciaApto` criado.
- Campo `criterio` adicionado ao request, entidade e response do Apto.

### 7. Completar compatibilidade e matching

Status: concluída.

Resultado:

- `MatchingService` controla elegibilidade, cálculo, ordenação, `topN`, origem do resultado e fallback.
- `ResultadoMatching` identifica diretamente o candidato.
- `IdentityHashMap` foi eliminado do `MatchmakingService`.
- Critérios determinísticos, elegibilidade por gênero, prompt, parser, Groq e mapper permanecem no Apto.

### 8. Remover o Observer e isolar funcionalidades do Apto

Status: concluída.

Resultado:

- Publisher, observers, eventos e testes removidos.
- `AnuncioService` e `ModeracaoService` chamam explicitamente o cancelamento de manifestações.
- `AvaliacaoService` chama diretamente `ReputacaoCalculoService`.
- Notificações de anúncio indisponível foram removidas.
- Avaliação e reputação permanecem exclusivamente em `com.apto`.

### 9. Atualizar documentação e validar a instanciação

Status: concluída nesta etapa.

Resultado:

- Especificação, contratos, modelo, diagrama, tarefas, rastreabilidade, pesquisa, plano e README atualizados.
- Study Buddy e Mentor Match removidos como implementação planejada da entrega atual.
- Hooks obrigatórios para uma futura instância documentados.
- Testes do core usam implementações falsas para provar funcionamento sem depender do Apto.

## Testes e aceitação

Comando principal:

```bash
cd backend
mvn test
```

Critérios finais:

- `elo-core` compila e testa sem depender de `com.apto`.
- `apto-api` preserva endpoints e comportamentos essenciais.
- Core controla os fluxos fixos.
- Apto instancia os pontos flexíveis.
- Documentação e diagrama refletem a arquitetura implementada.

## Premissas

- O framework continua baseado em Spring Boot/JPA.
- DTOs, controllers, mappers e repositories concretos pertencem a cada instância.
- O schema e os nomes JSON atuais foram preservados, exceto pelo campo aditivo `criterio` em denúncia.
- O frontend fica fora da evolução do framework.
- Study Buddy e Mentor Match são exemplos futuros, não implementação desta entrega.
