package com.mentormatch.dto.request;

import java.util.UUID;

public record CriarSolicitacaoMentoriaRequestDTO(UUID sessaoId, UUID alunoId, String mensagem) { }
