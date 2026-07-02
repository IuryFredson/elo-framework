package com.studybuddy.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarManifestacaoInteresseGrupoRequestDTO(
        @NotNull UUID grupoId,
        @NotNull UUID interessadoId,
        @Size(max = 500) String mensagem
) {
}
