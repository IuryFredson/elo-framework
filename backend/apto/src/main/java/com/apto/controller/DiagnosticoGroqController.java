package com.apto.controller;

import com.apto.integration.llm.GroqClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/diagnostico")
public class DiagnosticoGroqController {

    private final GroqClient groqClient;

    public DiagnosticoGroqController(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @GetMapping("/groq")
    public ResponseEntity<String> pingGroq() {
        String resposta = groqClient.completarChat(
                "Voce e um assistente breve.",
                "Diga ola.",
                false
        );
        return ResponseEntity.ok(resposta);
    }
}
