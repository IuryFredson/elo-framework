package com.studybuddy.service.matching;

import com.elo.compatibilidade.OrigemCompatibilidade;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.exception.GroqIntegracaoException;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudyBuddyMatchingLlmParserTest {

    private final StudyBuddyMatchingLlmParser parser =
            new StudyBuddyMatchingLlmParser(JsonMapper.builder().build());

    @Test
    void deveConverterMatchesValidosEIgnorarItensInvalidos() {
        UUID valido = UUID.randomUUID();
        String resposta = """
                {"matches":[
                  {"candidatoId":"%s","percentual":88,"justificativa":"Boa compatibilidade."},
                  {"candidatoId":"id-invalido","percentual":70,"justificativa":"Invalido."},
                  {"candidatoId":"%s","percentual":101,"justificativa":"Fora da faixa."}
                ]}
                """.formatted(valido, UUID.randomUUID());

        Map<UUID, ResultadoCompatibilidade> resultados = parser.parse(resposta);

        assertEquals(1, resultados.size());
        assertEquals(88, resultados.get(valido).percentual());
        assertEquals("Boa compatibilidade.", resultados.get(valido).justificativa());
        assertEquals(OrigemCompatibilidade.LLM, resultados.get(valido).origem());
        assertTrue(resultados.get(valido).criteriosAtendidos().isEmpty());
    }

    @Test
    void deveFalharQuandoEstruturaOuJsonSaoInvalidos() {
        assertThrows(GroqIntegracaoException.class, () -> parser.parse("{\"resultado\":[]}"));
        assertThrows(GroqIntegracaoException.class, () -> parser.parse("json-invalido"));
    }
}
