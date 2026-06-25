package com.apto.integration.llm;

import com.apto.config.GroqProperties;
import com.apto.exception.GroqIntegracaoException;
import com.apto.integration.llm.dto.GroqChatMessage;
import com.apto.integration.llm.dto.GroqChatRequest;
import com.apto.integration.llm.dto.GroqChatResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class GroqClient {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final RestClient groqRestClient;
    private final GroqProperties properties;

    public GroqClient(RestClient groqRestClient, GroqProperties properties) {
        this.groqRestClient = groqRestClient;
        this.properties = properties;
    }

    public String completarChat(String systemPrompt, String userPrompt, boolean respostaJson) {
        if (!StringUtils.hasText(properties.apiKey())) {
            throw new GroqIntegracaoException("Chave da Groq nao configurada (GROQ_API_KEY).");
        }

        GroqChatRequest request = new GroqChatRequest(
                properties.model(),
                List.of(
                        new GroqChatMessage("system", systemPrompt),
                        new GroqChatMessage("user", userPrompt)
                ),
                null,
                respostaJson ? Map.of("type", "json_object") : null
        );

        try {
            GroqChatResponse response = groqRestClient.post()
                    .uri(CHAT_COMPLETIONS_PATH)
                    .body(request)
                    .retrieve()
                    .body(GroqChatResponse.class);

            if (response == null || response.choices() == null || response.choices().isEmpty()) {
                throw new GroqIntegracaoException("Resposta da Groq sem choices.");
            }
            GroqChatResponse.Choice choice = response.choices().getFirst();
            if (choice == null || choice.message() == null || choice.message().content() == null) {
                throw new GroqIntegracaoException("Resposta da Groq sem conteudo.");
            }
            return choice.message().content();
        } catch (RestClientResponseException ex) {
            throw new GroqIntegracaoException(
                    "Falha na chamada a Groq (HTTP " + ex.getStatusCode().value() + ").",
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new GroqIntegracaoException("Falha de rede ao chamar Groq.", ex);
        }
    }
}
