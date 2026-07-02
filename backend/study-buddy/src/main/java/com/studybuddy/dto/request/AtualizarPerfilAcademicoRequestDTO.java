package com.studybuddy.dto.request;

import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AtualizarPerfilAcademicoRequestDTO(
        @NotBlank String curso,
        @NotEmpty Set<String> disciplinasInteresse,
        @NotEmpty Set<PeriodoDisponibilidade> disponibilidade,
        @NotNull ObjetivoEstudo objetivoEstudo,
        @NotNull NivelConhecimento nivelConhecimento,
        @NotNull ModalidadeEstudo modalidadePreferida,
        String descricao
) {
}
