package com.apto.service.matchmaking;

import com.apto.exception.GroqIntegracaoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchmakingLlmParser {

    private final ObjectMapper objectMapper;
    public Map<UUID, ResultadoCompatibilidade> parse(String conteudoJson) {
        try {
            JsonNode raiz = objectMapper.readTree(conteudoJson);
            JsonNode matches = raiz.get("matches");

            if (matches == null || !matches.isArray()) {
                throw new GroqIntegracaoException("Resposta do Groq sem campo 'matches' válido");
            }

            Map<UUID, ResultadoCompatibilidade> resultado = new HashMap<>();

            for (JsonNode match : matches) {
                String idStr = match.path("candidatoId").asText(null);
                int percentual = match.path("percentual").asInt(-1);
                String justificativa = match.path("justificativa").asText("");

                if (idStr == null || percentual < 0 || percentual > 100) {
                    log.warn("Match inválido ignorado: candidatoId={}, percentual={}", idStr, percentual);
                    continue;
                }

                UUID id = UUID.fromString(idStr);
                resultado.put(id, new ResultadoCompatibilidade(
                        percentual,
                        justificativa,
                        OrigemCompatibilidade.LLM
                ));
            }
            log.info("Resposta bruta do Grok: {}", conteudoJson);
            return resultado;

        } catch (GroqIntegracaoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Falha ao parsear resposta do Groq — ativando fallback. Causa: {}", e.getMessage());
            throw new GroqIntegracaoException("JSON inválido retornado pelo Groq", e);
        }
    }
}