package com.mentormatch.service.matching;

import com.elo.compatibilidade.MatchingService;
import com.elo.compatibilidade.ResultadoMatching;
import com.mentormatch.dto.response.*;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.MentorMatchingMapper;
import com.mentormatch.model.entity.*;
import com.mentormatch.repository.ParticipanteMentoriaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Slf4j @Service
public class MentorMatchingService extends MatchingService<ParticipanteMentoria, PerfilMentoria> {
    private final ParticipanteMentoriaRepository repository; private final MentorMatchingMapper mapper;
    public MentorMatchingService(ParticipanteMentoriaRepository repository,
            CompatibilidadeMentoriaCalculator calculator, MentorMatchCompatibilidadeLlmProvider llm,
            MentorMatchingMapper mapper) {
        super(calculator, llm); this.repository = repository; this.mapper = mapper;
    }
    public MentorMatchingResponseDTO buscarMentoresCompativeis(UUID alunoId, int topN) {
        List<MatchMentorResponseDTO> mentores = calcularMatches(alunoId, topN).stream().map(this::mapear).toList();
        return new MentorMatchingResponseDTO(alunoId, mentores.size(), mentores);
    }
    @Override protected ParticipanteMentoria buscarSolicitante(UUID id) {
        ParticipanteMentoria participante = repository.findById(id).orElseThrow(() -> new MentorMatchException("Aluno não encontrado: " + id));
        if (!(participante instanceof Aluno)) throw new MentorMatchException("O solicitante do matching deve ser um aluno.");
        return participante;
    }
    @Override protected List<ParticipanteMentoria> buscarCandidatos(UUID id) { return repository.buscarMentoresCandidatos(id); }
    @Override protected PerfilMentoria perfil(ParticipanteMentoria participante) { return participante.getPerfilMentoria(); }
    @Override protected boolean elegivel(ParticipanteMentoria solicitante, ParticipanteMentoria candidato) {
        return candidato instanceof Mentor && candidato.isAtivo() && candidato.getPerfilMentoria() != null;
    }
    @Override protected void validarPerfilSolicitante(ParticipanteMentoria solicitante) {
        if (!solicitante.isAtivo()) throw new MentorMatchException("O aluno solicitante está inativo.");
        if (solicitante.getPerfilMentoria() == null) throw new MentorMatchException("O aluno não possui perfil de mentoria.");
    }
    @Override protected void aoUsarLlm(ParticipanteMentoria solicitante, List<ParticipanteMentoria> candidatos) {
        log.info("Matching Mentor Match via LLM concluído: solicitante={}, candidatos={}", solicitante.getId(), candidatos.size());
    }
    @Override protected void aoUsarFallback(ParticipanteMentoria solicitante, RuntimeException causa) {
        log.warn("Groq indisponível; usando fallback determinístico. solicitante={}, causa={}", solicitante.getId(), causa.getMessage());
    }
    private MatchMentorResponseDTO mapear(ResultadoMatching<ParticipanteMentoria> resultado) {
        return mapper.paraResposta(resultado.candidato(), resultado.compatibilidade());
    }
}
