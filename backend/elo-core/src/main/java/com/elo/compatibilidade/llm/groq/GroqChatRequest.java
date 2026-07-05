package com.elo.compatibilidade.llm.groq;

import java.util.List;
import java.util.Map;

public record GroqChatRequest(
        String model,
        List<GroqChatMessage> messages,
        Double temperature,
        Map<String, String> responseFormat
) {
}
