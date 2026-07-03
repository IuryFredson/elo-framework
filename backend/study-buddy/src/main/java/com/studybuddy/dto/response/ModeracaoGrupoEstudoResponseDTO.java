package com.studybuddy.dto.response;

import com.elo.denuncia.StatusDenuncia;
import com.studybuddy.model.enums.AcaoModeracaoGrupoEstudo;
import com.studybuddy.model.enums.StatusGrupoEstudo;

import java.time.LocalDateTime;
import java.util.UUID;

public record ModeracaoGrupoEstudoResponseDTO(
        UUID denunciaId,
        UUID grupoId,
        StatusDenuncia statusDenunciaAnterior,
        StatusDenuncia statusDenunciaAtual,
        AcaoModeracaoGrupoEstudo acaoGrupoEstudoAplicada,
        StatusGrupoEstudo statusGrupoAnterior,
        StatusGrupoEstudo statusGrupoAtual,
        String justificativa,
        LocalDateTime moderadoEm
) {
}
