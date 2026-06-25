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
import com.apto.model.enums.StatusDenuncia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.DenunciaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ModeracaoService {

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

    public ModeracaoResponseDTO moderar(UUID denunciaId, ModerarDenunciaRequestDTO dto) {
        Denuncia denuncia = buscarDenunciaPorId(denunciaId);
        Anuncio anuncio = denuncia.getAnuncio();

        StatusDenuncia statusAnteriorDenuncia = denuncia.getStatusDenuncia();
        StatusAnuncio statusAnteriorAnuncio = anuncio.getStatus();

        validarDecisao(statusAnteriorDenuncia, dto.novoStatus(), dto.acaoAnuncio());

        aplicarDecisaoNaDenuncia(denuncia, dto.novoStatus());
        aplicarAcaoNoAnuncio(anuncio, dto.acaoAnuncio(), dto.novoStatus());

        anuncioRepository.save(anuncio);
        denunciaRepository.save(denuncia);

        return moderacaoMapper.toResponseDTO(
                denuncia,
                anuncio,
                statusAnteriorDenuncia,
                statusAnteriorAnuncio,
                dto);
    }

    private Denuncia buscarDenunciaPorId(UUID denunciaId) {
        return denunciaRepository.findById(denunciaId)
                .orElseThrow(() ->
                        new DenunciaNaoEncontradaException("Denúncia não encontrada com id: " + denunciaId)
                );
    }

    private void aplicarDecisaoNaDenuncia(Denuncia denuncia, StatusDenuncia novoStatus) {
        denuncia.setStatusDenuncia(novoStatus);
        denuncia.setStatusAtualizadoEm(LocalDateTime.now());
    }

    private void aplicarAcaoNoAnuncio(Anuncio anuncio,
                                      AcaoModeracaoAnuncio acao,
                                      StatusDenuncia novoStatusDenuncia) {

        if (novoStatusDenuncia != StatusDenuncia.PROCEDENTE) {
            return;
        }

        switch (acao) {
            case NENHUMA -> {
            }
            case PAUSAR_ANUNCIO -> anuncio.setStatus(StatusAnuncio.PAUSADO);
            case ENCERRAR_ANUNCIO -> anuncio.setStatus(StatusAnuncio.ENCERRADO);
        }
    }

    private void validarDecisao(StatusDenuncia statusAtual,
                                StatusDenuncia novoStatus,
                                AcaoModeracaoAnuncio acaoAnuncio) {

        if (statusAtual == StatusDenuncia.ARQUIVADA) {
            throw new ModeracaoInvalidaException("Não é possível moderar uma denúncia arquivada.");
        }

        switch (novoStatus) {
            case EM_ANALISE -> validarEntradaEmAnalise(statusAtual, acaoAnuncio);
            case IMPROCEDENTE -> validarImprocedencia(statusAtual, acaoAnuncio);
            case PROCEDENTE -> validarProcedencia(statusAtual, acaoAnuncio);
            case ARQUIVADA -> validarArquivamento(statusAtual, acaoAnuncio);
            case PENDENTE -> throw new ModeracaoInvalidaException("Não é permitido retornar denúncia para PENDENTE.");
        }
    }

    private void validarEntradaEmAnalise(StatusDenuncia statusAtual,
                                         AcaoModeracaoAnuncio acaoAnuncio) {
        if (statusAtual != StatusDenuncia.PENDENTE) {
            throw new ModeracaoInvalidaException(
                    "Só denúncias PENDENTES podem ser colocadas EM_ANALISE."
            );
        }

        if (acaoAnuncio != AcaoModeracaoAnuncio.NENHUMA) {
            throw new ModeracaoInvalidaException(
                    "Não é permitido aplicar ação ao anúncio ao colocar denúncia EM_ANALISE."
            );
        }
    }

    private void validarImprocedencia(StatusDenuncia statusAtual,
                                      AcaoModeracaoAnuncio acaoAnuncio) {
        if (statusAtual != StatusDenuncia.EM_ANALISE) {
            throw new ModeracaoInvalidaException(
                    "Só denúncias EM_ANALISE podem ser julgadas como IMPROCEDENTE."
            );
        }

        if (acaoAnuncio != AcaoModeracaoAnuncio.NENHUMA) {
            throw new ModeracaoInvalidaException(
                    "Denúncia IMPROCEDENTE não pode aplicar ação ao anúncio."
            );
        }
    }

    private void validarProcedencia(StatusDenuncia statusAtual,
                                    AcaoModeracaoAnuncio acaoAnuncio) {
        if (statusAtual != StatusDenuncia.EM_ANALISE) {
            throw new ModeracaoInvalidaException(
                    "Só denúncias EM_ANALISE podem ser julgadas como PROCEDENTE."
            );
        }

        if (acaoAnuncio == AcaoModeracaoAnuncio.NENHUMA) {
            throw new ModeracaoInvalidaException(
                    "Denúncia PROCEDENTE deve aplicar uma ação ao anúncio."
            );
        }
    }

    private void validarArquivamento(StatusDenuncia statusAtual,
                                     AcaoModeracaoAnuncio acaoAnuncio) {
        if (statusAtual != StatusDenuncia.PROCEDENTE
                && statusAtual != StatusDenuncia.IMPROCEDENTE) {
            throw new ModeracaoInvalidaException(
                    "Só denúncias PROCEDENTE ou IMPROCEDENTE podem ser arquivadas."
            );
        }

        if (acaoAnuncio != AcaoModeracaoAnuncio.NENHUMA) {
            throw new ModeracaoInvalidaException(
                    "Arquivamento não pode aplicar ação ao anúncio."
            );
        }
    }
}
