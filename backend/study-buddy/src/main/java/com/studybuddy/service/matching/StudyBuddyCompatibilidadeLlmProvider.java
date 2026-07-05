package com.studybuddy.service.matching;

import com.elo.compatibilidade.llm.AbstractCompatibilidadeLlmProvider;
import com.studybuddy.integration.llm.GroqClient;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import org.springframework.stereotype.Component;

@Component
public class StudyBuddyCompatibilidadeLlmProvider extends AbstractCompatibilidadeLlmProvider<Estudante, PerfilAcademico> {

    public StudyBuddyCompatibilidadeLlmProvider(
            GroqClient groqClient,
            StudyBuddyMatchingPromptBuilder promptBuilder,
            StudyBuddyMatchingLlmParser parser) {
        super(groqClient, promptBuilder, parser);
    }
}
