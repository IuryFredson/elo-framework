package com.apto.service;

import com.apto.dto.request.AtualizarAnuncioRequestDTO;
import com.apto.dto.request.CriarAnuncioRequestDTO;
import com.apto.dto.request.FiltroBuscaAnuncioDTO;
import com.apto.dto.response.AnuncioResponseDTO;
import com.apto.dto.response.BuscaAnuncioResponseDTO;
import com.apto.dto.response.PaginaResponseDTO;
import com.apto.event.AnuncioIndisponibilizadoEvent;
import com.apto.event.MotivoIndisponibilizacaoAnuncio;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.AnuncioNaoEncontradoException;
import com.apto.exception.MoradiaAssociadaComAnuncioException;
import com.apto.exception.MoradiaNaoEncontradaException;
import com.apto.mapper.AnuncioMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.enums.StatusAnuncio;
import com.apto.observer.DomainEventPublisher;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.MoradiaRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.elo.oferta.OfertaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AnuncioService extends OfertaService<
        Anuncio,
        CriarAnuncioRequestDTO,
        AtualizarAnuncioRequestDTO,
        AnuncioResponseDTO,
        StatusAnuncio> {

    private final AnuncioRepository anuncioRepository;
    private final MoradiaRepository moradiaRepository;
    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final ManifestacaoInteresseRepository manifestacaoRepository;
    private final DomainEventPublisher eventPublisher;
    private final AnuncioMapper anuncioMapper;

    public AnuncioService(AnuncioRepository anuncioRepository,
                          MoradiaRepository moradiaRepository,
                          PerfilAnuncianteRepository perfilAnuncianteRepository,
                          ManifestacaoInteresseRepository manifestacaoRepository,
                          DomainEventPublisher eventPublisher,
                          AnuncioMapper anuncioMapper) {
        this.anuncioRepository = anuncioRepository;
        this.moradiaRepository = moradiaRepository;
        this.perfilAnuncianteRepository = perfilAnuncianteRepository;
        this.manifestacaoRepository = manifestacaoRepository;
        this.eventPublisher = eventPublisher;
        this.anuncioMapper = anuncioMapper;
    }

    @Override
    protected AnuncioRepository repositorio() {
        return anuncioRepository;
    }

    @Override
    protected AnuncioMapper mapperResposta() {
        return anuncioMapper;
    }

    @Override
    protected RuntimeException erroOfertaNaoEncontrada(UUID id) {
        return new AnuncioNaoEncontradoException("Anúncio não encontrado com o id: " + id);
    }

    @Override
    protected Anuncio construirOferta(CriarAnuncioRequestDTO dto) {
        PerfilAnunciante anunciante = perfilAnuncianteRepository
                .findByUsuario_Id(dto.anuncianteId())
                .orElseThrow(() -> new AnuncianteNaoEncontradoException(
                        "Perfil de anunciante não encontrado para o usuário: " + dto.anuncianteId()));

        if (!anunciante.isAtivo()) {
            throw new AnuncianteNaoEncontradoException(
                    "O perfil de anunciante está inativo para o usuário: " + dto.anuncianteId());
        }

        Moradia moradia = moradiaRepository.findById(dto.moradiaId())
                .orElseThrow(() -> new MoradiaNaoEncontradaException(
                        "Moradia não encontrada com id " + dto.moradiaId()));

        if (anuncioRepository.existsByMoradia(moradia)) {
            throw new MoradiaAssociadaComAnuncioException(
                    "A moradia já está associada com um anúncio.");
        }

        Anuncio anuncio = new Anuncio();
        anuncio.setAnunciante(anunciante);
        anuncio.setTitulo(dto.titulo());
        anuncio.setDescricao(dto.descricao());
        anuncio.setValorMensal(dto.valorMensal());
        anuncio.setTipoAnuncio(dto.tipoAnuncio());
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setMoradia(moradia);
        anuncio.setDataPublicacao(LocalDate.now());
        return anuncio;
    }

    @Override
    protected void validarAtualizacao(
            Anuncio anuncio,
            UUID publicadorId,
            AtualizarAnuncioRequestDTO dto) {
        if (!anuncio.getAnuncianteUsuarioId().equals(publicadorId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para editar este anúncio.");
        }
    }

    @Override
    protected void aplicarAtualizacao(Anuncio anuncio, AtualizarAnuncioRequestDTO dto) {
        anuncio.setTitulo(dto.titulo());
        anuncio.setDescricao(dto.descricao());
        anuncio.setValorMensal(dto.valorMensal());
    }

    @Override
    protected StatusAnuncio obterStatus(Anuncio anuncio) {
        return anuncio.getStatus();
    }

    @Override
    protected void aplicarStatus(Anuncio anuncio, StatusAnuncio status) {
        anuncio.setStatus(status);
    }

    @Override
    protected void aposAlterarStatus(
            Anuncio anuncio,
            StatusAnuncio statusAnterior,
            StatusAnuncio novoStatus) {
        if (statusAnterior == novoStatus || novoStatus == StatusAnuncio.ATIVO) {
            return;
        }

        MotivoIndisponibilizacaoAnuncio motivo = novoStatus == StatusAnuncio.PAUSADO
                ? MotivoIndisponibilizacaoAnuncio.PAUSADO
                : MotivoIndisponibilizacaoAnuncio.ENCERRADO;

        publicarIndisponibilizacao(anuncio, statusAnterior, novoStatus, motivo);
    }

    @Override
    protected boolean deveExcluirFisicamente(Anuncio anuncio) {
        return !manifestacaoRepository.existsByAnuncio_Id(anuncio.getId());
    }

    @Override
    protected void aplicarExclusaoLogica(Anuncio anuncio) {
        anuncio.setStatus(StatusAnuncio.ENCERRADO);
    }

    @Override
    protected void aposExcluirLogicamente(
            Anuncio anuncio,
            StatusAnuncio statusAnterior,
            StatusAnuncio novoStatus) {
        publicarIndisponibilizacao(
                anuncio,
                statusAnterior,
                novoStatus,
                MotivoIndisponibilizacaoAnuncio.DELETADO);
    }

    public PaginaResponseDTO<BuscaAnuncioResponseDTO> buscarAnuncios(
            FiltroBuscaAnuncioDTO filtro,
            Pageable pageable) {
        Page<Anuncio> pagina = anuncioRepository.buscarComFiltros(
                filtro.valorMin(), filtro.valorMax(), filtro.bairro(),
                filtro.tipoMoradia(), filtro.tipoAnuncio(), filtro.mobiliado(),
                filtro.aceitaAnimais(), filtro.quantidadeVagas(), pageable);

        List<BuscaAnuncioResponseDTO> conteudo = pagina.getContent()
                .stream()
                .map(anuncioMapper::toBuscaResponseDTO)
                .toList();

        return new PaginaResponseDTO<>(
                conteudo,
                pagina.getNumber(),
                pagina.getTotalPages(),
                pagina.getTotalElements(),
                pagina.getSize());
    }

    private void publicarIndisponibilizacao(
            Anuncio anuncio,
            StatusAnuncio statusAnterior,
            StatusAnuncio novoStatus,
            MotivoIndisponibilizacaoAnuncio motivo) {
        eventPublisher.publish(new AnuncioIndisponibilizadoEvent(
                anuncio.getId(),
                statusAnterior,
                novoStatus,
                motivo));
    }
}
