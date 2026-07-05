package com.elo.compatibilidade.llm;

import java.util.List;

public interface MatchingPromptBuilder<U> {

    String montarSystemPrompt();

    String montarUserPrompt(U solicitante, List<U> candidatos);
}
