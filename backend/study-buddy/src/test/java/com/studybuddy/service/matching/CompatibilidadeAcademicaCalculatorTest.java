package com.studybuddy.service.matching;

import com.elo.compatibilidade.ResultadoCompatibilidade;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.model.enums.ModalidadeEstudo;
import com.studybuddy.model.enums.NivelConhecimento;
import com.studybuddy.model.enums.ObjetivoEstudo;
import com.studybuddy.model.enums.PeriodoDisponibilidade;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatibilidadeAcademicaCalculatorTest {

    private final CompatibilidadeAcademicaCalculator calculator = new CompatibilidadeAcademicaCalculator();

    @Test
    void deveCalcularCompatibilidadeAltaComCriteriosIguais() {
        ResultadoCompatibilidade resultado = calculator.calcular(perfil(), perfil());

        assertEquals(100, resultado.percentual());
        assertEquals(5, resultado.criteriosAtendidos().size());
        assertTrue(resultado.criteriosAtendidos().contains("disciplinas de interesse em comum"));
        assertTrue(resultado.criteriosAtendidos().contains("disponibilidade compativel"));
    }

    @Test
    void deveRetornarZeroQuandoPerfilAusente() {
        ResultadoCompatibilidade resultado = calculator.calcular(null, perfil());

        assertEquals(0, resultado.percentual());
        assertTrue(resultado.criteriosAtendidos().isEmpty());
    }

    private PerfilAcademico perfil() {
        PerfilAcademico perfil = new PerfilAcademico();
        perfil.setCurso("Computacao");
        perfil.setDisciplinasInteresse(Set.of("Algoritmos", "Estrutura de Dados"));
        perfil.setDisponibilidade(Set.of(PeriodoDisponibilidade.NOITE));
        perfil.setObjetivoEstudo(ObjetivoEstudo.PROVA);
        perfil.setNivelConhecimento(NivelConhecimento.INTERMEDIARIO);
        perfil.setModalidadePreferida(ModalidadeEstudo.ONLINE);
        perfil.setDescricao("Perfil para testes");
        return perfil;
    }
}
