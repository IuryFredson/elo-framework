package com.apto.service.matchmaking;

import com.apto.dto.response.MatchColegaResponseDTO;
import com.apto.dto.response.MatchmakingResponseDTO;
import com.apto.exception.PerfilConvivenciaAusenteException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.MatchmakingMapper;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.UsuarioUniversitarioRepository;
import com.elo.compatibilidade.MatchingService;
import com.elo.compatibilidade.ResultadoMatching;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MatchmakingService extends MatchingService<UsuarioUniversitario, PerfilConvivencia> {

    private final UsuarioUniversitarioRepository repository;
    private final CompatibilidadeDeterministicaCalculator compatibilidadeDeterministicaCalculator;
    private final MatchmakingMapper matchmakingMapper;

    public MatchmakingService(
            UsuarioUniversitarioRepository repository,
            CompatibilidadeDeterministicaCalculator compatibilidadeDeterministicaCalculator,
            AptoCompatibilidadeLlmProvider aptoCompatibilidadeLlmProvider,
            MatchmakingMapper matchmakingMapper
    ) {
        super(compatibilidadeDeterministicaCalculator, aptoCompatibilidadeLlmProvider);
        this.repository = repository;
        this.compatibilidadeDeterministicaCalculator = compatibilidadeDeterministicaCalculator;
        this.matchmakingMapper = matchmakingMapper;
    }

    public MatchmakingResponseDTO buscarColegasCompativeis(UUID solicitanteId, int topN) {
        List<MatchColegaResponseDTO> resultados = calcularMatches(solicitanteId, topN).stream()
                .map(this::mapearResultado)
                .toList();

        return new MatchmakingResponseDTO(solicitanteId, resultados.size(), resultados);
    }

    @Override
    protected UsuarioUniversitario buscarSolicitante(UUID solicitanteId) {
        return repository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "UsuÃ¡rio nÃ£o encontrado: " + solicitanteId));
    }

    @Override
    protected List<UsuarioUniversitario> buscarCandidatos(UUID solicitanteId) {
        return repository.buscarCandidatosMatchmaking(solicitanteId);
    }

    @Override
    protected PerfilConvivencia perfil(UsuarioUniversitario usuario) {
        return usuario.getPerfilConvivencia();
    }

    @Override
    protected boolean elegivel(UsuarioUniversitario solicitante, UsuarioUniversitario candidato) {
        return compatibilidadeDeterministicaCalculator.preferenciaGeneroCompativel(solicitante, candidato);
    }

    @Override
    protected void validarPerfilSolicitante(UsuarioUniversitario solicitante) {
        if (solicitante.getPerfilConvivencia() == null) {
            throw new PerfilConvivenciaAusenteException(
                    "O solicitante nÃ£o possui PerfilConvivencia cadastrado.");
        }
    }

    @Override
    protected void aoUsarLlm(UsuarioUniversitario solicitante, List<UsuarioUniversitario> candidatos) {
        log.info("Matchmaking via LLM concluÃ­do: solicitante={}, candidatos={}", solicitante.getId(), candidatos.size());
    }

    @Override
    protected void aoUsarFallback(UsuarioUniversitario solicitante, RuntimeException causa) {
        log.warn("Groq indisponÃ­vel â€” usando fallback determinÃ­stico. solicitante={}", solicitante.getId());
    }

    private MatchColegaResponseDTO mapearResultado(ResultadoMatching<UsuarioUniversitario> resultado) {
        return matchmakingMapper.toColegaResponseDTO(
                resultado.candidato(),
                ResultadoCompatibilidade.fromFramework(resultado.compatibilidade())
        );
    }
}
