package com.studybuddy.service;

import com.elo.denuncia.StatusDenuncia;
import com.elo.moderacao.AcaoModeracaoOferta;
import com.elo.persistencia.RepositorioBase;
import com.studybuddy.dto.request.ModerarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.ModeracaoGrupoEstudoResponseDTO;
import com.studybuddy.exception.DenunciaGrupoEstudoNaoEncontradaException;
import com.studybuddy.exception.ModeracaoGrupoEstudoInvalidaException;
import com.studybuddy.mapper.ModeracaoGrupoEstudoMapper;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.AcaoModeracaoGrupoEstudo;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.repository.DenunciaGrupoEstudoRepository;
import com.studybuddy.repository.GrupoEstudoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ModeracaoGrupoEstudoService extends com.elo.moderacao.ModeracaoService<
        DenunciaGrupoEstudo,
        GrupoEstudo,
        ModerarDenunciaGrupoEstudoRequestDTO,
        ModeracaoGrupoEstudoResponseDTO> {

    private final DenunciaGrupoEstudoRepository denunciaRepository;
    private final GrupoEstudoRepository grupoRepository;
    private final ModeracaoGrupoEstudoMapper moderacaoMapper;
    private final ManifestacaoInteresseGrupoService manifestacaoInteresseService;

    public ModeracaoGrupoEstudoService(
            DenunciaGrupoEstudoRepository denunciaRepository,
            GrupoEstudoRepository grupoRepository,
            ModeracaoGrupoEstudoMapper moderacaoMapper,
            ManifestacaoInteresseGrupoService manifestacaoInteresseService) {
        this.denunciaRepository = denunciaRepository;
        this.grupoRepository = grupoRepository;
        this.moderacaoMapper = moderacaoMapper;
        this.manifestacaoInteresseService = manifestacaoInteresseService;
    }

    @Override
    protected RepositorioBase<DenunciaGrupoEstudo, UUID> repositorioDenuncia() {
        return denunciaRepository;
    }

    @Override
    protected RepositorioBase<GrupoEstudo, UUID> repositorioOferta() {
        return grupoRepository;
    }

    @Override
    protected GrupoEstudo ofertaDaDenuncia(DenunciaGrupoEstudo denuncia) {
        return denuncia.getGrupoEstudo();
    }

    @Override
    protected StatusDenuncia novoStatus(ModerarDenunciaGrupoEstudoRequestDTO dto) {
        return dto.novoStatus();
    }

    @Override
    protected AcaoModeracaoOferta acaoOferta(ModerarDenunciaGrupoEstudoRequestDTO dto) {
        return switch (dto.acaoGrupoEstudo()) {
            case NENHUMA -> AcaoModeracaoOferta.NENHUMA;
            case PAUSAR_GRUPO -> AcaoModeracaoOferta.PAUSAR;
            case ENCERRAR_GRUPO -> AcaoModeracaoOferta.ENCERRAR;
        };
    }

    @Override
    protected Object statusOferta(GrupoEstudo oferta) {
        return oferta.getStatus();
    }

    @Override
    protected void aplicarStatusDenuncia(
            DenunciaGrupoEstudo denuncia,
            StatusDenuncia novoStatus,
            LocalDateTime atualizadoEm) {
        denuncia.setStatusDenuncia(novoStatus);
        denuncia.setStatusAtualizadoEm(atualizadoEm);
    }

    @Override
    protected void pausarOferta(GrupoEstudo oferta) {
        oferta.setStatus(StatusGrupoEstudo.PAUSADO);
        manifestacaoInteresseService.cancelarPendentesDaOferta(oferta.getId());
    }

    @Override
    protected void encerrarOferta(GrupoEstudo oferta) {
        oferta.setStatus(StatusGrupoEstudo.ENCERRADO);
        manifestacaoInteresseService.cancelarPendentesDaOferta(oferta.getId());
    }

    @Override
    protected ModeracaoGrupoEstudoResponseDTO mapearResposta(
            DenunciaGrupoEstudo denuncia,
            GrupoEstudo oferta,
            StatusDenuncia statusAnteriorDenuncia,
            Object statusAnteriorOferta,
            ModerarDenunciaGrupoEstudoRequestDTO dto,
            LocalDateTime moderadoEm) {
        return moderacaoMapper.toResponseDTO(
                denuncia,
                oferta,
                statusAnteriorDenuncia,
                (StatusGrupoEstudo) statusAnteriorOferta,
                dto);
    }

    @Override
    protected RuntimeException erroDenunciaNaoEncontrada(UUID id) {
        return new DenunciaGrupoEstudoNaoEncontradaException(
                "Denuncia de grupo de estudo nao encontrada com id: " + id);
    }

    @Override
    protected RuntimeException erroModeracaoInvalida(String mensagem) {
        return new ModeracaoGrupoEstudoInvalidaException(mensagem);
    }
}
