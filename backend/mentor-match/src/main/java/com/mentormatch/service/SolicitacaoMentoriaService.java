package com.mentormatch.service;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.elo.persistencia.RepositorioBase;
import com.mentormatch.dto.request.CriarSolicitacaoMentoriaRequestDTO;
import com.mentormatch.dto.response.SolicitacaoMentoriaResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.SolicitacaoMentoriaMapper;
import com.mentormatch.model.entity.*;
import com.mentormatch.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class SolicitacaoMentoriaService extends com.elo.manifestacao.ManifestacaoInteresseService<SolicitacaoMentoria, SessaoMentoria, Aluno, CriarSolicitacaoMentoriaRequestDTO, SolicitacaoMentoriaResponseDTO, SolicitacaoMentoriaResponseDTO> {
    private final SolicitacaoMentoriaRepository repository; private final SessaoMentoriaRepository sessoes; private final AlunoRepository alunos; private final SolicitacaoMentoriaMapper mapper;
    public SolicitacaoMentoriaService(SolicitacaoMentoriaRepository repository, SessaoMentoriaRepository sessoes, AlunoRepository alunos, SolicitacaoMentoriaMapper mapper) { this.repository=repository; this.sessoes=sessoes; this.alunos=alunos; this.mapper=mapper; }
    @Override protected RepositorioBase<SolicitacaoMentoria, UUID> repositorio() { return repository; }
    @Override protected UUID ofertaId(CriarSolicitacaoMentoriaRequestDTO d) { return d.sessaoId(); }
    @Override protected UUID interessadoId(CriarSolicitacaoMentoriaRequestDTO d) { return d.alunoId(); }
    @Override protected SessaoMentoria buscarOferta(UUID id) { return sessoes.findById(id).orElseThrow(() -> erroOferta()); }
    @Override protected Aluno buscarInteressado(UUID id) { return alunos.findById(id).orElseThrow(() -> new MentorMatchException("Aluno não encontrado: " + id)); }
    @Override protected SolicitacaoMentoria construirManifestacao(SessaoMentoria o, Aluno a, CriarSolicitacaoMentoriaRequestDTO d) { return new SolicitacaoMentoria(); }
    @Override protected void aplicarCriacao(SolicitacaoMentoria s, SessaoMentoria o, Aluno a, CriarSolicitacaoMentoriaRequestDTO d, StatusManifestacaoInteresse status, LocalDateTime data) {
        s.setSessao(o); s.setInteressado(a); s.setMensagem(d.mensagem()); s.setStatus(status); s.setDataSolicitacao(data);
    }
    @Override protected void aplicarResposta(SolicitacaoMentoria s, StatusManifestacaoInteresse status, LocalDateTime data) { s.setStatus(status); s.setDataResposta(data); }
    @Override protected boolean existeManifestacaoAtiva(UUID oferta, UUID aluno, Collection<StatusManifestacaoInteresse> status) { return repository.existsBySessao_IdAndInteressado_IdAndStatusIn(oferta, aluno, status); }
    @Override protected List<SolicitacaoMentoria> buscarPorOfertaOrdenado(UUID id) { return repository.findBySessao_IdOrderByDataSolicitacaoDesc(id); }
    @Override protected List<SolicitacaoMentoria> buscarPorInteressadoOrdenado(UUID id) { return repository.findByInteressado_IdOrderByDataSolicitacaoDesc(id); }
    @Override protected List<SolicitacaoMentoria> buscarPorOfertaEStatus(UUID id, StatusManifestacaoInteresse status) { return repository.findBySessao_IdAndStatus(id, status); }
    @Override protected UUID publicadorId(SolicitacaoMentoria s) { return s.getSessao().getPublicadorId(); }
    @Override protected SolicitacaoMentoriaResponseDTO mapearResumo(SolicitacaoMentoria s) { return mapper.paraResposta(s); }
    @Override protected SolicitacaoMentoriaResponseDTO mapearDetalhe(SolicitacaoMentoria s) { return mapper.paraResposta(s); }
    @Override protected RuntimeException erroManifestacaoNaoEncontrada(UUID id) { return new MentorMatchException("Solicitação não encontrada: " + id); }
    @Override protected RuntimeException erroOfertaInativa() { return new MentorMatchException("A sessão não está ativa."); }
    @Override protected RuntimeException erroInteresseProprio() { return new MentorMatchException("Não é possível solicitar a própria sessão."); }
    @Override protected RuntimeException erroManifestacaoDuplicada() { return new MentorMatchException("Já existe solicitação ativa para esta sessão."); }
    @Override protected RuntimeException erroAcessoNegado() { return new MentorMatchException("Acesso negado à solicitação."); }
    @Override protected RuntimeException erroTransicaoInvalida() { return new MentorMatchException("Transição de solicitação inválida."); }
    private RuntimeException erroOferta() { return new MentorMatchException("Sessão de mentoria não encontrada."); }
}
