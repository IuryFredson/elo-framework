package com.elo.compatibilidade.llm;

public interface ChatLlmClient {

    String completarChat(String systemPrompt, String userPrompt, boolean respostaJson);
}
