package com.apto.service;

import com.apto.dto.response.ReputacaoAnuncianteResponseDTO;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.ReputacaoAnunciante;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.ReputacaoAnuncianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReputacaoCalculoServiceTest {

    @Mock
    private ReputacaoAnuncianteRepository reputacaoRepository;

    @Mock
    private PerfilAnuncianteRepository perfilAnuncianteRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @InjectMocks
    private ReputacaoCalculoService reputacaoCalculoService;

    private UUID perfilAnuncianteId;
    private Locador locador;
    private PerfilAnunciante perfilAnunciante;
    private ReputacaoAnunciante reputacaoExistente;

    @BeforeEach
    void setUp() {
        perfilAnuncianteId = UUID.randomUUID();

        locador = new Locador();
        locador.setId(UUID.randomUUID());
        locador.setNome("Carlos Locador");
        locador.setEmail("carlos@email.com");
        locador.setDocumentoIdentificacao("12345678900");
        locador.setNomeExibicaoOuRazao("Carlos Imóveis");

        perfilAnunciante = new PerfilAnunciante();
        perfilAnunciante.setId(perfilAnuncianteId);
        perfilAnunciante.setUsuario(locador);
        perfilAnunciante.setAtivo(true);

        reputacaoExistente = new ReputacaoAnunciante();
        reputacaoExistente.setId(UUID.randomUUID());
        reputacaoExistente.setPerfilAnunciante(perfilAnunciante);
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
        avaliacao.setAnuncianteAvaliado(perfilAnunciante); // era: setLocadorAvaliado
        return avaliacao;
    }

    @Test
    void deveBuscarReputacaoExistentePorAnunciante() {
        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.of(reputacaoExistente));

        ReputacaoAnuncianteResponseDTO response =
                reputacaoCalculoService.buscarPorAnunciante(perfilAnuncianteId);

        assertNotNull(response);
        assertEquals(perfilAnuncianteId, response.perfilAnuncianteId());
        assertEquals(3.5, response.reputacaoScore());
    }

    @Test
    void deveRetornarReputacaoPadraoSeAnuncianteSemReputacao() {
        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.empty());

        ReputacaoAnuncianteResponseDTO response =
                reputacaoCalculoService.buscarPorAnunciante(perfilAnuncianteId);

        assertNotNull(response);
        assertEquals(0.0, response.reputacaoScore());
        assertEquals(0, response.totalAvaliacoes());
    }

    @Test
    void naoDeveBuscarReputacaoSeAnuncianteNaoExistir() {
        when(perfilAnuncianteRepository.findById(perfilAnuncianteId)).thenReturn(Optional.empty());

        assertThrows(AnuncianteNaoEncontradoException.class,
                () -> reputacaoCalculoService.buscarPorAnunciante(perfilAnuncianteId));
    }

    @Test
    void deveCalcularECriarReputacaoParaAnuncianteSemReputacaoPrevia() {
        Avaliacao a1 = criarAvaliacao(5, 5, 5, 5, 5);
        Avaliacao a2 = criarAvaliacao(3, 3, 3, 3, 3);

        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of(a1, a2));
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.empty());
        when(reputacaoRepository.findAll()).thenReturn(List.of());
        when(reputacaoRepository.save(any(ReputacaoAnunciante.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(perfilAnuncianteId);

        verify(reputacaoRepository).save(any(ReputacaoAnunciante.class));
    }

    @Test
    void deveAtualizarReputacaoExistente() {
        Avaliacao a1 = criarAvaliacao(5, 5, 5, 5, 5);

        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of(a1));
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.of(reputacaoExistente));
        when(reputacaoRepository.findAll()).thenReturn(List.of(reputacaoExistente));
        when(reputacaoRepository.save(any(ReputacaoAnunciante.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(perfilAnuncianteId);

        verify(reputacaoRepository).save(reputacaoExistente);
    }

    @Test
    void deveZerarReputacaoSeNaoHouverAvaliacoesAtivas() {
        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of());
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.of(reputacaoExistente));

        reputacaoCalculoService.calcularReputacaoEAtualizar(perfilAnuncianteId);

        verify(reputacaoRepository).save(reputacaoExistente);
        assertEquals(0.0, reputacaoExistente.getReputacaoScore());
        assertEquals(0, reputacaoExistente.getTotalAvaliacoes());
    }

    @Test
    void naoDeveCalcularSeAnuncianteNaoExistir() {
        when(perfilAnuncianteRepository.findById(perfilAnuncianteId)).thenReturn(Optional.empty());

        assertThrows(AnuncianteNaoEncontradoException.class,
                () -> reputacaoCalculoService.calcularReputacaoEAtualizar(perfilAnuncianteId));
        verify(reputacaoRepository, never()).save(any());
    }

    @Test
    void deveAplicarPesosCorretamenteNoScorePonderado() {
        // notas todas iguais a 4 → score ponderado deve ser exatamente 4.0
        Avaliacao a1 = criarAvaliacao(4, 4, 4, 4, 4);

        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of(a1));
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.of(reputacaoExistente));
        when(reputacaoRepository.findAll()).thenReturn(List.of());
        when(reputacaoRepository.save(any(ReputacaoAnunciante.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(perfilAnuncianteId);

        // mediaGlobal = 3.5 (lista vazia), m = 5, total = 1
        // reputacaoScore = (1 * 4.0 + 5 * 3.5) / 6 ≈ 3.583
        verify(reputacaoRepository).save(argThat(r ->
                r.getReputacaoScore() > 3.5 && r.getReputacaoScore() < 4.0
        ));
    }

    @Test
    void deveUsarMediaGlobalNaFormulaBayesiana() {
        // anunciante com nota máxima mas poucos votos deve ser puxado para a média global
        Avaliacao a1 = criarAvaliacao(5, 5, 5, 5, 5);

        ReputacaoAnunciante outraReputacao = new ReputacaoAnunciante();
        outraReputacao.setReputacaoScore(2.0);

        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of(a1));
        when(reputacaoRepository.findByPerfilAnunciante(perfilAnunciante))
                .thenReturn(Optional.of(reputacaoExistente));
        when(reputacaoRepository.findAll()).thenReturn(List.of(outraReputacao));
        when(reputacaoRepository.save(any(ReputacaoAnunciante.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(perfilAnuncianteId);

        // mediaGlobal = 2.0, scorePonderado = 5.0, total = 1, m = 5
        // reputacaoScore = (1 * 5.0 + 5 * 2.0) / 6 = 15/6 = 2.5
        verify(reputacaoRepository).save(argThat(r ->
                Math.abs(r.getReputacaoScore() - 2.5) < 0.001
        ));
    }

    @Test
    void deveCalcularReputacaoParaUniversitarioComPerfilAnunciante() {
        // garante que o cálculo funciona independente do tipo de Usuario do perfil
        com.apto.model.entity.UsuarioUniversitario universitario =
                new com.apto.model.entity.UsuarioUniversitario();
        universitario.setId(UUID.randomUUID());
        universitario.setNome("Ana Subloca");

        PerfilAnunciante perfilUniv = new PerfilAnunciante();
        perfilUniv.setId(UUID.randomUUID());
        perfilUniv.setUsuario(universitario);
        perfilUniv.setAtivo(true);

        Avaliacao av = new Avaliacao();
        av.setNotaGeral(4);
        av.setNotaComunicacao(4);
        av.setNotaFidelidadeAnuncio(4);
        av.setNotaEstadoMoradia(4);
        av.setNotaCustoBeneficio(4);
        av.setAtiva(true);
        av.setAnuncianteAvaliado(perfilUniv);

        when(perfilAnuncianteRepository.findById(perfilUniv.getId()))
                .thenReturn(Optional.of(perfilUniv));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilUniv.getId()))
                .thenReturn(List.of(av));
        when(reputacaoRepository.findByPerfilAnunciante(perfilUniv))
                .thenReturn(Optional.empty());
        when(reputacaoRepository.findAll()).thenReturn(List.of());
        when(reputacaoRepository.save(any(ReputacaoAnunciante.class))).thenAnswer(i -> i.getArgument(0));

        reputacaoCalculoService.calcularReputacaoEAtualizar(perfilUniv.getId());

        verify(reputacaoRepository).save(any(ReputacaoAnunciante.class));
    }
}