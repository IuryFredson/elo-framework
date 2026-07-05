package com.elo.compatibilidade.llm.groq;

import java.util.List;

public record GroqChatResponse(List<Choice> choices) {

    public record Choice(GroqChatMessage message) {
    }
}
