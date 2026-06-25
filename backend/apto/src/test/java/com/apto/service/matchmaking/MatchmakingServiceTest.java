package com.apto.service.matchmaking;

import com.apto.dto.response.MatchmakingResponseDTO;
import com.apto.exception.GroqIntegracaoException;
import com.apto.integration.llm.GroqClient;
import com.apto.mapper.MatchmakingMapper;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.Genero;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchmakingServiceTest {

    @Mock
    private UsuarioUniversitarioRepository repository;

    @Mock
    private GroqClient groqClient;

    @Mock
    private CompatibilidadeDeterministicaCalculator compatibilidadeDeterministicaCalculator;

    @Mock
    private MatchmakingPromptBuilder promptBuilder;

    @Mock
    private MatchmakingLlmParser parser;

    @Spy
    private MatchmakingMapper matchmakingMapper = new MatchmakingMapper();

    @InjectMocks
    private MatchmakingService matchmakingService;

    @Test
    void deveUsarContratoDeCompatibilidadeNoFallbackDeterministico() {
        UUID solicitanteId = UUID.randomUUID();
        UsuarioUniversitario solicitante = usuario(solicitanteId, "Ana", Genero.FEMININO);
        UsuarioUniversitario candidato = usuario(UUID.randomUUID(), "Bruno", Genero.MASCULINO);
        com.elo.compatibilidade.ResultadoCompatibilidade compatibilidade = new com.elo.compatibilidade.ResultadoCompatibilidade(
                87,
                "Compatibilidade calculada por criterios do Apto.",
                List.of()
        );

        when(repository.findById(solicitanteId)).thenReturn(Optional.of(solicitante));
        when(repository.buscarCandidatosMatchmaking(solicitanteId)).thenReturn(List.of(candidato));
        when(compatibilidadeDeterministicaCalculator.preferenciaGeneroCompativel(solicitante, candidato)).thenReturn(true);
        when(promptBuilder.montarSystemPrompt()).thenReturn("system");
        when(promptBuilder.montarUserPrompt(solicitante, List.of(candidato))).thenReturn("user");
        when(groqClient.completarChat(anyString(), anyString(), eq(true)))
                .thenThrow(new GroqIntegracaoException("LLM indisponivel"));
        when(compatibilidadeDeterministicaCalculator.calcular(
                solicitante.getPerfilConvivencia(),
                candidato.getPerfilConvivencia()
        )).thenReturn(compatibilidade);

        MatchmakingResponseDTO response = matchmakingService.buscarColegasCompativeis(solicitanteId, 10);

        assertEquals(solicitanteId, response.solicitanteId());
        assertEquals(1, response.total());
        assertEquals(candidato.getId(), response.candidatos().getFirst().id());
        assertEquals(87, response.candidatos().getFirst().percentualCompatibilidade());
        assertEquals(OrigemCompatibilidade.FALLBACK_DETERMINISTICO, response.candidatos().getFirst().origem());
        verify(compatibilidadeDeterministicaCalculator).calcular(
                solicitante.getPerfilConvivencia(),
                candidato.getPerfilConvivencia()
        );
    }

    private UsuarioUniversitario usuario(UUID id, String nome, Genero genero) {
        UsuarioUniversitario usuario = new UsuarioUniversitario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setCurso("Computacao");
        usuario.setGenero(genero);
        usuario.setPerfilConvivencia(new PerfilConvivencia());
        return usuario;
    }
}
