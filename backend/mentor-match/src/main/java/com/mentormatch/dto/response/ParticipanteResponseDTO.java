package com.mentormatch.dto.response;

import java.util.UUID;

public record ParticipanteResponseDTO(UUID id, String nome, String email, String telefone, boolean ativo, String tipo) { }
