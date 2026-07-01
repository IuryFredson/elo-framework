package com.apto.service;

import com.apto.dto.request.CriarManifestacaoInteresseRequestDTO;
import com.apto.dto.response.ManifestacaoInteresseDetalheResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseResponseDTO;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AnuncioNaoAtivoException;
import com.apto.exception.AnuncioNaoEncontradoException;
import com.apto.exception.ManifestacaoInteresseDuplicadaException;
import com.apto.exception.ManifestacaoInteresseInvalidaException;
import com.apto.exception.ManifestacaoInteresseNaoEncontradaException;
import com.apto.exception.TransicaoInvalidaManifestacaoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.ManifestacaoInteresseMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.elo.persistencia.RepositorioBase;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class ManifestacaoInteresseService extends com.elo.manifestacao.ManifestacaoInteresseService<
        ManifestacaoInteresse,
        Anuncio,
        UsuarioUniversitario,
        CriarManifestacaoInteresseRequestDTO,
        ManifestacaoInteresseResponseDTO,
        ManifestacaoInteresseDetalheResponseDTO> {

    private final ManifestacaoInteresseRepository manifestacaoRepository;
    private final AnuncioRepository anuncioRepository;
    private final UsuarioUniversitarioRepository universitarioRepository;
    private final ManifestacaoInteresseMapper manifestacaoMapper;

    public ManifestacaoInteresseService(ManifestacaoInteresseRepository manifestacaoRepository,
                                        AnuncioRepository anuncioRepository,
                                        UsuarioUniversitarioRepository universitarioRepository,
                                        ManifestacaoInteresseMapper manifestacaoMapper) {
        this.manifestacaoRepository = manifestacaoRepository;
        this.anuncioRepository = anuncioRepository;
        this.universitarioRepository = universitarioRepository;
        this.manifestacaoMapper = manifestacaoMapper;
    }

    public List<ManifestacaoInteresseResponseDTO> listarPorAnuncio(UUID anuncioId, UUID anuncianteId) {
        return listarPorOferta(anuncioId, anuncianteId);
    }

    @Override
    protected RepositorioBase<ManifestacaoInteresse, UUID> repositorio() {
        return manifestacaoRepository;
    }

    @Override
    protected UUID ofertaId(CriarManifestacaoInteresseRequestDTO dto) {
        return dto.anuncioId();
    }

    @Override
    protected UUID interessadoId(CriarManifestacaoInteresseRequestDTO dto) {
        return dto.interessadoId();
    }

    @Override
    protected Anuncio buscarOferta(UUID ofertaId) {
        return anuncioRepository.findById(ofertaId)
                .orElseThrow(() -> new AnuncioNaoEncontradoException(
                        "Anúncio não encontrado com o id: " + ofertaId));
    }

    @Override
    protected UsuarioUniversitario buscarInteressado(UUID interessadoId) {
        return universitarioRepository.findById(interessadoId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário universitário não encontrado com id: " + interessadoId));
    }

    @Override
    protected ManifestacaoInteresse construirManifestacao(
            Anuncio oferta,
            UsuarioUniversitario interessado,
            CriarManifestacaoInteresseRequestDTO dto) {
        return new ManifestacaoInteresse();
    }

    @Override
    protected void aplicarCriacao(
            ManifestacaoInteresse manifestacao,
            Anuncio oferta,
            UsuarioUniversitario interessado,
            CriarManifestacaoInteresseRequestDTO dto,
            StatusManifestacaoInteresse status,
            LocalDateTime dataManifestacao) {
        manifestacao.setAnuncio(oferta);
        manifestacao.setInteressado(interessado);
        manifestacao.setStatus(status);
        manifestacao.setMensagem(dto.mensagem());
        manifestacao.setDataManifestacao(dataManifestacao);
    }

    @Override
    protected void aplicarResposta(
            ManifestacaoInteresse manifestacao,
            StatusManifestacaoInteresse status,
            LocalDateTime dataResposta) {
        manifestacao.setStatus(status);
        manifestacao.setDataResposta(dataResposta);
    }

    @Override
    protected boolean existeManifestacaoAtiva(
            UUID ofertaId,
            UUID interessadoId,
            Collection<StatusManifestacaoInteresse> statusAtivos) {
        return manifestacaoRepository.existsByAnuncio_IdAndInteressado_IdAndStatusIn(
                ofertaId,
                interessadoId,
                statusAtivos);
    }

    @Override
    protected List<ManifestacaoInteresse> buscarPorOfertaOrdenado(UUID ofertaId) {
        return manifestacaoRepository.findByAnuncio_IdOrderByDataManifestacaoDesc(ofertaId);
    }

    @Override
    protected List<ManifestacaoInteresse> buscarPorInteressadoOrdenado(UUID interessadoId) {
        return manifestacaoRepository.findByInteressado_IdOrderByDataManifestacaoDesc(interessadoId);
    }

    @Override
    protected List<ManifestacaoInteresse> buscarPorOfertaEStatus(
            UUID ofertaId,
            StatusManifestacaoInteresse status) {
        return manifestacaoRepository.findByAnuncio_IdAndStatus(ofertaId, status);
    }

    @Override
    protected UUID publicadorId(ManifestacaoInteresse manifestacao) {
        return manifestacao.getAnuncio().getPublicadorId();
    }

    @Override
    protected ManifestacaoInteresseResponseDTO mapearResumo(ManifestacaoInteresse manifestacao) {
        return manifestacaoMapper.toResponseDTO(manifestacao);
    }

    @Override
    protected ManifestacaoInteresseDetalheResponseDTO mapearDetalhe(ManifestacaoInteresse manifestacao) {
        return manifestacaoMapper.toDetalheDTO(manifestacao);
    }

    @Override
    protected RuntimeException erroManifestacaoNaoEncontrada(UUID id) {
        return new ManifestacaoInteresseNaoEncontradaException(
                "Manifestação de interesse não encontrada com o id: " + id);
    }

    @Override
    protected RuntimeException erroOfertaInativa() {
        return new AnuncioNaoAtivoException(
                "Não é possível manifestar interesse em um anúncio que não está ativo.");
    }

    @Override
    protected RuntimeException erroInteresseProprio() {
        return new ManifestacaoInteresseInvalidaException(
                "Não é possível manifestar interesse no próprio anúncio.");
    }

    @Override
    protected RuntimeException erroManifestacaoDuplicada() {
        return new ManifestacaoInteresseDuplicadaException(
                "Já existe uma manifestação de interesse ativa deste usuário para este anúncio.");
    }

    @Override
    protected RuntimeException erroAcessoNegado() {
        return new AcessoNegadoException(
                "Usuário não tem permissão para acessar esta manifestação de interesse.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoResposta() {
        return new AcessoNegadoException(
                "Usuário não tem permissão para responder esta manifestação de interesse.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoCancelamento() {
        return new AcessoNegadoException(
                "Usuário não tem permissão para cancelar esta manifestação de interesse.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoListagemOferta() {
        return new AcessoNegadoException(
                "Usuário não tem permissão para visualizar as manifestações deste anúncio.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoVisualizacao() {
        return new AcessoNegadoException(
                "Usuário não tem permissão para visualizar esta manifestação de interesse.");
    }

    @Override
    protected RuntimeException erroTransicaoInvalida() {
        return new TransicaoInvalidaManifestacaoException(
                "Transição inválida: manifestação não está em PENDENTE.");
    }
}
