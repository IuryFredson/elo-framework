package com.elo.compatibilidade.llm;

import com.elo.compatibilidade.OrigemCompatibilidade;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class JsonMatchesCompatibilidadeParser implements CompatibilidadeLlmParser {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JsonMatchesCompatibilidadeParser.class);

    private final ObjectMapper objectMapper;

    protected JsonMatchesCompatibilidadeParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<UUID, ResultadoCompatibilidade> parse(String conteudoJson) {
        try {
            JsonNode raiz = objectMapper.readTree(conteudoJson);
            JsonNode matches = raiz.get("matches");

            if (matches == null || !matches.isArray()) {
                throw erroMatchesAusente();
            }

            Map<UUID, ResultadoCompatibilidade> resultados = new HashMap<>();
            for (JsonNode match : matches) {
                adicionarSeValido(match, resultados);
            }
            return resultados;
        } catch (RuntimeException exception) {
            if (ehErroDaInstancia(exception)) {
                throw exception;
            }
            log.warn("Falha ao parsear resposta da LLM - ativando fallback. Causa: {}", exception.getMessage());
            throw erroJsonInvalido(exception);
        } catch (Exception exception) {
            log.warn("Falha ao parsear resposta da LLM - ativando fallback. Causa: {}", exception.getMessage());
            throw erroJsonInvalido(exception);
        }
    }

    protected abstract RuntimeException erroMatchesAusente();

    protected abstract RuntimeException erroJsonInvalido(Exception exception);

    protected abstract boolean ehErroDaInstancia(RuntimeException exception);

    private void adicionarSeValido(JsonNode match, Map<UUID, ResultadoCompatibilidade> resultados) {
        JsonNode candidatoIdNode = match.get("candidatoId");
        JsonNode percentualNode = match.get("percentual");
        JsonNode justificativaNode = match.get("justificativa");

        String candidatoId = candidatoIdNode != null && candidatoIdNode.isTextual()
                ? candidatoIdNode.asText()
                : null;
        int percentual = percentualNode != null && percentualNode.isIntegralNumber()
                ? percentualNode.intValue()
                : -1;
        String justificativa = justificativaNode != null && justificativaNode.isTextual()
                ? justificativaNode.asText()
                : "";

        if (candidatoId == null || percentual < 0 || percentual > 100) {
            log.warn("Match invalido ignorado: candidatoId={}, percentual={}", candidatoId, percentual);
            return;
        }

        try {
            UUID id = UUID.fromString(candidatoId);
            resultados.put(id, new ResultadoCompatibilidade(
                    id,
                    percentual,
                    justificativa,
                    List.of(),
                    OrigemCompatibilidade.LLM));
        } catch (IllegalArgumentException exception) {
            log.warn("Match com candidatoId invalido ignorado: candidatoId={}", candidatoId);
        }
    }
}
