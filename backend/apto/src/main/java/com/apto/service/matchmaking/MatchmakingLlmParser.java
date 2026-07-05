package com.apto.service.matchmaking;

import com.apto.exception.GroqIntegracaoException;
import com.elo.compatibilidade.llm.JsonMatchesCompatibilidadeParser;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class MatchmakingLlmParser extends JsonMatchesCompatibilidadeParser {

    public MatchmakingLlmParser(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected RuntimeException erroMatchesAusente() {
        return new GroqIntegracaoException("Resposta do Groq sem campo 'matches' valido");
    }

    @Override
    protected RuntimeException erroJsonInvalido(Exception exception) {
        return new GroqIntegracaoException("JSON invalido retornado pelo Groq", exception);
    }

    @Override
    protected boolean ehErroDaInstancia(RuntimeException exception) {
        return exception instanceof GroqIntegracaoException;
    }
}
