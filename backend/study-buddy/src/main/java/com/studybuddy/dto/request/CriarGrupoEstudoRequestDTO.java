package com.studybuddy.dto.request;

import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CriarGrupoEstudoRequestDTO(
        @NotNull UUID publicadorId,
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String disciplina,
        @Positive int quantidadeVagas,
        @NotNull ModalidadeEstudo modalidade,
        @NotNull PeriodoDisponibilidade periodo
) {
}
