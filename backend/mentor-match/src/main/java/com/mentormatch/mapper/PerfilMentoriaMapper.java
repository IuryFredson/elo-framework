package com.mentormatch.mapper;

import com.elo.porta.MapperResposta;
import com.mentormatch.dto.response.PerfilMentoriaResponseDTO;
import com.mentormatch.model.entity.*;
import org.springframework.stereotype.Component;

@Component
public class PerfilMentoriaMapper implements MapperResposta<ParticipanteMentoria, PerfilMentoriaResponseDTO> {
    @Override public PerfilMentoriaResponseDTO paraResposta(ParticipanteMentoria participante) {
        PerfilMentoria p = participante.getPerfilMentoria();
        return new PerfilMentoriaResponseDTO(
                participante.getId(), participante.getNome(), participante instanceof Mentor ? "MENTOR" : "ALUNO",
                p == null ? null : p.getAreas(), p == null ? null : p.getObjetivos(),
                p == null ? null : p.getNivelConhecimento(), p == null ? null : p.getModalidades(),
                p == null ? null : p.getDisponibilidades(), p == null ? null : p.getIdiomas(),
                p == null ? null : p.getDescricao());
    }
}
