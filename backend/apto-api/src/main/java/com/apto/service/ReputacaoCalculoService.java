package com.apto.service;

import com.apto.dto.response.ReputacaoLocadorResponseDTO;
import com.apto.exception.LocadorNaoEncontradoException;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import com.apto.model.entity.ReputacaoLocador;
import com.apto.repository.*;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReputacaoCalculoService {

    private final ReputacaoLocadorRepository reputacaoLocadorRepository;
    private final LocadorRepository locadorRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public ReputacaoCalculoService(ReputacaoLocadorRepository reputacaoLocadorRepository,
                                   LocadorRepository locadorRepository, AvaliacaoRepository avaliacaoRepository) {
        this.reputacaoLocadorRepository = reputacaoLocadorRepository;
        this.locadorRepository = locadorRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    private ReputacaoLocador reputacaoLocadorPadrao(Locador locador) {
        ReputacaoLocador reputacao = new ReputacaoLocador();
        reputacao.setLocador(locador);
        reputacao.setReputacaoScore(0.0);
        reputacao.setTotalAvaliacoes(0);
        reputacao.setMediaGeral(0.0);
        reputacao.setMediaComunicacao(0.0);
        reputacao.setMediaFidelidadeAnuncio(0.0);
        reputacao.setMediaEstadoMoradia(0.0);
        reputacao.setMediaCustoBeneficio(0.0);
        reputacao.setUltimaAtualizacao(null);
        return reputacao;
    }

    public ReputacaoLocadorResponseDTO buscarPorLocador(UUID locadorId) {
        Locador locador = locadorRepository.findById(locadorId)
                .orElseThrow(() -> new LocadorNaoEncontradoException("Locador não encontrado com id " + locadorId));

        ReputacaoLocador reputacao = reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador)
                .orElseGet(() -> reputacaoLocadorPadrao(locador));

        return toResponseDTO(reputacao);
    }

    public void calcularReputacaoEAtualizar(UUID locadorId) {
        Locador locador = locadorRepository.findById(locadorId)
                .orElseThrow(() -> new LocadorNaoEncontradoException("Locador não encontrado com id " + locadorId));

        List<Avaliacao> avaliacoes = avaliacaoRepository.findByLocadorAvaliadoAndAtivaTrue(locador);

        ReputacaoLocador reputacao = reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador)
                .orElseGet(() -> {
                    ReputacaoLocador nova = new ReputacaoLocador();
                    nova.setLocador(locador);
                    return nova;
                });

        if (avaliacoes.isEmpty()) {
            reputacao.setReputacaoScore(0.0);
            reputacao.setTotalAvaliacoes(0);
            reputacao.setMediaGeral(0.0);
            reputacao.setMediaComunicacao(0.0);
            reputacao.setMediaFidelidadeAnuncio(0.0);
            reputacao.setMediaEstadoMoradia(0.0);
            reputacao.setMediaCustoBeneficio(0.0);
            reputacao.setUltimaAtualizacao(LocalDateTime.now());
            reputacaoLocadorRepository.save(reputacao);
            return;
        }

        int total = avaliacoes.size();
        double somaGeral =0, somaFidelidade = 0, somaComunicacao = 0, somaEstado = 0, somaCusto = 0;

        for (Avaliacao avaliacao : avaliacoes) {
            somaGeral += avaliacao.getNotaGeral();
            somaFidelidade += avaliacao.getNotaFidelidadeAnuncio();
            somaComunicacao += avaliacao.getNotaComunicacao();
            somaEstado += avaliacao.getNotaEstadoMoradia();
            somaCusto += avaliacao.getNotaCustoBeneficio();
        }

        double mediaGeral = somaGeral / total;
        double mediaFidelidade = somaFidelidade / total;
        double mediaComunicacao = somaComunicacao / total;
        double mediaEstado = somaEstado / total;
        double mediaCusto = somaCusto / total;

        //score baseado em pesos (peso 3, 2.5, 2, 1.5 e 1, respectivamente)
        double scorePonderado = (mediaGeral * 0.30)
                + (mediaFidelidade * 0.25)
                + (mediaComunicacao * 0.20)
                + (mediaEstado * 0.15)
                + (mediaCusto * 0.10);

        double constanteSuavizacao = 5.0;
        double mediaGlobal = calcularMediaGlobal();

        //Aplicação de média Bayesiana, misturando a nota com a média global de reputações
        double reputacaoScore = (total * scorePonderado + constanteSuavizacao * mediaGlobal) / (total + constanteSuavizacao);

        reputacao.setReputacaoScore(reputacaoScore);
        reputacao.setTotalAvaliacoes(total);
        reputacao.setMediaGeral(mediaGeral);
        reputacao.setMediaComunicacao(mediaComunicacao);
        reputacao.setMediaFidelidadeAnuncio(mediaFidelidade);
        reputacao.setMediaEstadoMoradia(mediaEstado);
        reputacao.setMediaCustoBeneficio(mediaCusto);
        reputacao.setUltimaAtualizacao(LocalDateTime.now());

        reputacaoLocadorRepository.save(reputacao);
    }

    private double calcularMediaGlobal() {
        List<ReputacaoLocador> reputacoes = reputacaoLocadorRepository.findAll();
        if (reputacoes.isEmpty()) return 3.5;

        return reputacoes.stream()
                .mapToDouble(ReputacaoLocador::getReputacaoScore)
                .average()
                .orElse(3.5);
    }

    private ReputacaoLocadorResponseDTO toResponseDTO(ReputacaoLocador reputacao) {
        return new ReputacaoLocadorResponseDTO(
                reputacao.getId(),
                reputacao.getLocador().getId(),
                reputacao.getReputacaoScore(),
                reputacao.getTotalAvaliacoes(),
                reputacao.getMediaGeral(),
                reputacao.getMediaComunicacao(),
                reputacao.getMediaFidelidadeAnuncio(),
                reputacao.getMediaEstadoMoradia(),
                reputacao.getMediaCustoBeneficio(),
                reputacao.getUltimaAtualizacao()
        );
    }

}

