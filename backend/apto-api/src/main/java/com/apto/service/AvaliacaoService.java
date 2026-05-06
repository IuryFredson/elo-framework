package com.apto.service;

import com.apto.dto.request.AtualizarAvaliacaoRequestDTO;
import com.apto.dto.request.CriarAvaliacaoRequestDTO;
import com.apto.dto.response.AvaliacaoResponseDTO;
import com.apto.dto.response.ResumoAvaliacoesLocadorResponseDTO;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AvaliacaoDuplicadaException;
import com.apto.exception.AvaliacaoInvalidaException;
import com.apto.exception.AvaliacaoNaoEncontradaException;
import com.apto.exception.LocadorNaoEncontradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioUniversitarioRepository universitarioRepository;
    private final LocadorRepository locadorRepository;
    private final AnuncioService anuncioService;
    private final ReputacaoCalculoService reputacaoCalculoService;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            UsuarioUniversitarioRepository universitarioRepository,
                            LocadorRepository locadorRepository,
                            AnuncioService anuncioService, ReputacaoCalculoService reputacaoCalculoService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.universitarioRepository = universitarioRepository;
        this.locadorRepository = locadorRepository;
        this.anuncioService = anuncioService;
        this.reputacaoCalculoService = reputacaoCalculoService;
    }

    public AvaliacaoResponseDTO criar(CriarAvaliacaoRequestDTO dto) {
        UsuarioUniversitario avaliador = universitarioRepository.findById(dto.avaliadorId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário universitário não encontrado com id: " + dto.avaliadorId()));

        Anuncio anuncio = anuncioService.buscarEntidadePorId(dto.anuncioId());

        if (anuncio.getAnunciante().getId().equals(avaliador.getId())) {
            throw new AvaliacaoInvalidaException("Não é possível avaliar o próprio anúncio.");
        }

        if (!(anuncio.getAnunciante() instanceof Locador locador)) {
            throw new AvaliacaoInvalidaException("Só é possível avaliar anúncios de locadores.");
        }

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
                dto.notaCustoBeneficio()
        );

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvaliador(avaliador);
        avaliacao.setLocadorAvaliado(locador);
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

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        reputacaoCalculoService.calcularReputacaoEAtualizar(locador.getId());
        return toResponseDTO(avaliacaoSalva);
    }

    public AvaliacaoResponseDTO buscarPorId(UUID id) {
        return toResponseDTO(buscarEntidadePorId(id));
    }

    public List<AvaliacaoResponseDTO> listarPorLocador(UUID locadorId) {
        validarLocadorExiste(locadorId);
        return avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId)
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
                dto.notaCustoBeneficio()
        );

        avaliacao.setNotaGeral(dto.notaGeral());
        avaliacao.setNotaComunicacao(dto.notaComunicacao());
        avaliacao.setNotaFidelidadeAnuncio(dto.notaFidelidadeAnuncio());
        avaliacao.setNotaEstadoMoradia(dto.notaEstadoMoradia());
        avaliacao.setNotaCustoBeneficio(dto.notaCustoBeneficio());
        avaliacao.setComentario(dto.comentario());

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        reputacaoCalculoService.calcularReputacaoEAtualizar(avaliacao.getLocadorAvaliado().getId());
        return toResponseDTO(avaliacaoSalva);
    }

    public void desativar(UUID id, UUID avaliadorId) {
        Avaliacao avaliacao = buscarEntidadePorId(id);
        validarDonoDaAvaliacao(avaliacao, avaliadorId);
        avaliacao.setAtiva(false);
        avaliacaoRepository.save(avaliacao);
        reputacaoCalculoService.calcularReputacaoEAtualizar(avaliacao.getLocadorAvaliado().getId());
    }

    public ResumoAvaliacoesLocadorResponseDTO resumoPorLocador(UUID locadorId) {
        Locador locador = locadorRepository.findById(locadorId)
                .orElseThrow(() -> new LocadorNaoEncontradoException(
                        "Locador não encontrado com id: " + locadorId));

        List<Avaliacao> avaliacoes = avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId);

        return new ResumoAvaliacoesLocadorResponseDTO(
                locador.getId(),
                locador.getNome(),
                avaliacoes.size(),
                media(avaliacoes, Avaliacao::getNotaGeral),
                media(avaliacoes, Avaliacao::getNotaComunicacao),
                media(avaliacoes, Avaliacao::getNotaFidelidadeAnuncio),
                media(avaliacoes, Avaliacao::getNotaEstadoMoradia),
                media(avaliacoes, Avaliacao::getNotaCustoBeneficio)
        );
    }

    private Avaliacao buscarEntidadePorId(UUID id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new AvaliacaoNaoEncontradaException(
                        "Avaliação não encontrada com id: " + id));
    }

    private void validarLocadorExiste(UUID locadorId) {
        if (!locadorRepository.existsById(locadorId)) {
            throw new LocadorNaoEncontradoException("Locador não encontrado com id: " + locadorId);
        }
    }

    private void validarDonoDaAvaliacao(Avaliacao avaliacao, UUID avaliadorId) {
        if (!avaliacao.getAvaliador().getId().equals(avaliadorId)) {
            throw new AcessoNegadoException("Usuário não tem permissão para alterar esta avaliação.");
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
        if (avaliacoes.isEmpty()) {
            return 0.0;
        }
        return avaliacoes.stream()
                .mapToInt(campo)
                .average()
                .orElse(0.0);
    }

    private AvaliacaoResponseDTO toResponseDTO(Avaliacao avaliacao) {
        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getAvaliador().getId(),
                avaliacao.getAvaliador().getNome(),
                avaliacao.getLocadorAvaliado().getId(),
                avaliacao.getLocadorAvaliado().getNome(),
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
                avaliacao.getAtiva()
        );
    }
}
