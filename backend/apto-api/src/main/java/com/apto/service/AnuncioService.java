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
import com.apto.exception.AnuncioNaoEncontradoException;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.MoradiaAssociadaComAnuncioException;
import com.apto.exception.MoradiaNaoEncontradaException;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.enums.StatusAnuncio;
import com.apto.observer.DomainEventPublisher;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.ManifestacaoInteresseRepository;
import com.apto.repository.MoradiaRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final MoradiaRepository moradiaRepository;
    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final ManifestacaoInteresseRepository manifestacaoRepository;
    private final DomainEventPublisher eventPublisher;

    public AnuncioService(AnuncioRepository anuncioRepository,
                          MoradiaRepository moradiaRepository,
                          PerfilAnuncianteRepository perfilAnuncianteRepository,
                          ManifestacaoInteresseRepository manifestacaoRepository,
                          DomainEventPublisher eventPublisher) {
        this.anuncioRepository = anuncioRepository;
        this.moradiaRepository = moradiaRepository;
        this.perfilAnuncianteRepository = perfilAnuncianteRepository;
        this.manifestacaoRepository = manifestacaoRepository;
        this.eventPublisher = eventPublisher;
    }

    public AnuncioResponseDTO criar(CriarAnuncioRequestDTO dto) {
        // Busca direta pelo perfil — independente se é Locador ou Universitário
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

        return toResponseDTO(anuncioRepository.save(anuncio));
    }

    public List<AnuncioResponseDTO> listarTodos() {
        return anuncioRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AnuncioResponseDTO buscarPorId(UUID id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    public Anuncio buscarEntidadePorId(UUID id) {
        return anuncioRepository.findById(id)
                .orElseThrow(() -> new AnuncioNaoEncontradoException(
                        "Anúncio não encontrado com o id: " + id));
    }

    public void deletar(UUID id) {
        Anuncio anuncio = buscarEntidadePorId(id);
        StatusAnuncio statusAnterior = anuncio.getStatus();

        if (manifestacaoRepository.existsByAnuncio_Id(id)) {
            anuncio.setStatus(StatusAnuncio.ENCERRADO);
            anuncioRepository.save(anuncio);
            eventPublisher.publish(new AnuncioIndisponibilizadoEvent(
                    anuncio.getId(),
                    statusAnterior,
                    StatusAnuncio.ENCERRADO,
                    MotivoIndisponibilizacaoAnuncio.DELETADO));
            return;
        }

        anuncioRepository.delete(anuncio);
    }

    public AnuncioResponseDTO atualizar(UUID id, UUID usuarioId, AtualizarAnuncioRequestDTO dto) {
        Anuncio anuncio = buscarEntidadePorId(id);

        // Compara o usuário dono do perfil, não o perfil diretamente
        if (!anuncio.getAnuncianteUsuarioId().equals(usuarioId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para editar este anúncio.");
        }

        anuncio.setTitulo(dto.titulo());
        anuncio.setDescricao(dto.descricao());
        anuncio.setValorMensal(dto.valorMensal());
        return toResponseDTO(anuncioRepository.save(anuncio));
    }

    public AnuncioResponseDTO atualizarStatus(UUID id, StatusAnuncio status) {
        Anuncio anuncio = buscarEntidadePorId(id);
        StatusAnuncio statusAnterior = anuncio.getStatus();
        anuncio.setStatus(status);
        Anuncio salvo = anuncioRepository.save(anuncio);
        publicarIndisponibilizacaoSeNecessario(salvo, statusAnterior, status);
        return toResponseDTO(salvo);
    }

    public PaginaResponseDTO<BuscaAnuncioResponseDTO> buscarAnuncios(
            FiltroBuscaAnuncioDTO filtro, Pageable pageable) {

        Page<Anuncio> pagina = anuncioRepository.buscarComFiltros(
                filtro.valorMin(), filtro.valorMax(), filtro.bairro(),
                filtro.tipoMoradia(), filtro.tipoAnuncio(), filtro.mobiliado(),
                filtro.aceitaAnimais(), filtro.quantidadeVagas(), pageable);

        List<BuscaAnuncioResponseDTO> conteudo = pagina.getContent()
                .stream()
                .map(this::toBuscaResponseDTO)
                .toList();

        return new PaginaResponseDTO<>(
                conteudo,
                pagina.getNumber(),
                pagina.getTotalPages(),
                pagina.getTotalElements(),
                pagina.getSize());
    }

    private BuscaAnuncioResponseDTO toBuscaResponseDTO(Anuncio anuncio) {
        Moradia moradia = anuncio.getMoradia();
        return new BuscaAnuncioResponseDTO(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getValorMensal(),
                anuncio.getTipoAnuncio(),
                anuncio.getStatus(),
                anuncio.getDataPublicacao(),
                moradia.getId(),
                moradia.getTipoMoradia(),
                moradia.getBairro(),
                moradia.getEnderecoResumo(),
                moradia.isMobiliado(),
                moradia.isAceitaAnimais(),
                moradia.getQuantidadeVagas(),
                anuncio.getAnuncianteNome()
        );
    }

    private void publicarIndisponibilizacaoSeNecessario(
            Anuncio anuncio,
            StatusAnuncio statusAnterior,
            StatusAnuncio statusNovo) {
        if (statusAnterior == statusNovo || statusNovo == StatusAnuncio.ATIVO) {
            return;
        }

        MotivoIndisponibilizacaoAnuncio motivo = statusNovo == StatusAnuncio.PAUSADO
                ? MotivoIndisponibilizacaoAnuncio.PAUSADO
                : MotivoIndisponibilizacaoAnuncio.ENCERRADO;

        eventPublisher.publish(new AnuncioIndisponibilizadoEvent(
                anuncio.getId(),
                statusAnterior,
                statusNovo,
                motivo));
    }

    private AnuncioResponseDTO toResponseDTO(Anuncio anuncio) {
        return new AnuncioResponseDTO(
                anuncio.getId(),
                anuncio.getTitulo(),
                anuncio.getDescricao(),
                anuncio.getValorMensal(),
                anuncio.getTipoAnuncio(),
                anuncio.getStatus(),
                anuncio.getDataPublicacao(),
                anuncio.getAnuncianteUsuarioId(),
                anuncio.getMoradia().getId()
        );
    }
}
