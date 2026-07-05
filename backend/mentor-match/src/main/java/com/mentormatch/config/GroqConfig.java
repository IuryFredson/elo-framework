package com.mentormatch.config;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class GroqConfig {
    @Bean
    public RestClient groqRestClient(GroqProperties properties) {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(properties.timeout()).withReadTimeout(properties.timeout());
        ClientHttpRequestFactory factory = ClientHttpRequestFactoryBuilder.detect().build(settings);
        RestClient.Builder builder = RestClient.builder().baseUrl(properties.baseUrl()).requestFactory(factory)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (StringUtils.hasText(properties.apiKey())) builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.apiKey());
        return builder.build();
    }
}
