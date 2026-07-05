package com.studybuddy.config;

import com.elo.compatibilidade.llm.groq.GroqClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("study-buddy.groq")
public record GroqProperties(
        String baseUrl,
        String model,
        String apiKey,
        Duration timeout
) implements GroqClientProperties {
    public GroqProperties {
        if (timeout == null) {
            timeout = Duration.ofSeconds(30);
        }
    }
}
