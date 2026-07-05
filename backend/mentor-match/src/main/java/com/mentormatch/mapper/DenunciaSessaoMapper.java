package com.mentormatch.mapper;

import com.mentormatch.dto.response.DenunciaSessaoResponseDTO;
import com.mentormatch.model.entity.DenunciaSessaoMentoria;
import com.mentormatch.model.enums.CriterioDenunciaMentorMatch;
import org.springframework.stereotype.Component;

@Component
public class DenunciaSessaoMapper {
    public DenunciaSessaoResponseDTO paraResposta(DenunciaSessaoMentoria d) {
        return new DenunciaSessaoResponseDTO(d.getId(), d.getDenuncianteId(), d.getOfertaId(),
                d.getTitulo(), d.getCorpo(), d.getCriterio() == null ? CriterioDenunciaMentorMatch.OUTRO : d.getCriterio(),
                d.getStatusDenuncia(), d.getCriadoEm());
    }
}
