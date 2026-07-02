package com.elo.compatibilidade;

import com.elo.perfil.Perfil;
import com.elo.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchingServiceTest {

    @Test
    void deveUsarLlmEFallbackDeterministicoOrdenandoELimitandoResultados() {
        UUID solicitanteId = UUID.randomUUID();
        FakeUsuario solicitante = new FakeUsuario(solicitanteId, new FakePerfil(0));
        FakeUsuario primeiro = new FakeUsuario(UUID.randomUUID(), new FakePerfil(40));
        FakeUsuario inelegivel = new FakeUsuario(UUID.randomUUID(), new FakePerfil(100));
        FakeUsuario melhor = new FakeUsuario(UUID.randomUUID(), new FakePerfil(90));
        FakeStrategy strategy = new FakeStrategy();
        FakeLlmProvider llmProvider = new FakeLlmProvider();
        llmProvider.resultados = Map.of(
                primeiro.getId(), new ResultadoCompatibilidade(
                        primeiro.getId(),
                        65,
                        "resultado por llm",
                        List.of(),
                        OrigemCompatibilidade.LLM
                )
        );
        FakeMatchingService service = new FakeMatchingService(
                solicitante,
                List.of(primeiro, inelegivel, melhor),
                strategy,
                llmProvider
        );

        List<ResultadoMatching<FakeUsuario>> resultados = service.calcularMatches(solicitanteId, 2);

        assertEquals(List.of("90"), strategy.candidatosCalculados());
        assertEquals(2, resultados.size());
        assertEquals(melhor.getId(), resultados.get(0).compatibilidade().candidatoId());
        assertEquals(90, resultados.get(0).compatibilidade().percentual());
        assertEquals(OrigemCompatibilidade.FALLBACK_DETERMINISTICO, resultados.get(0).compatibilidade().origem());
        assertEquals(primeiro.getId(), resultados.get(1).compatibilidade().candidatoId());
        assertEquals(65, resultados.get(1).compatibilidade().percentual());
        assertEquals(OrigemCompatibilidade.LLM, resultados.get(1).compatibilidade().origem());
    }

    private record FakePerfil(int percentual) implements Perfil {

        @Override
        public String tipoPerfil() {
            return "fake";
        }
    }

    private static class FakeUsuario extends Usuario {
        private final FakePerfil perfil;

        private FakeUsuario(UUID id, FakePerfil perfil) {
            setId(id);
            this.perfil = perfil;
        }
    }

    private static class FakeStrategy implements CompatibilidadeStrategy<FakePerfil> {

        private final List<String> candidatosCalculados = new ArrayList<>();

        @Override
        public ResultadoCompatibilidade calcular(FakePerfil solicitante, FakePerfil candidato) {
            candidatosCalculados.add(String.valueOf(candidato.percentual()));
            return new ResultadoCompatibilidade(
                    candidato.percentual(),
                    "fallback " + candidato.percentual(),
                    List.of("criterio-fake")
            );
        }

        List<String> candidatosCalculados() {
            return candidatosCalculados;
        }
    }

    private static class FakeLlmProvider implements ProvedorCompatibilidadeLlm<FakeUsuario, FakePerfil> {
        private Map<UUID, ResultadoCompatibilidade> resultados = Map.of();

        @Override
        public Map<UUID, ResultadoCompatibilidade> calcular(FakeUsuario solicitante, List<FakeUsuario> candidatos) {
            return resultados;
        }
    }

    private static class FakeMatchingService extends MatchingService<FakeUsuario, FakePerfil> {

        private final FakeUsuario solicitante;
        private final List<FakeUsuario> candidatos;

        private FakeMatchingService(
                FakeUsuario solicitante,
                List<FakeUsuario> candidatos,
                FakeStrategy strategy,
                FakeLlmProvider llmProvider
        ) {
            super(strategy, llmProvider);
            this.solicitante = solicitante;
            this.candidatos = candidatos;
        }

        @Override
        protected FakeUsuario buscarSolicitante(UUID solicitanteId) {
            return solicitante;
        }

        @Override
        protected List<FakeUsuario> buscarCandidatos(UUID solicitanteId) {
            return candidatos;
        }

        @Override
        protected FakePerfil perfil(FakeUsuario usuario) {
            return usuario.perfil;
        }

        @Override
        protected boolean elegivel(FakeUsuario solicitante, FakeUsuario candidato) {
            return candidato.perfil.percentual() != 100;
        }

        @Override
        protected void validarPerfilSolicitante(FakeUsuario solicitante) {
        }
    }
}
