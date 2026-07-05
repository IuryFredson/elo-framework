package com.mentormatch.mapper;

import com.elo.denuncia.StatusDenuncia;
import com.mentormatch.dto.request.ModerarDenunciaSessaoRequestDTO;
import com.mentormatch.dto.response.ModeracaoSessaoResponseDTO;
import com.mentormatch.model.entity.*;
import com.mentormatch.model.enums.StatusSessaoMentoria;
import org.springframework.stereotype.Component;

@Component
public class ModeracaoSessaoMapper {
    public ModeracaoSessaoResponseDTO paraResposta(DenunciaSessaoMentoria denuncia, SessaoMentoria sessao,
            StatusDenuncia statusDenunciaAnterior, StatusSessaoMentoria statusSessaoAnterior,
            ModerarDenunciaSessaoRequestDTO dto) {
        return new ModeracaoSessaoResponseDTO(denuncia.getId(), sessao.getId(), statusDenunciaAnterior,
                denuncia.getStatusDenuncia(), dto.acaoSessao(), statusSessaoAnterior, sessao.getStatus(),
                dto.justificativa(), denuncia.getStatusAtualizadoEm());
    }
}
