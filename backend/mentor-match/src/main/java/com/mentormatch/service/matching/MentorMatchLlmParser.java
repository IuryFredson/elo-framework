package com.mentormatch.service.matching;

import com.elo.compatibilidade.llm.JsonMatchesCompatibilidadeParser;
import com.mentormatch.exception.GroqIntegracaoException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class MentorMatchLlmParser extends JsonMatchesCompatibilidadeParser {
    public MentorMatchLlmParser(ObjectMapper objectMapper) { super(objectMapper); }
    @Override protected RuntimeException erroMatchesAusente() { return new GroqIntegracaoException("Resposta da Groq sem campo 'matches' válido."); }
    @Override protected RuntimeException erroJsonInvalido(Exception e) { return new GroqIntegracaoException("JSON inválido retornado pela Groq.", e); }
    @Override protected boolean ehErroDaInstancia(RuntimeException e) { return e instanceof GroqIntegracaoException; }
}
