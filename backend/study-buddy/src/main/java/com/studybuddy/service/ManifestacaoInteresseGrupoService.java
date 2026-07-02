package com.studybuddy.service;

import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.elo.persistencia.RepositorioBase;
import com.studybuddy.dto.request.CriarManifestacaoInteresseGrupoRequestDTO;
import com.studybuddy.dto.response.ManifestacaoInteresseGrupoResponseDTO;
import com.studybuddy.exception.AcessoNegadoException;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.GrupoEstudoInativoException;
import com.studybuddy.exception.GrupoEstudoNaoEncontradoException;
import com.studybuddy.exception.ManifestacaoInteresseDuplicadaException;
import com.studybuddy.exception.ManifestacaoInteresseInvalidaException;
import com.studybuddy.exception.ManifestacaoInteresseNaoEncontradaException;
import com.studybuddy.exception.TransicaoInvalidaManifestacaoException;
import com.studybuddy.mapper.ManifestacaoInteresseGrupoMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.entity.ManifestacaoInteresseGrupo;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.GrupoEstudoRepository;
import com.studybuddy.repository.ManifestacaoInteresseGrupoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class ManifestacaoInteresseGrupoService extends com.elo.manifestacao.ManifestacaoInteresseService<
        ManifestacaoInteresseGrupo,
        GrupoEstudo,
        Estudante,
        CriarManifestacaoInteresseGrupoRequestDTO,
        ManifestacaoInteresseGrupoResponseDTO,
        ManifestacaoInteresseGrupoResponseDTO> {

    private final ManifestacaoInteresseGrupoRepository manifestacaoRepository;
    private final GrupoEstudoRepository grupoRepository;
    private final EstudanteRepository estudanteRepository;
    private final ManifestacaoInteresseGrupoMapper manifestacaoMapper;

    public ManifestacaoInteresseGrupoService(ManifestacaoInteresseGrupoRepository manifestacaoRepository,
                                             GrupoEstudoRepository grupoRepository,
                                             EstudanteRepository estudanteRepository,
                                             ManifestacaoInteresseGrupoMapper manifestacaoMapper) {
        this.manifestacaoRepository = manifestacaoRepository;
        this.grupoRepository = grupoRepository;
        this.estudanteRepository = estudanteRepository;
        this.manifestacaoMapper = manifestacaoMapper;
    }

    public List<ManifestacaoInteresseGrupoResponseDTO> listarPorGrupo(UUID grupoId, UUID publicadorId) {
        return listarPorOferta(grupoId, publicadorId);
    }

    @Override
    protected RepositorioBase<ManifestacaoInteresseGrupo, UUID> repositorio() {
        return manifestacaoRepository;
    }

    @Override
    protected UUID ofertaId(CriarManifestacaoInteresseGrupoRequestDTO dto) {
        return dto.grupoId();
    }

    @Override
    protected UUID interessadoId(CriarManifestacaoInteresseGrupoRequestDTO dto) {
        return dto.interessadoId();
    }

    @Override
    protected GrupoEstudo buscarOferta(UUID ofertaId) {
        return grupoRepository.findById(ofertaId)
                .orElseThrow(() -> new GrupoEstudoNaoEncontradoException(
                        "Grupo de estudo nao encontrado com id: " + ofertaId));
    }

    @Override
    protected Estudante buscarInteressado(UUID interessadoId) {
        return estudanteRepository.findById(interessadoId)
                .orElseThrow(() -> new EstudanteNaoEncontradoException(
                        "Estudante nao encontrado com id: " + interessadoId));
    }

    @Override
    protected ManifestacaoInteresseGrupo construirManifestacao(
            GrupoEstudo oferta,
            Estudante interessado,
            CriarManifestacaoInteresseGrupoRequestDTO dto) {
        return new ManifestacaoInteresseGrupo();
    }

    @Override
    protected void aplicarCriacao(
            ManifestacaoInteresseGrupo manifestacao,
            GrupoEstudo oferta,
            Estudante interessado,
            CriarManifestacaoInteresseGrupoRequestDTO dto,
            StatusManifestacaoInteresse status,
            LocalDateTime dataManifestacao) {
        manifestacao.setGrupo(oferta);
        manifestacao.setInteressado(interessado);
        manifestacao.setStatus(status);
        manifestacao.setMensagem(dto.mensagem());
        manifestacao.setDataManifestacao(dataManifestacao);
    }

    @Override
    protected void aplicarResposta(
            ManifestacaoInteresseGrupo manifestacao,
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
        return manifestacaoRepository.existsByGrupo_IdAndInteressado_IdAndStatusIn(
                ofertaId,
                interessadoId,
                statusAtivos);
    }

    @Override
    protected List<ManifestacaoInteresseGrupo> buscarPorOfertaOrdenado(UUID ofertaId) {
        return manifestacaoRepository.findByGrupo_IdOrderByDataManifestacaoDesc(ofertaId);
    }

    @Override
    protected List<ManifestacaoInteresseGrupo> buscarPorInteressadoOrdenado(UUID interessadoId) {
        return manifestacaoRepository.findByInteressado_IdOrderByDataManifestacaoDesc(interessadoId);
    }

    @Override
    protected List<ManifestacaoInteresseGrupo> buscarPorOfertaEStatus(
            UUID ofertaId,
            StatusManifestacaoInteresse status) {
        return manifestacaoRepository.findByGrupo_IdAndStatus(ofertaId, status);
    }

    @Override
    protected UUID publicadorId(ManifestacaoInteresseGrupo manifestacao) {
        return manifestacao.getGrupo().getPublicadorId();
    }

    @Override
    protected ManifestacaoInteresseGrupoResponseDTO mapearResumo(ManifestacaoInteresseGrupo manifestacao) {
        return manifestacaoMapper.toResponseDTO(manifestacao);
    }

    @Override
    protected ManifestacaoInteresseGrupoResponseDTO mapearDetalhe(ManifestacaoInteresseGrupo manifestacao) {
        return manifestacaoMapper.toResponseDTO(manifestacao);
    }

    @Override
    protected RuntimeException erroManifestacaoNaoEncontrada(UUID id) {
        return new ManifestacaoInteresseNaoEncontradaException(
                "Manifestacao de interesse nao encontrada com id: " + id);
    }

    @Override
    protected RuntimeException erroOfertaInativa() {
        return new GrupoEstudoInativoException(
                "Nao e possivel manifestar interesse em um grupo que nao esta ativo.");
    }

    @Override
    protected RuntimeException erroInteresseProprio() {
        return new ManifestacaoInteresseInvalidaException(
                "Nao e possivel manifestar interesse no proprio grupo de estudo.");
    }

    @Override
    protected RuntimeException erroManifestacaoDuplicada() {
        return new ManifestacaoInteresseDuplicadaException(
                "Ja existe manifestacao de interesse ativa deste estudante para este grupo.");
    }

    @Override
    protected RuntimeException erroAcessoNegado() {
        return new AcessoNegadoException(
                "Estudante nao tem permissao para acessar esta manifestacao de interesse.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoResposta() {
        return new AcessoNegadoException(
                "Estudante nao tem permissao para responder esta manifestacao de interesse.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoCancelamento() {
        return new AcessoNegadoException(
                "Estudante nao tem permissao para cancelar esta manifestacao de interesse.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoListagemOferta() {
        return new AcessoNegadoException(
                "Estudante nao tem permissao para visualizar as manifestacoes deste grupo.");
    }

    @Override
    protected RuntimeException erroAcessoNegadoVisualizacao() {
        return new AcessoNegadoException(
                "Estudante nao tem permissao para visualizar esta manifestacao de interesse.");
    }

    @Override
    protected RuntimeException erroTransicaoInvalida() {
        return new TransicaoInvalidaManifestacaoException(
                "Transicao invalida: manifestacao nao esta em PENDENTE.");
    }
}
