package com.apto.service;

import com.apto.dto.request.AtualizarAvaliacaoRequestDTO;
import com.apto.dto.request.CriarAvaliacaoRequestDTO;
import com.apto.dto.response.AvaliacaoResponseDTO;
import com.apto.dto.response.ResumoAvaliacoesAnuncianteResponseDTO;
import com.apto.event.AvaliacaoAlteradaEvent;
import com.apto.event.TipoOperacaoAvaliacao;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AvaliacaoDuplicadaException;
import com.apto.exception.AvaliacaoInvalidaException;
import com.apto.exception.AvaliacaoNaoEncontradaException;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import com.apto.observer.DomainEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioUniversitarioRepository universitarioRepository;
    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final AnuncioService anuncioService;
    private final DomainEventPublisher eventPublisher;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            UsuarioUniversitarioRepository universitarioRepository,
                            PerfilAnuncianteRepository perfilAnuncianteRepository,
                            AnuncioService anuncioService,
                            DomainEventPublisher eventPublisher) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.universitarioRepository = universitarioRepository;
        this.perfilAnuncianteRepository = perfilAnuncianteRepository;
        this.anuncioService = anuncioService;
        this.eventPublisher = eventPublisher;
    }

    public AvaliacaoResponseDTO criar(CriarAvaliacaoRequestDTO dto) {
        UsuarioUniversitario avaliador = universitarioRepository.findById(dto.avaliadorId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário universitário não encontrado com id: " + dto.avaliadorId()));

        Anuncio anuncio = anuncioService.buscarEntidadePorId(dto.anuncioId());
        PerfilAnunciante anuncianteAvaliado = anuncio.getAnunciante();

        // Universitário não pode avaliar anúncio que ele mesmo publicou
        if (anuncianteAvaliado.getUsuario().getId().equals(avaliador.getId())) {
            throw new AvaliacaoInvalidaException("Não é possível avaliar o próprio anúncio.");
        }

        // Não precisa mais de instanceof — qualquer anunciante pode ser avaliado
        if (avaliacaoRepository.existsByAvaliador_IdAndAnuncio_IdAndAtivaTrue(
                avaliador.getId(), anuncio.getId())) {
            throw new AvaliacaoDuplicadaException(
                    "Já existe uma avaliação ativa deste usuário para este anúncio.");
        }

        validarNotas(
                dto.notaGeral(),
                dto.notaComunicacao(),
                dto.notaFidelidadeAnuncio(),
                dto.notaEstadoMoradia(),
                dto.notaCustoBeneficio());

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvaliador(avaliador);
        avaliacao.setAnuncianteAvaliado(anuncianteAvaliado);
        avaliacao.setAnuncio(anuncio);
        avaliacao.setMoradia(anuncio.getMoradia());
        avaliacao.setNotaGeral(dto.notaGeral());
        avaliacao.setNotaComunicacao(dto.notaComunicacao());
        avaliacao.setNotaFidelidadeAnuncio(dto.notaFidelidadeAnuncio());
        avaliacao.setNotaEstadoMoradia(dto.notaEstadoMoradia());
        avaliacao.setNotaCustoBeneficio(dto.notaCustoBeneficio());
        avaliacao.setComentario(dto.comentario());
        avaliacao.setDataCriacao(LocalDateTime.now());
        avaliacao.setAtiva(true);

        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        publicarAvaliacaoAlterada(salva, TipoOperacaoAvaliacao.CRIADA);
        return toResponseDTO(salva);
    }

    public AvaliacaoResponseDTO buscarPorId(UUID id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    public List<AvaliacaoResponseDTO> listarPorAnunciante(UUID perfilAnuncianteId) {
        if (!perfilAnuncianteRepository.existsById(perfilAnuncianteId)) {
            throw new AnuncianteNaoEncontradoException(
                    "Perfil de anunciante não encontrado com id: " + perfilAnuncianteId);
        }
        return avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AvaliacaoResponseDTO> listarPorMoradia(UUID moradiaId) {
        return avaliacaoRepository.findByMoradia_IdAndAtivaTrue(moradiaId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AvaliacaoResponseDTO> listarPorAvaliador(UUID avaliadorId) {
        return avaliacaoRepository.findByAvaliador_IdAndAtivaTrue(avaliadorId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public AvaliacaoResponseDTO atualizar(UUID id, UUID avaliadorId, AtualizarAvaliacaoRequestDTO dto) {
        Avaliacao avaliacao = buscarEntidadePorId(id);
        validarDonoDaAvaliacao(avaliacao, avaliadorId);

        validarNotas(
                dto.notaGeral(),
                dto.notaComunicacao(),
                dto.notaFidelidadeAnuncio(),
                dto.notaEstadoMoradia(),
                dto.notaCustoBeneficio());

        avaliacao.setNotaGeral(dto.notaGeral());
        avaliacao.setNotaComunicacao(dto.notaComunicacao());
        avaliacao.setNotaFidelidadeAnuncio(dto.notaFidelidadeAnuncio());
        avaliacao.setNotaEstadoMoradia(dto.notaEstadoMoradia());
        avaliacao.setNotaCustoBeneficio(dto.notaCustoBeneficio());
        avaliacao.setComentario(dto.comentario());

        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        publicarAvaliacaoAlterada(salva, TipoOperacaoAvaliacao.ATUALIZADA);
        return toResponseDTO(salva);
    }

    public void desativar(UUID id, UUID avaliadorId) {
        Avaliacao avaliacao = buscarEntidadePorId(id);
        validarDonoDaAvaliacao(avaliacao, avaliadorId);
        avaliacao.setAtiva(false);
        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        publicarAvaliacaoAlterada(salva, TipoOperacaoAvaliacao.DESATIVADA);
    }

    public ResumoAvaliacoesAnuncianteResponseDTO resumoPorAnunciante(UUID perfilAnuncianteId) {
        PerfilAnunciante perfil = perfilAnuncianteRepository.findById(perfilAnuncianteId)
                .orElseThrow(() -> new AnuncianteNaoEncontradoException(
                        "Perfil de anunciante não encontrado com id: " + perfilAnuncianteId));

        List<Avaliacao> avaliacoes =
                avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId);

        return new ResumoAvaliacoesAnuncianteResponseDTO(
                perfil.getId(),
                perfil.getUsuario().getNome(),
                avaliacoes.size(),
                media(avaliacoes, Avaliacao::getNotaGeral),
                media(avaliacoes, Avaliacao::getNotaComunicacao),
                media(avaliacoes, Avaliacao::getNotaFidelidadeAnuncio),
                media(avaliacoes, Avaliacao::getNotaEstadoMoradia),
                media(avaliacoes, Avaliacao::getNotaCustoBeneficio));
    }

    private Avaliacao buscarEntidadePorId(UUID id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new AvaliacaoNaoEncontradaException(
                        "Avaliação não encontrada com id: " + id));
    }

    private void validarDonoDaAvaliacao(Avaliacao avaliacao, UUID avaliadorId) {
        if (!avaliacao.getAvaliador().getId().equals(avaliadorId)) {
            throw new AcessoNegadoException(
                    "Usuário não tem permissão para alterar esta avaliação.");
        }
    }

    private void validarNotas(Integer... notas) {
        for (Integer nota : notas) {
            if (nota == null || nota < 1 || nota > 5) {
                throw new AvaliacaoInvalidaException("As notas devem estar entre 1 e 5.");
            }
        }
    }

    private Double media(List<Avaliacao> avaliacoes, ToIntFunction<Avaliacao> campo) {
        if (avaliacoes.isEmpty()) return 0.0;
        return avaliacoes.stream().mapToInt(campo).average().orElse(0.0);
    }

    private void publicarAvaliacaoAlterada(Avaliacao avaliacao, TipoOperacaoAvaliacao tipoOperacao) {
        eventPublisher.publish(new AvaliacaoAlteradaEvent(
                avaliacao.getId(),
                avaliacao.getAnuncianteAvaliado().getId(),
                tipoOperacao));
    }

    private AvaliacaoResponseDTO toResponseDTO(Avaliacao avaliacao) {
        PerfilAnunciante anunciante = avaliacao.getAnuncianteAvaliado();
        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getAvaliador().getId(),
                avaliacao.getAvaliador().getNome(),
                anunciante.getId(),
                anunciante.getUsuario().getNome(),
                avaliacao.getMoradia().getId(),
                avaliacao.getAnuncio().getId(),
                avaliacao.getAnuncio().getTitulo(),
                avaliacao.getNotaGeral(),
                avaliacao.getNotaComunicacao(),
                avaliacao.getNotaFidelidadeAnuncio(),
                avaliacao.getNotaEstadoMoradia(),
                avaliacao.getNotaCustoBeneficio(),
                avaliacao.getComentario(),
                avaliacao.getDataCriacao(),
                avaliacao.getAtiva());
    }
}
