package com.apto.service;

import com.apto.dto.request.CriarManifestacaoInteresseRequestDTO;
import com.apto.dto.response.ContatoLiberadoResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseDetalheResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseResponseDTO;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AnuncioNaoAtivoException;
import com.apto.exception.ManifestacaoInteresseDuplicadaException;
import com.apto.exception.ManifestacaoInteresseInvalidaException;
import com.apto.exception.ManifestacaoInteresseNaoEncontradaException;
import com.apto.exception.TransicaoInvalidaManifestacaoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.ManifestacaoInteresseMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.StatusAnuncio;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ManifestacaoInteresseService {

    private static final Set<StatusManifestacaoInteresse> STATUS_ATIVOS =
            Set.of(StatusManifestacaoInteresse.PENDENTE, StatusManifestacaoInteresse.ACEITA);

    private final ManifestacaoInteresseRepository manifestacaoRepository;
    private final AnuncioService anuncioService;
    private final UsuarioUniversitarioRepository universitarioRepository;
    private final ManifestacaoInteresseMapper manifestacaoMapper;

    public ManifestacaoInteresseService(ManifestacaoInteresseRepository manifestacaoRepository,
                                        AnuncioService anuncioService,
                                        UsuarioUniversitarioRepository universitarioRepository,
                                        ManifestacaoInteresseMapper manifestacaoMapper) {
        this.manifestacaoRepository = manifestacaoRepository;
        this.anuncioService = anuncioService;
        this.universitarioRepository = universitarioRepository;
        this.manifestacaoMapper = manifestacaoMapper;
    }

    public ManifestacaoInteresseDetalheResponseDTO criar(CriarManifestacaoInteresseRequestDTO dto) {
        Anuncio anuncio = anuncioService.buscarEntidadePorId(dto.anuncioId());

        UsuarioUniversitario interessado = universitarioRepository.findById(dto.interessadoId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário universitário não encontrado com id: " + dto.interessadoId()));

        if (anuncio.getStatus() != StatusAnuncio.ATIVO) {
            throw new AnuncioNaoAtivoException(
                    "Não é possível manifestar interesse em um anúncio que não está ativo.");
        }

        if (anuncio.getAnunciante().getUsuario().getId().equals(interessado.getId())) {
            throw new ManifestacaoInteresseInvalidaException(
                    "Não é possível manifestar interesse no próprio anúncio.");
        }

        if (manifestacaoRepository.existsByAnuncio_IdAndInteressado_IdAndStatusIn(
                anuncio.getId(), interessado.getId(), STATUS_ATIVOS)) {
            throw new ManifestacaoInteresseDuplicadaException(
                    "Já existe uma manifestação de interesse ativa deste usuário para este anúncio.");
        }

        ManifestacaoInteresse manifestacao = new ManifestacaoInteresse();
        manifestacao.setAnuncio(anuncio);
        manifestacao.setInteressado(interessado);
        manifestacao.setStatus(StatusManifestacaoInteresse.PENDENTE);
        manifestacao.setMensagem(dto.mensagem());
        manifestacao.setDataManifestacao(LocalDateTime.now());

        ManifestacaoInteresse salva = manifestacaoRepository.save(manifestacao);
        return manifestacaoMapper.toDetalheDTO(salva);
    }

    public ManifestacaoInteresseDetalheResponseDTO aceitar(UUID id, UUID anuncianteId) {
        ManifestacaoInteresse manifestacao = buscarEntidadePorId(id);
        validarAnunciante(manifestacao, anuncianteId);
        validarTransicaoDePendente(manifestacao);

        manifestacao.setStatus(StatusManifestacaoInteresse.ACEITA);
        manifestacao.setDataResposta(LocalDateTime.now());
        return manifestacaoMapper.toDetalheDTO(manifestacaoRepository.save(manifestacao));
    }

    public ManifestacaoInteresseDetalheResponseDTO recusar(UUID id, UUID anuncianteId) {
        ManifestacaoInteresse manifestacao = buscarEntidadePorId(id);
        validarAnunciante(manifestacao, anuncianteId);
        validarTransicaoDePendente(manifestacao);

        manifestacao.setStatus(StatusManifestacaoInteresse.RECUSADA);
        manifestacao.setDataResposta(LocalDateTime.now());
        return manifestacaoMapper.toDetalheDTO(manifestacaoRepository.save(manifestacao));
    }

    public ManifestacaoInteresseDetalheResponseDTO cancelar(UUID id, UUID interessadoId) {
        ManifestacaoInteresse manifestacao = buscarEntidadePorId(id);

        if (!manifestacao.getInteressado().getId().equals(interessadoId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para cancelar esta manifestação de interesse.");
        }

        validarTransicaoDePendente(manifestacao);

        manifestacao.setStatus(StatusManifestacaoInteresse.CANCELADA);
        manifestacao.setDataResposta(LocalDateTime.now());
        return manifestacaoMapper.toDetalheDTO(manifestacaoRepository.save(manifestacao));
    }

    public List<ManifestacaoInteresseResponseDTO> listarPorAnuncio(UUID anuncioId, UUID anuncianteId) {
        Anuncio anuncio = anuncioService.buscarEntidadePorId(anuncioId);

        if (!anuncio.getAnunciante().getUsuario().getId().equals(anuncianteId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para visualizar as manifestações deste anúncio.");
        }

        return manifestacaoRepository.findByAnuncio_IdOrderByDataManifestacaoDesc(anuncioId)
                .stream()
                .map(manifestacaoMapper::toResponseDTO)
                .toList();
    }

    public List<ManifestacaoInteresseResponseDTO> listarPorInteressado(UUID interessadoId) {
        return manifestacaoRepository.findByInteressado_IdOrderByDataManifestacaoDesc(interessadoId)
                .stream()
                .map(manifestacaoMapper::toResponseDTO)
                .toList();
    }

    public ManifestacaoInteresseDetalheResponseDTO buscarPorId(UUID id, UUID solicitanteId) {
        ManifestacaoInteresse manifestacao = buscarEntidadePorId(id);

        UUID interessadoId = manifestacao.getInteressado().getId();
        UUID anuncianteId = manifestacao.getAnuncio().getAnunciante().getUsuario().getId();

        if (!solicitanteId.equals(interessadoId) && !solicitanteId.equals(anuncianteId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para visualizar esta manifestação de interesse.");
        }

        return manifestacaoMapper.toDetalheDTO(manifestacao);
    }

    private ManifestacaoInteresse buscarEntidadePorId(UUID id) {
        return manifestacaoRepository.findById(id)
                .orElseThrow(() -> new ManifestacaoInteresseNaoEncontradaException(
                        "Manifestação de interesse não encontrada com o id: " + id));
    }

    private void validarAnunciante(ManifestacaoInteresse manifestacao, UUID anuncianteId) {
        if (!manifestacao.getAnuncio().getAnunciante().getUsuario().getId().equals(anuncianteId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para responder esta manifestação de interesse.");
        }
    }

    private void validarTransicaoDePendente(ManifestacaoInteresse manifestacao) {
        if (manifestacao.getStatus() != StatusManifestacaoInteresse.PENDENTE) {
            throw new TransicaoInvalidaManifestacaoException(
                    "Transição inválida: manifestação não está em PENDENTE.");
        }
    }

}
