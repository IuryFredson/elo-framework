package com.apto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("apto.groq")
public record GroqProperties(
        String baseUrl,
        String model,
        String apiKey,
        Duration timeout
) {
    public GroqProperties {
        if (timeout == null) {
            timeout = Duration.ofSeconds(30);
        }
    }
}
