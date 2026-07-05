package com.mentormatch.dto.response;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import java.time.LocalDateTime;
import java.util.UUID;

public record SolicitacaoMentoriaResponseDTO(
        UUID id, UUID sessaoId, String sessaoTitulo, UUID alunoId, String alunoNome,
        UUID mentorId, String mentorNome, StatusManifestacaoInteresse status,
        String mensagem, LocalDateTime dataSolicitacao, LocalDateTime dataResposta) { }
