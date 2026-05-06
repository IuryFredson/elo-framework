package com.apto.service;

import com.apto.dto.response.ReputacaoLocadorResponseDTO;
import com.apto.exception.LocadorNaoEncontradoException;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import com.apto.model.entity.ReputacaoLocador;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.ReputacaoLocadorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReputacaoCalculoServiceTest {

    @Mock
    private ReputacaoLocadorRepository reputacaoLocadorRepository;

    @Mock
    private LocadorRepository locadorRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private ReputacaoCalculoService reputacaoCalculoService;

    private UUID locadorId;
    private Locador locador;
    private ReputacaoLocador reputacaoExistente;

    @BeforeEach
    void setUp() {
        locadorId = UUID.randomUUID();

        locador = new Locador();
        locador.setId(locadorId);
        locador.setNome("Carlos Locador");
        locador.setEmail("carlos@email.com");
        locador.setDocumentoIdentificacao("12345678900");
        locador.setNomeExibicaoOuRazao("Carlos Imóveis");

        reputacaoExistente = new ReputacaoLocador();
        reputacaoExistente.setId(UUID.randomUUID());
        reputacaoExistente.setLocador(locador);
        reputacaoExistente.setReputacaoScore(3.5);
        reputacaoExistente.setTotalAvaliacoes(2);
        reputacaoExistente.setMediaGeral(4.0);
        reputacaoExistente.setMediaComunicacao(3.5);
        reputacaoExistente.setMediaFidelidadeAnuncio(4.0);
        reputacaoExistente.setMediaEstadoMoradia(3.5);
        reputacaoExistente.setMediaCustoBeneficio(3.0);
    }

    private Avaliacao criarAvaliacao(int geral, int comunicacao, int fidelidade, int estado, int custo) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNotaGeral(geral);
        avaliacao.setNotaComunicacao(comunicacao);
        avaliacao.setNotaFidelidadeAnuncio(fidelidade);
        avaliacao.setNotaEstadoMoradia(estado);
        avaliacao.setNotaCustoBeneficio(custo);
        avaliacao.setAtiva(true);
        avaliacao.setLocadorAvaliado(locador);
        return avaliacao;
    }

    @Test
    void deveBuscarReputacaoExistentePorLocador() {
        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.of(reputacaoExistente));

        ReputacaoLocadorResponseDTO response = reputacaoCalculoService.buscarPorLocador(locadorId);

        assertNotNull(response);
        assertEquals(locadorId, response.locadorId());
        assertEquals(3.5, response.reputacaoScore());
    }

    @Test
    void deveRetornarReputacaoPadraoSeLocadorSemReputacao() {
        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.empty());

        ReputacaoLocadorResponseDTO response = reputacaoCalculoService.buscarPorLocador(locadorId);

        assertNotNull(response);
        assertEquals(0.0, response.reputacaoScore());
        assertEquals(0, response.totalAvaliacoes());
    }

    @Test
    void naoDeveBuscarReputacaoSeLocadorNaoExistir() {
        when(locadorRepository.findById(locadorId)).thenReturn(Optional.empty());

        assertThrows(LocadorNaoEncontradoException.class,
                () -> reputacaoCalculoService.buscarPorLocador(locadorId));
    }

    @Test
    void deveCalcularECriarReputacaoParaLocadorSemReputacaoPrevia() {
        Avaliacao a1 = criarAvaliacao(5, 5, 5, 5, 5);
        Avaliacao a2 = criarAvaliacao(3, 3, 3, 3, 3);

        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of(a1, a2));
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.empty());
        when(reputacaoLocadorRepository.findAll()).thenReturn(List.of());
        when(reputacaoLocadorRepository.save(any(ReputacaoLocador.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(locadorId);

        verify(reputacaoLocadorRepository).save(any(ReputacaoLocador.class));
    }

    @Test
    void deveAtualizarReputacaoExistente() {
        Avaliacao a1 = criarAvaliacao(5, 5, 5, 5, 5);

        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of(a1));
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.of(reputacaoExistente));
        when(reputacaoLocadorRepository.findAll()).thenReturn(List.of(reputacaoExistente));
        when(reputacaoLocadorRepository.save(any(ReputacaoLocador.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(locadorId);

        verify(reputacaoLocadorRepository).save(reputacaoExistente);
    }

    @Test
    void deveZerarReputacaoSeNaoHouverAvaliacoesAtivas() {
        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of());
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.of(reputacaoExistente));

        reputacaoCalculoService.calcularReputacaoEAtualizar(locadorId);

        verify(reputacaoLocadorRepository).save(reputacaoExistente);
        assertEquals(0.0, reputacaoExistente.getReputacaoScore());
        assertEquals(0, reputacaoExistente.getTotalAvaliacoes());
    }

    @Test
    void naoDeveCalcularSeLocadorNaoExistir() {
        when(locadorRepository.findById(locadorId)).thenReturn(Optional.empty());

        assertThrows(LocadorNaoEncontradoException.class,
                () -> reputacaoCalculoService.calcularReputacaoEAtualizar(locadorId));
        verify(reputacaoLocadorRepository, never()).save(any());
    }

    @Test
    void deveAplicarPesosCorretamentNoScorePonderado() {
        // notas todas iguais a 4 entao score ponderado deve ser exatamente 4.0
        Avaliacao a1 = criarAvaliacao(4, 4, 4, 4, 4);

        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of(a1));
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.of(reputacaoExistente));
        when(reputacaoLocadorRepository.findAll()).thenReturn(List.of());
        when(reputacaoLocadorRepository.save(any(ReputacaoLocador.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(locadorId);

        // com mediaGlobal = 3.5 (lista vazia), constanteSuavizacao = 5, total = 1:
        // reputacaoScore = (1 * 4.0 + 5 * 3.5) / (1 + 5) = 21.5 / 6 aproximadamente 3.583
        verify(reputacaoLocadorRepository).save(argThat(r ->
                r.getReputacaoScore() > 3.5 && r.getReputacaoScore() < 4.0
        ));
    }

    @Test
    void deveUsarMediaGlobalNaFormulaBayesiana() {
        // locador com nota máxima -> com poucos votos o score deve ser puxado para a média global, atendendo a media bayesiana
        Avaliacao a1 = criarAvaliacao(5, 5, 5, 5, 5);

        ReputacaoLocador outraReputacao = new ReputacaoLocador();
        outraReputacao.setReputacaoScore(2.0);

        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of(a1));
        when(reputacaoLocadorRepository.findReputacaoLocadorByLocador(locador))
                .thenReturn(Optional.of(reputacaoExistente));
        when(reputacaoLocadorRepository.findAll()).thenReturn(List.of(outraReputacao));
        when(reputacaoLocadorRepository.save(any(ReputacaoLocador.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(locadorId);

        // mediaGlobal = 2.0, scorePonderado = 5.0, total = 1, m = 5
        // reputacaoScore = (1 * 5.0 + 5 * 2.0) / 6 = 15/6 = 2.5
        verify(reputacaoLocadorRepository).save(argThat(r ->
                Math.abs(r.getReputacaoScore() - 2.5) < 0.001
        ));
    }
}