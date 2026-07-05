package com.mentormatch.dto.request;

import com.mentormatch.model.enums.ModalidadeMentoria;
import com.mentormatch.model.enums.NivelConhecimento;
import com.mentormatch.model.enums.PeriodoDisponibilidade;

public record AtualizarSessaoMentoriaRequestDTO(
        String titulo, String descricao, String area, NivelConhecimento nivelAtendido,
        ModalidadeMentoria modalidade, PeriodoDisponibilidade periodo, int capacidade) { }
