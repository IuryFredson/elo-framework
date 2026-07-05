package com.mentormatch.mapper;

import com.mentormatch.dto.response.SolicitacaoMentoriaResponseDTO;
import com.mentormatch.model.entity.SolicitacaoMentoria;
import org.springframework.stereotype.Component;

@Component
public class SolicitacaoMentoriaMapper {
    public SolicitacaoMentoriaResponseDTO paraResposta(SolicitacaoMentoria s) {
        return new SolicitacaoMentoriaResponseDTO(s.getId(), s.getSessao().getId(), s.getSessao().getTitulo(),
                s.getInteressado().getId(), s.getInteressado().getNome(), s.getSessao().getPublicadorId(),
                s.getSessao().getPublicador().getNome(), s.getStatus(), s.getMensagem(),
                s.getDataSolicitacao(), s.getDataResposta());
    }
}
