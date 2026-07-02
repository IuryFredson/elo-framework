package com.apto.service.matchmaking;

import com.apto.integration.llm.GroqClient;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.elo.compatibilidade.ProvedorCompatibilidadeLlm;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AptoCompatibilidadeLlmProvider implements ProvedorCompatibilidadeLlm<UsuarioUniversitario, PerfilConvivencia> {

    private final GroqClient groqClient;
    private final MatchmakingPromptBuilder promptBuilder;
    private final MatchmakingLlmParser parser;

    @Override
    public Map<UUID, ResultadoCompatibilidade> calcular(
            UsuarioUniversitario solicitante,
            List<UsuarioUniversitario> candidatos
    ) {
        String system = promptBuilder.montarSystemPrompt();
        String user = promptBuilder.montarUserPrompt(solicitante, candidatos);
        String conteudoLlm = groqClient.completarChat(system, user, true);
        return parser.parse(conteudoLlm);
    }
}
