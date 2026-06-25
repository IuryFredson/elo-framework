package com.apto.service.matchmaking;

import com.apto.dto.response.MatchColegaResponseDTO;
import com.apto.dto.response.MatchmakingResponseDTO;
import com.apto.exception.GroqIntegracaoException;
import com.apto.exception.PerfilConvivenciaAusenteException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.integration.llm.GroqClient;
import com.apto.mapper.MatchmakingMapper;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.UsuarioUniversitarioRepository;
import com.elo.compatibilidade.CompatibilidadeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final UsuarioUniversitarioRepository repository;
    private final GroqClient groqClient;
    private final CompatibilidadeDeterministicaCalculator compatibilidadeDeterministicaCalculator;
    private final MatchmakingPromptBuilder promptBuilder;
    private final MatchmakingLlmParser parser;
    private final MatchmakingMapper matchmakingMapper;

    public MatchmakingResponseDTO buscarColegasCompativeis(UUID solicitanteId, int topN) {

        UsuarioUniversitario solicitante = repository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "UsuÃ¡rio nÃ£o encontrado: " + solicitanteId));

        if (solicitante.getPerfilConvivencia() == null) {
            throw new PerfilConvivenciaAusenteException(
                    "O solicitante nÃ£o possui PerfilConvivencia cadastrado.");
        }

        List<UsuarioUniversitario> candidatos = repository
                .buscarCandidatosMatchmaking(solicitanteId)
                .stream()
                .filter(c -> compatibilidadeDeterministicaCalculator.preferenciaGeneroCompativel(solicitante, c))
                .toList();

        if (candidatos.isEmpty()) {
            return new MatchmakingResponseDTO(solicitanteId, 0, List.of());
        }

        List<MatchColegaResponseDTO> resultados;
        try {
            String system = promptBuilder.montarSystemPrompt();
            String user = promptBuilder.montarUserPrompt(solicitante, candidatos);

            String conteudoLlm = groqClient.completarChat(system, user, true);
            Map<UUID, ResultadoCompatibilidade> resultadosPorId = parser.parse(conteudoLlm);

            resultados = calcularResultadosComFluxoFixo(solicitante, candidatos, resultadosPorId, topN);
            log.info("Matchmaking via LLM concluÃ­do: solicitante={}, candidatos={}", solicitanteId, candidatos.size());

        } catch (GroqIntegracaoException e) {
            log.warn("Groq indisponÃ­vel â€” usando fallback determinÃ­stico. solicitante={}", solicitanteId);
            resultados = calcularResultadosComFluxoFixo(solicitante, candidatos, Map.of(), topN);
        }

        return new MatchmakingResponseDTO(solicitanteId, resultados.size(), resultados);
    }

    private List<MatchColegaResponseDTO> calcularResultadosComFluxoFixo(
            UsuarioUniversitario solicitante,
            List<UsuarioUniversitario> candidatos,
            Map<UUID, ResultadoCompatibilidade> resultadosPorId,
            int topN) {

        Map<PerfilConvivencia, UsuarioUniversitario> candidatoPorPerfil = new IdentityHashMap<>();
        List<PerfilConvivencia> perfisCandidatos = candidatos.stream()
                .peek(candidato -> candidatoPorPerfil.put(candidato.getPerfilConvivencia(), candidato))
                .map(UsuarioUniversitario::getPerfilConvivencia)
                .toList();

        StrategyAptoPorChamada strategy = new StrategyAptoPorChamada(candidatoPorPerfil, resultadosPorId);
        CompatibilidadeService<PerfilConvivencia> compatibilidadeService = new CompatibilidadeService<>(strategy);
        BiPredicate<PerfilConvivencia, PerfilConvivencia> elegivel = (perfilSolicitante, perfilCandidato) -> {
            UsuarioUniversitario candidato = candidatoPorPerfil.get(perfilCandidato);
            return compatibilidadeDeterministicaCalculator.preferenciaGeneroCompativel(solicitante, candidato);
        };

        return compatibilidadeService
                .calcularCompatibilidades(solicitante.getPerfilConvivencia(), perfisCandidatos, elegivel, topN)
                .stream()
                .map(resultadoFramework -> matchmakingMapper.toColegaResponseDTO(
                        strategy.candidato(resultadoFramework),
                        strategy.resultadoApto(resultadoFramework)
                ))
                .toList();
    }

    private class StrategyAptoPorChamada implements com.elo.compatibilidade.CompatibilidadeStrategy<PerfilConvivencia> {

        private final Map<PerfilConvivencia, UsuarioUniversitario> candidatoPorPerfil;
        private final Map<UUID, ResultadoCompatibilidade> resultadosPorId;
        private final Map<com.elo.compatibilidade.ResultadoCompatibilidade, UsuarioUniversitario> candidatoPorResultado =
                new IdentityHashMap<>();
        private final Map<com.elo.compatibilidade.ResultadoCompatibilidade, ResultadoCompatibilidade> resultadoAptoPorResultado =
                new IdentityHashMap<>();

        private StrategyAptoPorChamada(
                Map<PerfilConvivencia, UsuarioUniversitario> candidatoPorPerfil,
                Map<UUID, ResultadoCompatibilidade> resultadosPorId) {
            this.candidatoPorPerfil = candidatoPorPerfil;
            this.resultadosPorId = resultadosPorId;
        }

        @Override
        public com.elo.compatibilidade.ResultadoCompatibilidade calcular(
                PerfilConvivencia solicitante,
                PerfilConvivencia candidatoPerfil) {
            UsuarioUniversitario candidato = candidatoPorPerfil.get(candidatoPerfil);
            ResultadoCompatibilidade resultado = candidato == null ? null : resultadosPorId.get(candidato.getId());

            if (resultado == null) {
                com.elo.compatibilidade.ResultadoCompatibilidade resultadoFramework =
                        compatibilidadeDeterministicaCalculator.calcular(solicitante, candidatoPerfil);
                resultado = ResultadoCompatibilidade.fromFramework(
                        resultadoFramework,
                        OrigemCompatibilidade.FALLBACK_DETERMINISTICO
                );
                candidatoPorResultado.put(resultadoFramework, candidato);
                resultadoAptoPorResultado.put(resultadoFramework, resultado);
                return resultadoFramework;
            }

            com.elo.compatibilidade.ResultadoCompatibilidade resultadoFramework =
                    new com.elo.compatibilidade.ResultadoCompatibilidade(
                            resultado.percentual(),
                            resultado.justificativa(),
                            List.of()
                    );
            candidatoPorResultado.put(resultadoFramework, candidato);
            resultadoAptoPorResultado.put(resultadoFramework, resultado);
            return resultadoFramework;
        }

        private UsuarioUniversitario candidato(com.elo.compatibilidade.ResultadoCompatibilidade resultado) {
            return candidatoPorResultado.get(resultado);
        }

        private ResultadoCompatibilidade resultadoApto(com.elo.compatibilidade.ResultadoCompatibilidade resultado) {
            return resultadoAptoPorResultado.get(resultado);
        }
    }
}
