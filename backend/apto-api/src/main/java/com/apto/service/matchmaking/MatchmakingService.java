package com.apto.service.matchmaking;

import com.apto.dto.response.MatchColegaResponseDTO;
import com.apto.dto.response.MatchmakingResponseDTO;
import com.apto.exception.GroqIntegracaoException;
import com.apto.exception.PerfilConvivenciaAusenteException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.integration.llm.GroqClient;
import com.apto.mapper.MatchmakingMapper;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.UsuarioUniversitarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final UsuarioUniversitarioRepository repository;
    private final GroqClient groqClient;
    private final CompatibilidadeStrategy compatibilidadeStrategy;
    private final MatchmakingPromptBuilder promptBuilder;
    private final MatchmakingLlmParser parser;
    private final MatchmakingMapper matchmakingMapper;

    public MatchmakingResponseDTO buscarColegasCompativeis(UUID solicitanteId, int topN) {

        // 1. Solicitante existe?
        UsuarioUniversitario solicitante = repository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário não encontrado: " + solicitanteId));

        // 2. Solicitante tem perfil?
        if (solicitante.getPerfilConvivencia() == null) {
            throw new PerfilConvivenciaAusenteException(
                    "O solicitante não possui PerfilConvivencia cadastrado.");
        }

        // 3. Buscar candidatos válidos, filtrar por preferência de gênero
        List<UsuarioUniversitario> candidatos = repository
                .buscarCandidatosMatchmaking(solicitanteId)
                .stream()
                .filter(c -> compatibilidadeStrategy.preferenciaGeneroCompativel(solicitante, c))
                .toList();

        // 4. Lista vazia → retornar DTO vazio
        if (candidatos.isEmpty()) {
            return new MatchmakingResponseDTO(solicitanteId, 0, List.of());
        }

        // 5. Tentar caminho feliz com LLM
        List<MatchColegaResponseDTO> resultados;
        try {
            String system = promptBuilder.montarSystemPrompt();
            String user = promptBuilder.montarUserPrompt(solicitante, candidatos);

            String conteudoLlm = groqClient.completarChat(system, user, true);
            Map<UUID, ResultadoCompatibilidade> resultadosPorId = parser.parse(conteudoLlm);

            resultados = montarResultadosComLlm(candidatos, resultadosPorId, solicitante);
            log.info("Matchmaking via LLM concluído: solicitante={}, candidatos={}", solicitanteId, candidatos.size());

        } catch (GroqIntegracaoException e) {
            // 6. Fallback: calcular tudo deterministicamente
            log.warn("Groq indisponível — usando fallback determinístico. solicitante={}", solicitanteId);
            resultados = candidatos.stream()
                    .map(c -> matchmakingMapper.toColegaResponseDTO(c, compatibilidadeStrategy.calcular(solicitante, c)))
                    .toList();
        }

        // 7. Ordenar desc e truncar em topN
        List<MatchColegaResponseDTO> ordenados = resultados.stream()
                .sorted(Comparator.comparingInt(MatchColegaResponseDTO::percentualCompatibilidade).reversed())
                .limit(topN)
                .toList();

        return new MatchmakingResponseDTO(solicitanteId, ordenados.size(), ordenados);
    }

    // Monta resultados mistos: LLM para quem veio no retorno, fallback para os demais
    private List<MatchColegaResponseDTO> montarResultadosComLlm(
            List<UsuarioUniversitario> candidatos,
            Map<UUID, ResultadoCompatibilidade> resultadosPorId,
            UsuarioUniversitario solicitante) {

        List<MatchColegaResponseDTO> lista = new ArrayList<>();
        for (UsuarioUniversitario candidato : candidatos) {
            ResultadoCompatibilidade resultado = resultadosPorId.get(candidato.getId());
            if (resultado == null) {
                // Candidato ausente no retorno da LLM → usar fallback para ele
                resultado = compatibilidadeStrategy.calcular(solicitante, candidato);
            }
            lista.add(matchmakingMapper.toColegaResponseDTO(candidato, resultado));
        }
        return lista;
    }
}
