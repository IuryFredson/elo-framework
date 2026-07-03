package com.studybuddy.service.matching;

import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.integration.llm.GroqClient;
import com.studybuddy.model.entity.Estudante;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StudyBuddyCompatibilidadeLlmProviderTest {

    @Test
    void deveMontarPromptChamarGroqEInterpretarResposta() {
        GroqClient client = mock(GroqClient.class);
        StudyBuddyMatchingPromptBuilder promptBuilder = mock(StudyBuddyMatchingPromptBuilder.class);
        StudyBuddyMatchingLlmParser parser = mock(StudyBuddyMatchingLlmParser.class);
        StudyBuddyCompatibilidadeLlmProvider provider =
                new StudyBuddyCompatibilidadeLlmProvider(client, promptBuilder, parser);
        Estudante solicitante = new Estudante();
        List<Estudante> candidatos = List.of(new Estudante());
        Map<UUID, ResultadoCompatibilidade> esperado = Map.of();

        when(promptBuilder.montarSystemPrompt()).thenReturn("system");
        when(promptBuilder.montarUserPrompt(solicitante, candidatos)).thenReturn("user");
        when(client.completarChat("system", "user", true)).thenReturn("resposta");
        when(parser.parse("resposta")).thenReturn(esperado);

        assertSame(esperado, provider.calcular(solicitante, candidatos));
        verify(client).completarChat("system", "user", true);
        verify(parser).parse("resposta");
    }
}
