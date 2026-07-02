package com.studybuddy.service.matching;

import com.elo.compatibilidade.ProvedorCompatibilidadeLlm;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class StudyBuddyCompatibilidadeLlmProvider implements ProvedorCompatibilidadeLlm<Estudante, PerfilAcademico> {

    @Override
    public Map<UUID, ResultadoCompatibilidade> calcular(Estudante solicitante, List<Estudante> candidatos) {
        return Map.of();
    }
}
