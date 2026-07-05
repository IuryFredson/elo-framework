package com.mentormatch.mapper;

import com.elo.porta.MapperResposta;
import com.mentormatch.dto.response.ParticipanteResponseDTO;
import com.mentormatch.model.entity.Aluno;
import org.springframework.stereotype.Component;

@Component
public class AlunoMapper implements MapperResposta<Aluno, ParticipanteResponseDTO> {
    @Override public ParticipanteResponseDTO paraResposta(Aluno aluno) {
        return new ParticipanteResponseDTO(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getTelefone(), aluno.isAtivo(), "ALUNO");
    }
}
