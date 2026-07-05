package com.apto.service.matchmaking;

import com.apto.integration.llm.GroqClient;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.elo.compatibilidade.llm.AbstractCompatibilidadeLlmProvider;
import org.springframework.stereotype.Component;

@Component
public class AptoCompatibilidadeLlmProvider extends AbstractCompatibilidadeLlmProvider<UsuarioUniversitario, PerfilConvivencia> {

    public AptoCompatibilidadeLlmProvider(
            GroqClient groqClient,
            MatchmakingPromptBuilder promptBuilder,
            MatchmakingLlmParser parser) {
        super(groqClient, promptBuilder, parser);
    }
}
