package com.studybuddy.dto.response;

import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;

import java.util.Set;
import java.util.UUID;

public record PerfilAcademicoResponseDTO(
        UUID estudanteId,
        String nome,
        String email,
        String matricula,
        String instituicao,
        String curso,
        Set<String> disciplinasInteresse,
        Set<PeriodoDisponibilidade> disponibilidade,
        ObjetivoEstudo objetivoEstudo,
        NivelConhecimento nivelConhecimento,
        ModalidadeEstudo modalidadePreferida,
        String descricao
) {
}
