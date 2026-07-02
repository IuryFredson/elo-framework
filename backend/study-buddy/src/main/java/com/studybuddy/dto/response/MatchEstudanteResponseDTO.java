package com.studybuddy.dto.response;

import com.elo.compatibilidade.OrigemCompatibilidade;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record MatchEstudanteResponseDTO(
        UUID id,
        String nome,
        String curso,
        String instituicao,
        Set<String> disciplinasInteresse,
        ObjetivoEstudo objetivoEstudo,
        NivelConhecimento nivelConhecimento,
        ModalidadeEstudo modalidadePreferida,
        int percentualCompatibilidade,
        String justificativa,
        List<String> criteriosAtendidos,
        OrigemCompatibilidade origem
) {
}
