package com.mentormatch.service.matching;

import com.elo.compatibilidade.llm.MatchingPromptBuilder;
import com.mentormatch.model.entity.ParticipanteMentoria;
import com.mentormatch.model.entity.PerfilMentoria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.util.*;

@Slf4j @Component @RequiredArgsConstructor
public class MentorMatchPromptBuilder implements MatchingPromptBuilder<ParticipanteMentoria> {
    private final ObjectMapper objectMapper;
    @Override public String montarSystemPrompt() {
        return """
            Você avalia a compatibilidade entre um aluno e mentores.
            Receberá JSON com o perfil do solicitante e uma lista de candidatos identificados por candidatoId.
            Considere áreas, objetivos, nível de conhecimento, modalidades, disponibilidades e idiomas.
            Use a descrição somente como contexto qualitativo e não invente informações.
            Retorne APENAS JSON válido, incluindo todos os candidatos, no formato:
            {"matches":[{"candidatoId":"<uuid>","percentual":<0-100>,"justificativa":"<texto>"}]}
            Ordene por percentual decrescente. Escreva a justificativa em pt-BR, explicando afinidades,
            possíveis limitações e uma orientação prática, sem citar nomes ou dados pessoais.
            Reproduza cada candidatoId exatamente como recebido.
            """;
    }
    @Override public String montarUserPrompt(ParticipanteMentoria solicitante, List<ParticipanteMentoria> candidatos) {
        try {
            Map<String,Object> payload = new LinkedHashMap<>(); payload.put("solicitante", extrair(solicitante, false));
            payload.put("candidatos", candidatos.stream().map(c -> extrair(c, true)).toList());
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) { log.error("Erro ao serializar prompt do Mentor Match", e); throw new RuntimeException("Falha ao construir prompt para LLM", e); }
    }
    private Map<String,Object> extrair(ParticipanteMentoria participante, boolean id) {
        PerfilMentoria p = participante.getPerfilMentoria(); Map<String,Object> campos = new LinkedHashMap<>();
        if (id) campos.put("candidatoId", participante.getId().toString());
        campos.put("areas", p.getAreas()); campos.put("objetivos", p.getObjetivos()); campos.put("nivelConhecimento", p.getNivelConhecimento());
        campos.put("modalidades", p.getModalidades()); campos.put("disponibilidades", p.getDisponibilidades()); campos.put("idiomas", p.getIdiomas());
        if (p.getDescricao() != null && !p.getDescricao().isBlank()) campos.put("descricao", p.getDescricao().strip());
        return campos;
    }
}
