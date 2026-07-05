package com.mentormatch.service;

import com.elo.denuncia.*;
import com.elo.persistencia.RepositorioBase;
import com.mentormatch.dto.request.CriarDenunciaSessaoRequestDTO;
import com.mentormatch.dto.response.DenunciaSessaoResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.DenunciaSessaoMapper;
import com.mentormatch.model.entity.*;
import com.mentormatch.model.enums.CriterioDenunciaMentorMatch;
import com.mentormatch.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DenunciaSessaoMentoriaService extends DenunciaService<DenunciaSessaoMentoria, SessaoMentoria,
        ParticipanteMentoria, CriarDenunciaSessaoRequestDTO, DenunciaSessaoResponseDTO> {
    private final DenunciaSessaoMentoriaRepository repository; private final SessaoMentoriaRepository sessoes;
    private final ParticipanteMentoriaRepository participantes; private final DenunciaSessaoMapper mapper;
    public DenunciaSessaoMentoriaService(DenunciaSessaoMentoriaRepository repository,
            SessaoMentoriaRepository sessoes, ParticipanteMentoriaRepository participantes, DenunciaSessaoMapper mapper) {
        this.repository=repository; this.sessoes=sessoes; this.participantes=participantes; this.mapper=mapper;
    }
    @Override protected RepositorioBase<DenunciaSessaoMentoria, UUID> repositorio() { return repository; }
    @Override protected UUID denuncianteId(CriarDenunciaSessaoRequestDTO d) { return d.denuncianteId(); }
    @Override protected UUID ofertaId(CriarDenunciaSessaoRequestDTO d) { return d.sessaoId(); }
    @Override protected ParticipanteMentoria buscarDenunciante(UUID id) { return participantes.findById(id).orElseThrow(() -> new MentorMatchException("Participante não encontrado: " + id)); }
    @Override protected SessaoMentoria buscarOferta(UUID id) { return sessoes.findById(id).orElseThrow(() -> new MentorMatchException("Sessão não encontrada: " + id)); }
    @Override protected DenunciaSessaoMentoria construirDenuncia(CriarDenunciaSessaoRequestDTO d) { return new DenunciaSessaoMentoria(); }
    @Override protected CriterioDenuncia criterio(CriarDenunciaSessaoRequestDTO d) { return d.criterioOuOutro(); }
    @Override protected void aplicarCriacao(DenunciaSessaoMentoria d, ParticipanteMentoria denunciante,
            SessaoMentoria sessao, CriarDenunciaSessaoRequestDTO dto, CriterioDenuncia criterio,
            StatusDenuncia status, LocalDateTime criadoEm) {
        d.setDenunciante(denunciante); d.setSessao(sessao); d.setTitulo(dto.titulo()); d.setCorpo(dto.corpo());
        d.setCriterio((CriterioDenunciaMentorMatch) criterio); d.setStatusDenuncia(status);
        d.setCriadoEm(criadoEm); d.setStatusAtualizadoEm(criadoEm);
    }
    @Override protected void aplicarStatus(DenunciaSessaoMentoria d, StatusDenuncia status, LocalDateTime data) { d.setStatusDenuncia(status); d.setStatusAtualizadoEm(data); }
    @Override protected List<DenunciaSessaoMentoria> buscarPorOferta(SessaoMentoria s) { return repository.findBySessao(s); }
    @Override protected List<DenunciaSessaoMentoria> buscarPorDenunciante(ParticipanteMentoria p) { return repository.findByDenunciante(p); }
    @Override protected List<DenunciaSessaoMentoria> buscarPorStatusDenuncia(StatusDenuncia s) { return repository.findByStatusDenuncia(s); }
    @Override protected DenunciaSessaoResponseDTO mapear(DenunciaSessaoMentoria d) { return mapper.paraResposta(d); }
    @Override protected RuntimeException erroDenunciaNaoEncontrada(UUID id) { return new MentorMatchException("Denúncia não encontrada: " + id); }
    @Override protected RuntimeException erroTransicaoInvalida(StatusDenuncia atual, StatusDenuncia novo) { return new MentorMatchException("Transição de denúncia inválida: " + atual + " -> " + novo); }
}
