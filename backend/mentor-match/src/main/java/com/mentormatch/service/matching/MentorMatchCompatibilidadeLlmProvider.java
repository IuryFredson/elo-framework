package com.mentormatch.service.matching;

import com.elo.compatibilidade.llm.AbstractCompatibilidadeLlmProvider;
import com.mentormatch.integration.llm.GroqClient;
import com.mentormatch.model.entity.ParticipanteMentoria;
import com.mentormatch.model.entity.PerfilMentoria;
import org.springframework.stereotype.Component;

@Component
public class MentorMatchCompatibilidadeLlmProvider extends AbstractCompatibilidadeLlmProvider<ParticipanteMentoria, PerfilMentoria> {
    public MentorMatchCompatibilidadeLlmProvider(GroqClient client, MentorMatchPromptBuilder prompt, MentorMatchLlmParser parser) {
        super(client, prompt, parser);
    }
}
