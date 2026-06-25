package com.apto.mapper;

import com.apto.dto.response.MatchColegaResponseDTO;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.service.matchmaking.ResultadoCompatibilidade;
import org.springframework.stereotype.Component;

@Component
public class MatchmakingMapper {

    public MatchColegaResponseDTO toColegaResponseDTO(UsuarioUniversitario candidato, ResultadoCompatibilidade resultado) {
        return new MatchColegaResponseDTO(
                candidato.getId(),
                candidato.getNome(),
                candidato.getCurso(),
                candidato.getGenero(),
                resultado.percentual(),
                resultado.justificativa(),
                resultado.origem()
        );
    }
}
