package com.studybuddy.integration.llm;

import com.studybuddy.config.GroqProperties;
import com.studybuddy.exception.GroqIntegracaoException;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class GroqClientTest {

    @Test
    void deveFalharAntesDaChamadaQuandoChaveNaoEstaConfigurada() {
        RestClient restClient = mock(RestClient.class);
        GroqProperties properties = new GroqProperties(
                "https://api.groq.com/openai/v1",
                "modelo",
                "",
                Duration.ofSeconds(30)
        );
        GroqClient client = new GroqClient(restClient, properties);

        assertThrows(GroqIntegracaoException.class,
                () -> client.completarChat("system", "user", true));
        verifyNoInteractions(restClient);
    }
}
