package com.mentormatch.mapper;

import com.elo.porta.MapperResposta;
import com.mentormatch.dto.response.SessaoMentoriaResponseDTO;
import com.mentormatch.model.entity.SessaoMentoria;
import org.springframework.stereotype.Component;

@Component
public class SessaoMentoriaMapper implements MapperResposta<SessaoMentoria, SessaoMentoriaResponseDTO> {
    @Override public SessaoMentoriaResponseDTO paraResposta(SessaoMentoria s) {
        return new SessaoMentoriaResponseDTO(s.getId(), s.getTitulo(), s.getDescricao(), s.getArea(),
                s.getNivelAtendido(), s.getModalidade(), s.getPeriodo(), s.getCapacidade(), s.getStatus(),
                s.getDataPublicacao(), s.getPublicadorId(), s.getPublicador().getNome());
    }
}
