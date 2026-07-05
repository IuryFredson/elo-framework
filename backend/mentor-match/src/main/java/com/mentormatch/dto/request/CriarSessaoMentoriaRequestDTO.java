package com.mentormatch.dto.request;

import com.mentormatch.model.enums.ModalidadeMentoria;
import com.mentormatch.model.enums.NivelConhecimento;
import com.mentormatch.model.enums.PeriodoDisponibilidade;
import java.util.UUID;

public record CriarSessaoMentoriaRequestDTO(
        String titulo, String descricao, String area, NivelConhecimento nivelAtendido,
        ModalidadeMentoria modalidade, PeriodoDisponibilidade periodo, int capacidade,
        UUID mentorId) { }
