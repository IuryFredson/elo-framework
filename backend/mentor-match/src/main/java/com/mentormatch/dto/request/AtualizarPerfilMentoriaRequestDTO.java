package com.mentormatch.dto.request;

import com.mentormatch.model.enums.ModalidadeMentoria;
import com.mentormatch.model.enums.NivelConhecimento;
import com.mentormatch.model.enums.PeriodoDisponibilidade;
import java.util.Set;

public record AtualizarPerfilMentoriaRequestDTO(
        Set<String> areas, Set<String> objetivos, NivelConhecimento nivelConhecimento,
        Set<ModalidadeMentoria> modalidades, Set<PeriodoDisponibilidade> disponibilidades,
        Set<String> idiomas, String descricao) { }
