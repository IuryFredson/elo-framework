package com.apto.service;

import com.apto.dto.response.ReputacaoAnuncianteResponseDTO;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.mapper.ReputacaoAnuncianteMapper;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.ReputacaoAnunciante;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.ReputacaoAnuncianteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReputacaoCalculoService {

    private final ReputacaoAnuncianteRepository reputacaoRepository;
    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ReputacaoAnuncianteMapper reputacaoMapper;

    public ReputacaoCalculoService(ReputacaoAnuncianteRepository reputacaoRepository,
                                   PerfilAnuncianteRepository perfilAnuncianteRepository,
                                   AvaliacaoRepository avaliacaoRepository,
                                   ReputacaoAnuncianteMapper reputacaoMapper) {
        this.reputacaoRepository = reputacaoRepository;
        this.perfilAnuncianteRepository = perfilAnuncianteRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.reputacaoMapper = reputacaoMapper;
    }

    public ReputacaoAnuncianteResponseDTO buscarPorAnunciante(UUID perfilAnuncianteId) {
        PerfilAnunciante perfil = perfilAnuncianteRepository.findById(perfilAnuncianteId)
                .orElseThrow(() -> new AnuncianteNaoEncontradoException(
                        "Perfil de anunciante não encontrado com id: " + perfilAnuncianteId));

        ReputacaoAnunciante reputacao = reputacaoRepository
                .findByPerfilAnunciante(perfil)
                .orElseGet(() -> reputacaoPadrao(perfil));

        return reputacaoMapper.toResponseDTO(reputacao);
    }

    public void calcularReputacaoEAtualizar(UUID perfilAnuncianteId) {
        PerfilAnunciante perfil = perfilAnuncianteRepository.findById(perfilAnuncianteId)
                .orElseThrow(() -> new AnuncianteNaoEncontradoException(
                        "Perfil de anunciante não encontrado com id: " + perfilAnuncianteId));

        List<Avaliacao> avaliacoes =
                avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId);

        ReputacaoAnunciante reputacao = reputacaoRepository
                .findByPerfilAnunciante(perfil)
                .orElseGet(() -> {
                    ReputacaoAnunciante nova = new ReputacaoAnunciante();
                    nova.setPerfilAnunciante(perfil);
                    return nova;
                });

        if (avaliacoes.isEmpty()) {
            zerarReputacao(reputacao);
            reputacaoRepository.save(reputacao);
            return;
        }

        int total = avaliacoes.size();
        double somaGeral = 0, somaFidelidade = 0, somaComunicacao = 0,
                somaEstado = 0, somaCusto = 0;

        for (Avaliacao av : avaliacoes) {
            somaGeral       += av.getNotaGeral();
            somaFidelidade  += av.getNotaFidelidadeAnuncio();
            somaComunicacao += av.getNotaComunicacao();
            somaEstado      += av.getNotaEstadoMoradia();
            somaCusto       += av.getNotaCustoBeneficio();
        }

        double mediaGeral       = somaGeral       / total;
        double mediaFidelidade  = somaFidelidade  / total;
        double mediaComunicacao = somaComunicacao / total;
        double mediaEstado      = somaEstado      / total;
        double mediaCusto       = somaCusto       / total;

        // Score ponderado (pesos: geral 30%, fidelidade 25%, comunicação 20%,
        //                         estado 15%, custo-benefício 10%)
        double scorePonderado = (mediaGeral       * 0.30)
                + (mediaFidelidade  * 0.25)
                + (mediaComunicacao * 0.20)
                + (mediaEstado      * 0.15)
                + (mediaCusto       * 0.10);

        // Média Bayesiana: suaviza notas de anunciantes com poucas avaliações
        double constanteSuavizacao = 5.0;
        double mediaGlobal = calcularMediaGlobal();
        double reputacaoScore = (total * scorePonderado + constanteSuavizacao * mediaGlobal)
                / (total + constanteSuavizacao);

        reputacao.setReputacaoScore(reputacaoScore);
        reputacao.setTotalAvaliacoes(total);
        reputacao.setMediaGeral(mediaGeral);
        reputacao.setMediaComunicacao(mediaComunicacao);
        reputacao.setMediaFidelidadeAnuncio(mediaFidelidade);
        reputacao.setMediaEstadoMoradia(mediaEstado);
        reputacao.setMediaCustoBeneficio(mediaCusto);
        reputacao.setUltimaAtualizacao(LocalDateTime.now());

        reputacaoRepository.save(reputacao);
    }

    private void zerarReputacao(ReputacaoAnunciante reputacao) {
        reputacao.setReputacaoScore(0.0);
        reputacao.setTotalAvaliacoes(0);
        reputacao.setMediaGeral(0.0);
        reputacao.setMediaComunicacao(0.0);
        reputacao.setMediaFidelidadeAnuncio(0.0);
        reputacao.setMediaEstadoMoradia(0.0);
        reputacao.setMediaCustoBeneficio(0.0);
        reputacao.setUltimaAtualizacao(LocalDateTime.now());
    }

    private ReputacaoAnunciante reputacaoPadrao(PerfilAnunciante perfil) {
        ReputacaoAnunciante r = new ReputacaoAnunciante();
        r.setPerfilAnunciante(perfil);
        r.setReputacaoScore(0.0);
        r.setTotalAvaliacoes(0);
        r.setMediaGeral(0.0);
        r.setMediaComunicacao(0.0);
        r.setMediaFidelidadeAnuncio(0.0);
        r.setMediaEstadoMoradia(0.0);
        r.setMediaCustoBeneficio(0.0);
        r.setUltimaAtualizacao(null);
        return r;
    }

    private double calcularMediaGlobal() {
        List<ReputacaoAnunciante> reputacoes = reputacaoRepository.findAll();
        if (reputacoes.isEmpty()) return 3.5;
        return reputacoes.stream()
                .mapToDouble(ReputacaoAnunciante::getReputacaoScore)
                .average()
                .orElse(3.5);
    }

}
