package com.studybuddy.service.matching;

import com.elo.compatibilidade.OrigemCompatibilidade;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.exception.GroqIntegracaoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyBuddyMatchingLlmParser {

    private final ObjectMapper objectMapper;

    public Map<UUID, ResultadoCompatibilidade> parse(String conteudoJson) {
        try {
            JsonNode raiz = objectMapper.readTree(conteudoJson);
            JsonNode matches = raiz.get("matches");

            if (matches == null || !matches.isArray()) {
                throw new GroqIntegracaoException("Resposta da Groq sem campo 'matches' valido");
            }

            Map<UUID, ResultadoCompatibilidade> resultados = new HashMap<>();
            for (JsonNode match : matches) {
                adicionarSeValido(match, resultados);
            }
            return resultados;
        } catch (GroqIntegracaoException exception) {
            throw exception;
        } catch (Exception exception) {
            log.warn("Falha ao parsear resposta da Groq - ativando fallback. Causa: {}",
                    exception.getMessage());
            throw new GroqIntegracaoException("JSON invalido retornado pela Groq", exception);
        }
    }

    private void adicionarSeValido(
            JsonNode match,
            Map<UUID, ResultadoCompatibilidade> resultados
    ) {
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
                    OrigemCompatibilidade.LLM
            ));
        } catch (IllegalArgumentException exception) {
            log.warn("Match com candidatoId invalido ignorado: candidatoId={}", candidatoId);
        }
    }
}
