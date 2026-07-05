package com.mentormatch.mapper;

import com.elo.porta.MapperResposta;
import com.mentormatch.dto.response.ParticipanteResponseDTO;
import com.mentormatch.model.entity.Mentor;
import org.springframework.stereotype.Component;

@Component
public class MentorMapper implements MapperResposta<Mentor, ParticipanteResponseDTO> {
    @Override public ParticipanteResponseDTO paraResposta(Mentor mentor) {
        return new ParticipanteResponseDTO(mentor.getId(), mentor.getNome(), mentor.getEmail(), mentor.getTelefone(), mentor.isAtivo(), "MENTOR");
    }
}
