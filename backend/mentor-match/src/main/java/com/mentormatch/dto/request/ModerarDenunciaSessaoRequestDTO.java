package com.mentormatch.dto.request;

import com.elo.denuncia.StatusDenuncia;
import com.mentormatch.model.enums.AcaoModeracaoSessao;

public record ModerarDenunciaSessaoRequestDTO(
        StatusDenuncia novoStatus, AcaoModeracaoSessao acaoSessao, String justificativa) { }
