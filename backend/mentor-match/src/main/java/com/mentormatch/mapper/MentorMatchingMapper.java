package com.mentormatch.mapper;

import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.mentormatch.dto.response.MatchMentorResponseDTO;
import com.mentormatch.model.entity.*;
import org.springframework.stereotype.Component;

@Component
public class MentorMatchingMapper {
    public MatchMentorResponseDTO paraResposta(ParticipanteMentoria candidato, ResultadoCompatibilidade resultado) {
        PerfilMentoria p = candidato.getPerfilMentoria();
        return new MatchMentorResponseDTO(candidato.getId(), candidato.getNome(), p.getAreas(), p.getObjetivos(),
                p.getNivelConhecimento(), p.getModalidades(), p.getDisponibilidades(), p.getIdiomas(), p.getDescricao(),
                resultado.percentual(), resultado.justificativa(), resultado.criteriosAtendidos(), resultado.origem());
    }
}
