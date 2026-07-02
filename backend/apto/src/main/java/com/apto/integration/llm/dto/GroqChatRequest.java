package com.apto.integration.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GroqChatRequest(
        String model,
        List<GroqChatMessage> messages,
        Double temperature,
        @JsonProperty("response_format") Map<String, Object> responseFormat
) {
}
