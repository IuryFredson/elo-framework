package com.mentormatch.integration.llm;

import com.elo.compatibilidade.llm.groq.AbstractGroqChatClient;
import com.mentormatch.config.GroqProperties;
import com.mentormatch.exception.GroqIntegracaoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

@Component
public class GroqClient extends AbstractGroqChatClient {
    public GroqClient(RestClient client, GroqProperties properties) { super(client, properties); }
    @Override protected RuntimeException erroChaveAusente() { return new GroqIntegracaoException("Chave da Groq não configurada (GROQ_API_KEY)."); }
    @Override protected RuntimeException erroSemChoices() { return new GroqIntegracaoException("Resposta da Groq sem choices."); }
    @Override protected RuntimeException erroSemConteudo() { return new GroqIntegracaoException("Resposta da Groq sem conteúdo."); }
    @Override protected RuntimeException erroHttp(RestClientResponseException e) { return new GroqIntegracaoException("Falha na chamada à Groq (HTTP " + e.getStatusCode().value() + ").", e); }
    @Override protected RuntimeException erroRede(ResourceAccessException e) { return new GroqIntegracaoException("Falha de rede ao chamar Groq.", e); }
}
