package com.apto.mapper;

import com.apto.dto.response.AvaliacaoResponseDTO;
import com.apto.dto.response.ResumoAvaliacoesAnuncianteResponseDTO;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.PerfilAnunciante;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.ToIntFunction;

@Component
public class AvaliacaoMapper {

    public AvaliacaoResponseDTO toResponseDTO(Avaliacao avaliacao) {
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

    public ResumoAvaliacoesAnuncianteResponseDTO toResumoAnuncianteDTO(
            PerfilAnunciante perfil,
            List<Avaliacao> avaliacoes) {
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

    private Double media(List<Avaliacao> avaliacoes, ToIntFunction<Avaliacao> campo) {
        if (avaliacoes.isEmpty()) return 0.0;
        return avaliacoes.stream().mapToInt(campo).average().orElse(0.0);
    }
}
