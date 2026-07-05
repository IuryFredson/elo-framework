package com.mentormatch.dto.response;

import com.elo.denuncia.StatusDenuncia;
import com.mentormatch.model.enums.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record ModeracaoSessaoResponseDTO(
        UUID denunciaId, UUID sessaoId, StatusDenuncia statusDenunciaAnterior,
        StatusDenuncia statusDenunciaAtual, AcaoModeracaoSessao acaoAplicada,
        StatusSessaoMentoria statusSessaoAnterior, StatusSessaoMentoria statusSessaoAtual,
        String justificativa, LocalDateTime moderadoEm) { }
