package com.studybuddy.dto.response;

import com.elo.manifestacao.StatusManifestacaoInteresse;

import java.time.LocalDateTime;
import java.util.UUID;

public record ManifestacaoInteresseGrupoResponseDTO(
        UUID id,
        UUID grupoId,
        String grupoTitulo,
        UUID interessadoId,
        String interessadoNome,
        UUID publicadorId,
        String publicadorNome,
        StatusManifestacaoInteresse status,
        String mensagem,
        LocalDateTime dataManifestacao,
        LocalDateTime dataResposta
) {
}
