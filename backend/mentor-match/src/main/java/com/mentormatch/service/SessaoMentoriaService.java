package com.mentormatch.service;

import com.elo.oferta.OfertaService;
import com.mentormatch.dto.request.*;
import com.mentormatch.dto.response.SessaoMentoriaResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.SessaoMentoriaMapper;
import com.mentormatch.model.entity.*;
import com.mentormatch.model.enums.*;
import com.mentormatch.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class SessaoMentoriaService extends OfertaService<SessaoMentoria, CriarSessaoMentoriaRequestDTO, AtualizarSessaoMentoriaRequestDTO, SessaoMentoriaResponseDTO, StatusSessaoMentoria> {
    private final SessaoMentoriaRepository repository; private final MentorRepository mentores; private final SessaoMentoriaMapper mapper;
    public SessaoMentoriaService(SessaoMentoriaRepository repository, MentorRepository mentores, SessaoMentoriaMapper mapper) { this.repository=repository; this.mentores=mentores; this.mapper=mapper; }
    @Override protected SessaoMentoriaRepository repositorio() { return repository; }
    @Override protected SessaoMentoriaMapper mapperResposta() { return mapper; }
    @Override protected RuntimeException erroOfertaNaoEncontrada(UUID id) { return new MentorMatchException("Sessão de mentoria não encontrada: " + id); }
    @Override protected SessaoMentoria construirOferta(CriarSessaoMentoriaRequestDTO d) {
        Mentor mentor = mentores.findById(d.mentorId()).orElseThrow(() -> new MentorMatchException("Mentor não encontrado: " + d.mentorId()));
        SessaoMentoria s = new SessaoMentoria(); s.setPublicador(mentor); aplicar(s, d.titulo(), d.descricao(), d.area(), d.nivelAtendido(), d.modalidade(), d.periodo(), d.capacidade());
        s.setStatus(StatusSessaoMentoria.ATIVA); s.setDataPublicacao(LocalDate.now()); return s;
    }
    @Override protected void validarAtualizacao(SessaoMentoria s, UUID publicadorId, AtualizarSessaoMentoriaRequestDTO d) {
        if (!s.getPublicadorId().equals(publicadorId)) throw new MentorMatchException("Somente o mentor publicador pode atualizar a sessão.");
    }
    @Override protected void aplicarAtualizacao(SessaoMentoria s, AtualizarSessaoMentoriaRequestDTO d) { aplicar(s, d.titulo(), d.descricao(), d.area(), d.nivelAtendido(), d.modalidade(), d.periodo(), d.capacidade()); }
    private void aplicar(SessaoMentoria s, String titulo, String descricao, String area, NivelConhecimento nivel, ModalidadeMentoria modalidade, PeriodoDisponibilidade periodo, int capacidade) {
        s.setTitulo(titulo); s.setDescricao(descricao); s.setArea(area); s.setNivelAtendido(nivel); s.setModalidade(modalidade); s.setPeriodo(periodo); s.setCapacidade(capacidade);
    }
    @Override protected StatusSessaoMentoria obterStatus(SessaoMentoria s) { return s.getStatus(); }
    @Override protected void aplicarStatus(SessaoMentoria s, StatusSessaoMentoria status) { s.setStatus(status); }
    @Override protected boolean deveExcluirFisicamente(SessaoMentoria s) { return true; }
    @Override protected void aplicarExclusaoLogica(SessaoMentoria s) { s.setStatus(StatusSessaoMentoria.ENCERRADA); }
    @Transactional(readOnly = true)
    public List<SessaoMentoriaResponseDTO> buscar(String area, ModalidadeMentoria modalidade, NivelConhecimento nivel, PeriodoDisponibilidade periodo) {
        return repository.buscarAtivas(area, modalidade, nivel, periodo).stream().map(mapper::paraResposta).toList();
    }
}
