package com.studybuddy.dto.response;

import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import com.studybuddy.model.enums.StatusGrupoEstudo;

import java.time.LocalDate;
import java.util.UUID;

public record GrupoEstudoResponseDTO(
        UUID id,
        String titulo,
        String descricao,
        String disciplina,
        UUID publicadorId,
        String publicadorNome,
        int quantidadeVagas,
        ModalidadeEstudo modalidade,
        PeriodoDisponibilidade periodo,
        StatusGrupoEstudo status,
        LocalDate dataPublicacao
) {
}
