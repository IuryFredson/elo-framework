package com.studybuddy.mapper;

import com.elo.porta.MapperResposta;
import com.studybuddy.dto.response.EstudanteResponseDTO;
import com.studybuddy.model.entity.Estudante;
import org.springframework.stereotype.Component;

@Component
public class EstudanteMapper implements MapperResposta<Estudante, EstudanteResponseDTO> {

    @Override
    public EstudanteResponseDTO paraResposta(Estudante estudante) {
        return toResponseDTO(estudante);
    }

    public EstudanteResponseDTO toResponseDTO(Estudante estudante) {
        return new EstudanteResponseDTO(
                estudante.getId(),
                estudante.getNome(),
                estudante.getEmail(),
                estudante.getTelefone(),
                estudante.isAtivo(),
                estudante.getMatricula(),
                estudante.getInstituicao()
        );
    }
}
