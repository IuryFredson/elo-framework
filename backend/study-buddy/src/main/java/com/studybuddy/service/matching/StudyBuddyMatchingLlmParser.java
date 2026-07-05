package com.studybuddy.service.matching;

import com.elo.compatibilidade.llm.JsonMatchesCompatibilidadeParser;
import com.studybuddy.exception.GroqIntegracaoException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class StudyBuddyMatchingLlmParser extends JsonMatchesCompatibilidadeParser {

    public StudyBuddyMatchingLlmParser(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    protected RuntimeException erroMatchesAusente() {
        return new GroqIntegracaoException("Resposta da Groq sem campo 'matches' valido");
    }

    @Override
    protected RuntimeException erroJsonInvalido(Exception exception) {
        return new GroqIntegracaoException("JSON invalido retornado pela Groq", exception);
    }

    @Override
    protected boolean ehErroDaInstancia(RuntimeException exception) {
        return exception instanceof GroqIntegracaoException;
    }
}
