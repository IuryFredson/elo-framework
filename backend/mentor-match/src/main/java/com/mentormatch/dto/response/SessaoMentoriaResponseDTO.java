package com.mentormatch.dto.response;

import com.mentormatch.model.enums.*;
import java.time.LocalDate;
import java.util.UUID;

public record SessaoMentoriaResponseDTO(
        UUID id, String titulo, String descricao, String area, NivelConhecimento nivelAtendido,
        ModalidadeMentoria modalidade, PeriodoDisponibilidade periodo, int capacidade,
        StatusSessaoMentoria status, LocalDate dataPublicacao, UUID mentorId, String mentorNome) { }
