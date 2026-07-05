package com.mentormatch.dto.response;

import com.mentormatch.model.enums.*;
import java.util.Set;
import java.util.UUID;

public record PerfilMentoriaResponseDTO(
        UUID participanteId, String nome, String tipo, Set<String> areas, Set<String> objetivos,
        NivelConhecimento nivelConhecimento, Set<ModalidadeMentoria> modalidades,
        Set<PeriodoDisponibilidade> disponibilidades, Set<String> idiomas, String descricao) { }
