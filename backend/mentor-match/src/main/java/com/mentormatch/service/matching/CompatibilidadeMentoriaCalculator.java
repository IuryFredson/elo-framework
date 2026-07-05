package com.mentormatch.service.matching;

import com.elo.compatibilidade.CompatibilidadeStrategy;
import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.mentormatch.model.entity.PerfilMentoria;
import com.mentormatch.model.enums.PeriodoDisponibilidade;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class CompatibilidadeMentoriaCalculator implements CompatibilidadeStrategy<PerfilMentoria> {
    private static final String JUSTIFICATIVA = "Compatibilidade calculada por critérios determinísticos de mentoria.";

    @Override
    public ResultadoCompatibilidade calcular(PerfilMentoria aluno, PerfilMentoria mentor) {
        if (aluno == null || mentor == null) return new ResultadoCompatibilidade(0, JUSTIFICATIVA, List.of());
        int total = 0; List<String> criterios = new ArrayList<>();
        if (intersectaTextos(aluno.getAreas(), mentor.getAreas())) { total += 35; criterios.add("áreas em comum"); }
        if (intersectaTextos(aluno.getObjetivos(), mentor.getObjetivos())) { total += 20; criterios.add("objetivos compatíveis"); }
        if (disponibilidadeCompativel(aluno, mentor)) { total += 20; criterios.add("disponibilidade compatível"); }
        if (intersecta(aluno.getModalidades(), mentor.getModalidades())) { total += 15; criterios.add("modalidade compatível"); }
        if (aluno.getNivelConhecimento() != null && mentor.getNivelConhecimento() != null
                && mentor.getNivelConhecimento().ordinal() >= aluno.getNivelConhecimento().ordinal()) {
            total += 10; criterios.add("nível de conhecimento compatível");
        }
        return new ResultadoCompatibilidade(total, JUSTIFICATIVA, criterios);
    }

    private boolean disponibilidadeCompativel(PerfilMentoria a, PerfilMentoria b) {
        Set<PeriodoDisponibilidade> da = a.getDisponibilidades(), db = b.getDisponibilidades();
        return da != null && db != null && (da.contains(PeriodoDisponibilidade.FLEXIVEL)
                || db.contains(PeriodoDisponibilidade.FLEXIVEL) || intersecta(da, db));
    }
    private boolean intersectaTextos(Set<String> a, Set<String> b) {
        if (a == null || b == null) return false;
        Set<String> normalizados = new HashSet<>();
        a.stream().filter(Objects::nonNull).map(v -> v.strip().toLowerCase(Locale.ROOT)).forEach(normalizados::add);
        return b.stream().filter(Objects::nonNull).map(v -> v.strip().toLowerCase(Locale.ROOT)).anyMatch(normalizados::contains);
    }
    private boolean intersecta(Set<?> a, Set<?> b) {
        if (a == null || b == null) return false;
        return a.stream().anyMatch(b::contains);
    }
}
