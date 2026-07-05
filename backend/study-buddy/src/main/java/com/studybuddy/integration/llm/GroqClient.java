package com.studybuddy.integration.llm;

import com.elo.compatibilidade.llm.groq.AbstractGroqChatClient;
import com.studybuddy.config.GroqProperties;
import com.studybuddy.exception.GroqIntegracaoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GroqClient extends AbstractGroqChatClient {

    public GroqClient(RestClient groqRestClient, GroqProperties properties) {
        super(groqRestClient, properties);
    }

    @Override
    protected RuntimeException erroChaveAusente() {
        return new GroqIntegracaoException("Chave da Groq nao configurada (GROQ_API_KEY).");
    }

    @Override
    protected RuntimeException erroSemChoices() {
        return new GroqIntegracaoException("Resposta da Groq sem choices.");
    }

    @Override
    protected RuntimeException erroSemConteudo() {
        return new GroqIntegracaoException("Resposta da Groq sem conteudo.");
    }

    @Override
    protected RuntimeException erroHttp(RestClientResponseException exception) {
        return new GroqIntegracaoException(
                "Falha na chamada a Groq (HTTP " + exception.getStatusCode().value() + ").",
                exception
        );
    }

    @Override
    protected RuntimeException erroRede(ResourceAccessException exception) {
        return new GroqIntegracaoException("Falha de rede ao chamar Groq.", exception);
    }
}
