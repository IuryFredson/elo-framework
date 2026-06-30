package com.apto.service;

import com.apto.dto.request.ModerarDenunciaRequestDTO;
import com.apto.dto.response.ModeracaoResponseDTO;
import com.apto.exception.DenunciaNaoEncontradaException;
import com.apto.exception.ModeracaoInvalidaException;
import com.apto.mapper.ModeracaoMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.model.enums.AcaoModeracaoAnuncio;
import com.apto.model.enums.StatusAnuncio;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.DenunciaRepository;
import com.elo.denuncia.StatusDenuncia;
import com.elo.moderacao.AcaoModeracaoOferta;
import com.elo.persistencia.RepositorioBase;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ModeracaoService extends com.elo.moderacao.ModeracaoService<
        Denuncia,
        Anuncio,
        ModerarDenunciaRequestDTO,
        ModeracaoResponseDTO> {

    private final DenunciaRepository denunciaRepository;
    private final AnuncioRepository anuncioRepository;
    private final ModeracaoMapper moderacaoMapper;

    public ModeracaoService(DenunciaRepository denunciaRepository,
                            AnuncioRepository anuncioRepository,
                            ModeracaoMapper moderacaoMapper) {
        this.denunciaRepository = denunciaRepository;
        this.anuncioRepository = anuncioRepository;
        this.moderacaoMapper = moderacaoMapper;
    }

    @Override
    protected RepositorioBase<Denuncia, UUID> repositorioDenuncia() {
        return denunciaRepository;
    }

    @Override
    protected RepositorioBase<Anuncio, UUID> repositorioOferta() {
        return anuncioRepository;
    }

    @Override
    protected Anuncio ofertaDaDenuncia(Denuncia denuncia) {
        return denuncia.getAnuncio();
    }

    @Override
    protected StatusDenuncia novoStatus(ModerarDenunciaRequestDTO dto) {
        return dto.novoStatus();
    }

    @Override
    protected AcaoModeracaoOferta acaoOferta(ModerarDenunciaRequestDTO dto) {
        return switch (dto.acaoAnuncio()) {
            case NENHUMA -> AcaoModeracaoOferta.NENHUMA;
            case PAUSAR_ANUNCIO -> AcaoModeracaoOferta.PAUSAR;
            case ENCERRAR_ANUNCIO -> AcaoModeracaoOferta.ENCERRAR;
        };
    }

    @Override
    protected Object statusOferta(Anuncio oferta) {
        return oferta.getStatus();
    }

    @Override
    protected void aplicarStatusDenuncia(Denuncia denuncia, StatusDenuncia novoStatus, LocalDateTime atualizadoEm) {
        denuncia.setStatusDenuncia(novoStatus);
        denuncia.setStatusAtualizadoEm(atualizadoEm);
    }

    @Override
    protected void pausarOferta(Anuncio oferta) {
        oferta.setStatus(StatusAnuncio.PAUSADO);
    }

    @Override
    protected void encerrarOferta(Anuncio oferta) {
        oferta.setStatus(StatusAnuncio.ENCERRADO);
    }

    @Override
    protected ModeracaoResponseDTO mapearResposta(
            Denuncia denuncia,
            Anuncio oferta,
            StatusDenuncia statusAnteriorDenuncia,
            Object statusAnteriorOferta,
            ModerarDenunciaRequestDTO dto,
            LocalDateTime moderadoEm) {
        return moderacaoMapper.toResponseDTO(
                denuncia,
                oferta,
                statusAnteriorDenuncia,
                (StatusAnuncio) statusAnteriorOferta,
                dto);
    }

    @Override
    protected RuntimeException erroDenunciaNaoEncontrada(UUID id) {
        return new DenunciaNaoEncontradaException("Denúncia não encontrada com id: " + id);
    }

    @Override
    protected RuntimeException erroModeracaoInvalida(String mensagem) {
        return new ModeracaoInvalidaException(mensagem);
    }
}
