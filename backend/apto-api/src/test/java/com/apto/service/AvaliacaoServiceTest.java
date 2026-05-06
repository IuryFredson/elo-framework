package com.apto.service;

import com.apto.dto.request.AtualizarAvaliacaoRequestDTO;
import com.apto.dto.request.CriarAvaliacaoRequestDTO;
import com.apto.dto.response.AvaliacaoResponseDTO;
import com.apto.dto.response.ResumoAvaliacoesLocadorResponseDTO;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AvaliacaoDuplicadaException;
import com.apto.exception.AvaliacaoInvalidaException;
import com.apto.exception.AvaliacaoNaoEncontradaException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.Genero;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private UsuarioUniversitarioRepository universitarioRepository;

    @Mock
    private LocadorRepository locadorRepository;

    @Mock
    private AnuncioService anuncioService;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private UUID avaliacaoId;
    private UUID avaliadorId;
    private UUID locadorId;
    private UUID anuncioId;
    private UUID moradiaId;

    private UsuarioUniversitario avaliador;
    private Locador locador;
    private Moradia moradia;
    private Anuncio anuncio;
    private Avaliacao avaliacao;
    private CriarAvaliacaoRequestDTO criarDTO;
    private AtualizarAvaliacaoRequestDTO atualizarDTO;

    @BeforeEach
    void setUp() {
        avaliacaoId = UUID.randomUUID();
        avaliadorId = UUID.randomUUID();
        locadorId = UUID.randomUUID();
        anuncioId = UUID.randomUUID();
        moradiaId = UUID.randomUUID();

        avaliador = new UsuarioUniversitario();
        avaliador.setId(avaliadorId);
        avaliador.setNome("Maria Estudante");
        avaliador.setEmail("maria@email.com");
        avaliador.setEmailInstitucional("maria@universidade.edu");
        avaliador.setCurso("Computação");
        avaliador.setDataNascimento(LocalDate.of(2001, 3, 12));
        avaliador.setGenero(Genero.FEMININO);

        locador = new Locador();
        locador.setId(locadorId);
        locador.setNome("Carlos Locador");
        locador.setEmail("carlos@email.com");
        locador.setDocumentoIdentificacao("12345678900");
        locador.setNomeExibicaoOuRazao("Carlos Imóveis");

        moradia = new Moradia();
        moradia.setId(moradiaId);
        moradia.setTipoMoradia(TipoMoradia.APARTAMENTO);
        moradia.setBairro("Centro");
        moradia.setEnderecoResumo("Rua A, 123");
        moradia.setMobiliado(true);
        moradia.setAceitaAnimais(false);
        moradia.setQuantidadeVagas(1);
        moradia.setRegrasMoradia("Sem festas");

        anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setTitulo("Apartamento no Centro");
        anuncio.setDescricao("Perto da universidade");
        anuncio.setValorMensal(new BigDecimal("1200.00"));
        anuncio.setTipoAnuncio(TipoAnuncio.IMOVEL_COMPLETO);
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDataPublicacao(LocalDate.now());
        anuncio.setAnunciante(locador);
        anuncio.setMoradia(moradia);

        avaliacao = new Avaliacao();
        avaliacao.setId(avaliacaoId);
        avaliacao.setAvaliador(avaliador);
        avaliacao.setLocadorAvaliado(locador);
        avaliacao.setMoradia(moradia);
        avaliacao.setAnuncio(anuncio);
        avaliacao.setNotaGeral(5);
        avaliacao.setNotaComunicacao(4);
        avaliacao.setNotaFidelidadeAnuncio(5);
        avaliacao.setNotaEstadoMoradia(4);
        avaliacao.setNotaCustoBeneficio(5);
        avaliacao.setComentario("Boa experiência");
        avaliacao.setDataCriacao(LocalDateTime.now());
        avaliacao.setAtiva(true);

        criarDTO = new CriarAvaliacaoRequestDTO(
                avaliadorId,
                anuncioId,
                5,
                4,
                5,
                4,
                5,
                "Boa experiência"
        );

        atualizarDTO = new AtualizarAvaliacaoRequestDTO(
                4,
                4,
                4,
                3,
                4,
                "Comentário atualizado"
        );
    }

    @Test
    void deveCriarAvaliacaoComDadosValidos() {
        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.of(avaliador));
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(avaliacaoRepository.existsByAvaliador_IdAndAnuncio_IdAndAtivaTrue(avaliadorId, anuncioId))
                .thenReturn(false);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        AvaliacaoResponseDTO response = avaliacaoService.criar(criarDTO);

        assertNotNull(response);
        assertEquals(avaliacaoId, response.id());
        assertEquals(avaliadorId, response.avaliadorId());
        assertEquals(locadorId, response.locadorAvaliadoId());
        assertEquals(moradiaId, response.moradiaId());
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    void naoDeveCriarAvaliacaoSeAvaliadorNaoExistir() {
        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> avaliacaoService.criar(criarDTO));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAvaliacaoDoProprioAnuncio() {
        anuncio.setAnunciante(avaliador);
        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.of(avaliador));
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);

        assertThrows(AvaliacaoInvalidaException.class, () -> avaliacaoService.criar(criarDTO));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAvaliacaoParaAnuncioDeUniversitario() {
        UsuarioUniversitario anuncianteUniversitario = new UsuarioUniversitario();
        anuncianteUniversitario.setId(UUID.randomUUID());
        anuncio.setAnunciante(anuncianteUniversitario);

        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.of(avaliador));
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);

        assertThrows(AvaliacaoInvalidaException.class, () -> avaliacaoService.criar(criarDTO));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAvaliacaoDuplicadaAtiva() {
        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.of(avaliador));
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(avaliacaoRepository.existsByAvaliador_IdAndAnuncio_IdAndAtivaTrue(avaliadorId, anuncioId))
                .thenReturn(true);

        assertThrows(AvaliacaoDuplicadaException.class, () -> avaliacaoService.criar(criarDTO));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAvaliacaoComNotaInvalida() {
        CriarAvaliacaoRequestDTO dto = new CriarAvaliacaoRequestDTO(
                avaliadorId, anuncioId, 6, 4, 5, 4, 5, "Nota inválida");

        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.of(avaliador));
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(avaliacaoRepository.existsByAvaliador_IdAndAnuncio_IdAndAtivaTrue(avaliadorId, anuncioId))
                .thenReturn(false);

        assertThrows(AvaliacaoInvalidaException.class, () -> avaliacaoService.criar(dto));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveBuscarAvaliacaoPorId() {
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));

        AvaliacaoResponseDTO response = avaliacaoService.buscarPorId(avaliacaoId);

        assertEquals(avaliacaoId, response.id());
    }

    @Test
    void naoDeveBuscarAvaliacaoInexistente() {
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        assertThrows(AvaliacaoNaoEncontradaException.class,
                () -> avaliacaoService.buscarPorId(avaliacaoId));
    }

    @Test
    void deveListarAvaliacoesPorLocador() {
        when(locadorRepository.existsById(locadorId)).thenReturn(true);
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of(avaliacao));

        List<AvaliacaoResponseDTO> resultado = avaliacaoService.listarPorLocador(locadorId);

        assertEquals(1, resultado.size());
        assertEquals(locadorId, resultado.get(0).locadorAvaliadoId());
    }

    @Test
    void deveListarAvaliacoesPorMoradia() {
        when(avaliacaoRepository.findByMoradia_IdAndAtivaTrue(moradiaId))
                .thenReturn(List.of(avaliacao));

        List<AvaliacaoResponseDTO> resultado = avaliacaoService.listarPorMoradia(moradiaId);

        assertEquals(1, resultado.size());
        assertEquals(moradiaId, resultado.get(0).moradiaId());
    }

    @Test
    void deveListarAvaliacoesPorAvaliador() {
        when(avaliacaoRepository.findByAvaliador_IdAndAtivaTrue(avaliadorId))
                .thenReturn(List.of(avaliacao));

        List<AvaliacaoResponseDTO> resultado = avaliacaoService.listarPorAvaliador(avaliadorId);

        assertEquals(1, resultado.size());
        assertEquals(avaliadorId, resultado.get(0).avaliadorId());
    }

    @Test
    void deveAtualizarAvaliacaoDoProprioAvaliador() {
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        AvaliacaoResponseDTO response = avaliacaoService.atualizar(avaliacaoId, avaliadorId, atualizarDTO);

        assertEquals(4, response.notaGeral());
        assertEquals("Comentário atualizado", response.comentario());
        verify(avaliacaoRepository).save(avaliacao);
    }

    @Test
    void naoDeveAtualizarAvaliacaoDeOutroAvaliador() {
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));

        assertThrows(AcessoNegadoException.class,
                () -> avaliacaoService.atualizar(avaliacaoId, UUID.randomUUID(), atualizarDTO));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveDesativarAvaliacaoDoProprioAvaliador() {
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        avaliacaoService.desativar(avaliacaoId, avaliadorId);

        ArgumentCaptor<Avaliacao> captor = ArgumentCaptor.forClass(Avaliacao.class);
        verify(avaliacaoRepository).save(captor.capture());
        assertFalse(captor.getValue().getAtiva());
    }

    @Test
    void deveGerarResumoPorLocador() {
        Avaliacao outra = new Avaliacao();
        outra.setNotaGeral(3);
        outra.setNotaComunicacao(5);
        outra.setNotaFidelidadeAnuncio(3);
        outra.setNotaEstadoMoradia(5);
        outra.setNotaCustoBeneficio(3);

        when(locadorRepository.findById(locadorId)).thenReturn(Optional.of(locador));
        when(avaliacaoRepository.findByLocadorAvaliado_IdAndAtivaTrue(locadorId))
                .thenReturn(List.of(avaliacao, outra));

        ResumoAvaliacoesLocadorResponseDTO resumo = avaliacaoService.resumoPorLocador(locadorId);

        assertEquals(locadorId, resumo.locadorId());
        assertEquals(2, resumo.totalAvaliacoes());
        assertEquals(4.0, resumo.notaMediaGeral());
        assertEquals(4.5, resumo.notaMediaComunicacao());
    }
}
