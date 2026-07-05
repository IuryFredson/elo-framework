package com.elo.compatibilidade.llm.groq;

import com.elo.compatibilidade.llm.ChatLlmClient;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

public abstract class AbstractGroqChatClient implements ChatLlmClient {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final RestClient groqRestClient;
    private final GroqClientProperties properties;

    protected AbstractGroqChatClient(RestClient groqRestClient, GroqClientProperties properties) {
        this.groqRestClient = groqRestClient;
        this.properties = properties;
    }

    @Override
    public String completarChat(String systemPrompt, String userPrompt, boolean respostaJson) {
        if (!StringUtils.hasText(properties.apiKey())) {
            throw erroChaveAusente();
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
                throw erroSemChoices();
            }
            GroqChatResponse.Choice choice = response.choices().getFirst();
            if (choice == null || choice.message() == null || choice.message().content() == null) {
                throw erroSemConteudo();
            }
            return choice.message().content();
        } catch (RestClientResponseException exception) {
            throw erroHttp(exception);
        } catch (ResourceAccessException exception) {
            throw erroRede(exception);
        }
    }

    protected abstract RuntimeException erroChaveAusente();

    protected abstract RuntimeException erroSemChoices();

    protected abstract RuntimeException erroSemConteudo();

    protected abstract RuntimeException erroHttp(RestClientResponseException exception);

    protected abstract RuntimeException erroRede(ResourceAccessException exception);
}
