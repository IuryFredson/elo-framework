package com.studybuddy.service.matching;

import com.elo.compatibilidade.MatchingService;
import com.elo.compatibilidade.ResultadoMatching;
import com.studybuddy.dto.response.MatchEstudanteResponseDTO;
import com.studybuddy.dto.response.StudyBuddyMatchingResponseDTO;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.PerfilAcademicoAusenteException;
import com.studybuddy.mapper.StudyBuddyMatchingMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.repository.EstudanteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudyBuddyMatchingService extends MatchingService<Estudante, PerfilAcademico> {

    private final EstudanteRepository estudanteRepository;
    private final StudyBuddyMatchingMapper matchingMapper;

    public StudyBuddyMatchingService(
            EstudanteRepository estudanteRepository,
            CompatibilidadeAcademicaCalculator compatibilidadeAcademicaCalculator,
            StudyBuddyCompatibilidadeLlmProvider compatibilidadeLlmProvider,
            StudyBuddyMatchingMapper matchingMapper) {
        super(compatibilidadeAcademicaCalculator, compatibilidadeLlmProvider);
        this.estudanteRepository = estudanteRepository;
        this.matchingMapper = matchingMapper;
    }

    public StudyBuddyMatchingResponseDTO buscarEstudantesCompativeis(UUID estudanteId, int topN) {
        List<MatchEstudanteResponseDTO> candidatos = calcularMatches(estudanteId, topN).stream()
                .map(this::mapearResultado)
                .toList();

        return new StudyBuddyMatchingResponseDTO(estudanteId, candidatos.size(), candidatos);
    }

    @Override
    protected Estudante buscarSolicitante(UUID solicitanteId) {
        return estudanteRepository.findById(solicitanteId)
                .orElseThrow(() -> new EstudanteNaoEncontradoException(
                        "Estudante nao encontrado com id: " + solicitanteId));
    }

    @Override
    protected List<Estudante> buscarCandidatos(UUID solicitanteId) {
        return estudanteRepository.buscarCandidatosMatching(solicitanteId);
    }

    @Override
    protected PerfilAcademico perfil(Estudante estudante) {
        return estudante.getPerfilAcademico();
    }

    @Override
    protected boolean elegivel(Estudante solicitante, Estudante candidato) {
        return candidato.isAtivo() && candidato.getPerfilAcademico() != null;
    }

    @Override
    protected void validarPerfilSolicitante(Estudante solicitante) {
        if (solicitante.getPerfilAcademico() == null) {
            throw new PerfilAcademicoAusenteException(
                    "O solicitante nao possui PerfilAcademico cadastrado.");
        }
    }

    private MatchEstudanteResponseDTO mapearResultado(ResultadoMatching<Estudante> resultado) {
        return matchingMapper.toEstudanteResponseDTO(
                resultado.candidato(),
                resultado.compatibilidade());
    }
}
