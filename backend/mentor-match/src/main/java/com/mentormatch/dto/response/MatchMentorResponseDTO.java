package com.mentormatch.dto.response;

import com.elo.compatibilidade.OrigemCompatibilidade;
import com.mentormatch.model.enums.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record MatchMentorResponseDTO(
        UUID id, String nome, Set<String> areas, Set<String> objetivos,
        NivelConhecimento nivelConhecimento, Set<ModalidadeMentoria> modalidades,
        Set<PeriodoDisponibilidade> disponibilidades, Set<String> idiomas, String descricao,
        int percentualCompatibilidade, String justificativa, List<String> criteriosAtendidos,
        OrigemCompatibilidade origem) { }
