package com.studybuddy.service.matching;

import com.elo.compatibilidade.ProvedorCompatibilidadeLlm;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.integration.llm.GroqClient;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudyBuddyCompatibilidadeLlmProvider implements ProvedorCompatibilidadeLlm<Estudante, PerfilAcademico> {

    private final GroqClient groqClient;
    private final StudyBuddyMatchingPromptBuilder promptBuilder;
    private final StudyBuddyMatchingLlmParser parser;

    @Override
    public Map<UUID, ResultadoCompatibilidade> calcular(Estudante solicitante, List<Estudante> candidatos) {
        String systemPrompt = promptBuilder.montarSystemPrompt();
        String userPrompt = promptBuilder.montarUserPrompt(solicitante, candidatos);
        String resposta = groqClient.completarChat(systemPrompt, userPrompt, true);
        return parser.parse(resposta);
    }
}
