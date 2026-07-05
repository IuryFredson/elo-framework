package com.mentormatch.service;

import com.elo.denuncia.StatusDenuncia;
import com.elo.moderacao.AcaoModeracaoOferta;
import com.elo.persistencia.RepositorioBase;
import com.mentormatch.dto.request.ModerarDenunciaSessaoRequestDTO;
import com.mentormatch.dto.response.ModeracaoSessaoResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.ModeracaoSessaoMapper;
import com.mentormatch.model.entity.*;
import com.mentormatch.model.enums.StatusSessaoMentoria;
import com.mentormatch.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ModeracaoSessaoMentoriaService extends com.elo.moderacao.ModeracaoService<DenunciaSessaoMentoria,
        SessaoMentoria, ModerarDenunciaSessaoRequestDTO, ModeracaoSessaoResponseDTO> {
    private final DenunciaSessaoMentoriaRepository denuncias; private final SessaoMentoriaRepository sessoes;
    private final ModeracaoSessaoMapper mapper; private final SolicitacaoMentoriaService solicitacoes;
    public ModeracaoSessaoMentoriaService(DenunciaSessaoMentoriaRepository denuncias,
            SessaoMentoriaRepository sessoes, ModeracaoSessaoMapper mapper, SolicitacaoMentoriaService solicitacoes) {
        this.denuncias=denuncias; this.sessoes=sessoes; this.mapper=mapper; this.solicitacoes=solicitacoes;
    }
    @Override protected RepositorioBase<DenunciaSessaoMentoria, UUID> repositorioDenuncia() { return denuncias; }
    @Override protected RepositorioBase<SessaoMentoria, UUID> repositorioOferta() { return sessoes; }
    @Override protected SessaoMentoria ofertaDaDenuncia(DenunciaSessaoMentoria d) { return d.getSessao(); }
    @Override protected StatusDenuncia novoStatus(ModerarDenunciaSessaoRequestDTO d) { return d.novoStatus(); }
    @Override protected AcaoModeracaoOferta acaoOferta(ModerarDenunciaSessaoRequestDTO d) {
        return switch (d.acaoSessao()) { case NENHUMA -> AcaoModeracaoOferta.NENHUMA; case PAUSAR_SESSAO -> AcaoModeracaoOferta.PAUSAR; case ENCERRAR_SESSAO -> AcaoModeracaoOferta.ENCERRAR; };
    }
    @Override protected Object statusOferta(SessaoMentoria s) { return s.getStatus(); }
    @Override protected void aplicarStatusDenuncia(DenunciaSessaoMentoria d, StatusDenuncia status, LocalDateTime data) { d.setStatusDenuncia(status); d.setStatusAtualizadoEm(data); }
    @Override protected void pausarOferta(SessaoMentoria s) { s.setStatus(StatusSessaoMentoria.PAUSADA); solicitacoes.cancelarPendentesDaOferta(s.getId()); }
    @Override protected void encerrarOferta(SessaoMentoria s) { s.setStatus(StatusSessaoMentoria.ENCERRADA); solicitacoes.cancelarPendentesDaOferta(s.getId()); }
    @Override protected ModeracaoSessaoResponseDTO mapearResposta(DenunciaSessaoMentoria d, SessaoMentoria s,
            StatusDenuncia statusAnterior, Object statusOfertaAnterior, ModerarDenunciaSessaoRequestDTO dto, LocalDateTime data) {
        return mapper.paraResposta(d, s, statusAnterior, (StatusSessaoMentoria) statusOfertaAnterior, dto);
    }
    @Override protected RuntimeException erroDenunciaNaoEncontrada(UUID id) { return new MentorMatchException("Denúncia não encontrada: " + id); }
    @Override protected RuntimeException erroModeracaoInvalida(String mensagem) { return new MentorMatchException(mensagem); }
}
