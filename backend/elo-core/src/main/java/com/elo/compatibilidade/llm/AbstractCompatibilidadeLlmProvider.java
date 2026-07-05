package com.elo.compatibilidade.llm;

import com.elo.compatibilidade.ProvedorCompatibilidadeLlm;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.elo.perfil.Perfil;
import com.elo.usuario.Usuario;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractCompatibilidadeLlmProvider<U extends Usuario, P extends Perfil>
        implements ProvedorCompatibilidadeLlm<U, P> {

    private final ChatLlmClient llmClient;
    private final MatchingPromptBuilder<U> promptBuilder;
    private final CompatibilidadeLlmParser parser;

    protected AbstractCompatibilidadeLlmProvider(
            ChatLlmClient llmClient,
            MatchingPromptBuilder<U> promptBuilder,
            CompatibilidadeLlmParser parser) {
        this.llmClient = llmClient;
        this.promptBuilder = promptBuilder;
        this.parser = parser;
    }

    @Override
    public Map<UUID, ResultadoCompatibilidade> calcular(U solicitante, List<U> candidatos) {
        String systemPrompt = promptBuilder.montarSystemPrompt();
        String userPrompt = promptBuilder.montarUserPrompt(solicitante, candidatos);
        String resposta = llmClient.completarChat(systemPrompt, userPrompt, true);
        return parser.parse(resposta);
    }
}
