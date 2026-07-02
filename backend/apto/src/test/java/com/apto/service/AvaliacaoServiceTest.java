package com.apto.service;

import com.apto.dto.request.AtualizarAvaliacaoRequestDTO;
import com.apto.dto.request.CriarAvaliacaoRequestDTO;
import com.apto.dto.response.AvaliacaoResponseDTO;
import com.apto.dto.response.ResumoAvaliacoesAnuncianteResponseDTO;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.AvaliacaoDuplicadaException;
import com.apto.exception.AvaliacaoInvalidaException;
import com.apto.exception.AvaliacaoNaoEncontradaException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.AvaliacaoMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Avaliacao;
import com.apto.model.entity.Locador;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.Genero;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.repository.AvaliacaoRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
    private PerfilAnuncianteRepository perfilAnuncianteRepository;

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private ReputacaoCalculoService reputacaoCalculoService;

    @Spy
    private AvaliacaoMapper avaliacaoMapper = new AvaliacaoMapper();

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private UUID avaliacaoId;
    private UUID avaliadorId;
    private UUID perfilAnuncianteId;
    private UUID locadorId;
    private UUID anuncioId;
    private UUID moradiaId;

    private UsuarioUniversitario avaliador;
    private Locador locador;
    private PerfilAnunciante perfilAnunciante;
    private Moradia moradia;
    private Anuncio anuncio;
    private Avaliacao avaliacao;
    private CriarAvaliacaoRequestDTO criarDTO;
    private AtualizarAvaliacaoRequestDTO atualizarDTO;

    @BeforeEach
    void setUp() {
        avaliacaoId        = UUID.randomUUID();
        avaliadorId        = UUID.randomUUID();
        locadorId          = UUID.randomUUID();
        perfilAnuncianteId = UUID.randomUUID();
        anuncioId          = UUID.randomUUID();
        moradiaId          = UUID.randomUUID();

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

        // PerfilAnunciante do Locador
        perfilAnunciante = new PerfilAnunciante();
        perfilAnunciante.setId(perfilAnuncianteId);
        perfilAnunciante.setUsuario(locador);
        perfilAnunciante.setAtivo(true);

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
        anuncio.setAnunciante(perfilAnunciante);
        anuncio.setMoradia(moradia);

        avaliacao = new Avaliacao();
        avaliacao.setId(avaliacaoId);
        avaliacao.setAvaliador(avaliador);
        avaliacao.setAnuncianteAvaliado(perfilAnunciante);
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
                avaliadorId, anuncioId, 5, 4, 5, 4, 5, "Boa experiência");

        atualizarDTO = new AtualizarAvaliacaoRequestDTO(
                4, 4, 4, 3, 4, "Comentário atualizado");
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
        assertEquals(perfilAnuncianteId, response.anuncianteAvaliadoId());
        assertEquals(moradiaId, response.moradiaId());
        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(reputacaoCalculoService).calcularReputacaoEAtualizar(perfilAnuncianteId);
    }

    @Test
    void naoDeveCriarAvaliacaoSeAvaliadorNaoExistir() {
        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> avaliacaoService.criar(criarDTO));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAvaliacaoDoProprioAnuncio() {
        PerfilAnunciante perfilDoAvaliador = new PerfilAnunciante();
        perfilDoAvaliador.setId(UUID.randomUUID());
        perfilDoAvaliador.setUsuario(avaliador);
        perfilDoAvaliador.setAtivo(true);
        anuncio.setAnunciante(perfilDoAvaliador);

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
    void deveCriarAvaliacaoParaAnuncioDeUniversitarioComPerfilAnunciante() {
        UsuarioUniversitario anuncianteUniv = new UsuarioUniversitario();
        anuncianteUniv.setId(UUID.randomUUID());
        anuncianteUniv.setNome("João Subloca");

        PerfilAnunciante perfilUniv = new PerfilAnunciante();
        perfilUniv.setId(UUID.randomUUID());
        perfilUniv.setUsuario(anuncianteUniv);
        perfilUniv.setAtivo(true);

        anuncio.setAnunciante(perfilUniv);

        Avaliacao avaliacaoUniv = new Avaliacao();
        avaliacaoUniv.setId(UUID.randomUUID());
        avaliacaoUniv.setAvaliador(avaliador);
        avaliacaoUniv.setAnuncianteAvaliado(perfilUniv);
        avaliacaoUniv.setMoradia(moradia);
        avaliacaoUniv.setAnuncio(anuncio);
        avaliacaoUniv.setNotaGeral(4);
        avaliacaoUniv.setNotaComunicacao(4);
        avaliacaoUniv.setNotaFidelidadeAnuncio(4);
        avaliacaoUniv.setNotaEstadoMoradia(4);
        avaliacaoUniv.setNotaCustoBeneficio(4);
        avaliacaoUniv.setComentario("Ok");
        avaliacaoUniv.setDataCriacao(LocalDateTime.now());
        avaliacaoUniv.setAtiva(true);

        when(universitarioRepository.findById(avaliadorId)).thenReturn(Optional.of(avaliador));
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(avaliacaoRepository.existsByAvaliador_IdAndAnuncio_IdAndAtivaTrue(avaliadorId, anuncioId))
                .thenReturn(false);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacaoUniv);

        AvaliacaoResponseDTO response = avaliacaoService.criar(criarDTO);
        assertNotNull(response);
        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(reputacaoCalculoService).calcularReputacaoEAtualizar(perfilUniv.getId());
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
    void deveListarAvaliacoesPorAnunciante() {
        when(perfilAnuncianteRepository.existsById(perfilAnuncianteId)).thenReturn(true);
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of(avaliacao));

        List<AvaliacaoResponseDTO> resultado = avaliacaoService.listarPorAnunciante(perfilAnuncianteId);

        assertEquals(1, resultado.size());
        assertEquals(perfilAnuncianteId, resultado.get(0).anuncianteAvaliadoId());
    }

    @Test
    void naoDeveListarAvaliacoesPorAnuncianteInexistente() {
        when(perfilAnuncianteRepository.existsById(perfilAnuncianteId)).thenReturn(false);

        assertThrows(AnuncianteNaoEncontradoException.class,
                () -> avaliacaoService.listarPorAnunciante(perfilAnuncianteId));
        verify(avaliacaoRepository, never()).findByAnuncianteAvaliado_IdAndAtivaTrue(any());
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
        verify(reputacaoCalculoService).calcularReputacaoEAtualizar(perfilAnuncianteId);
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
        verify(reputacaoCalculoService).calcularReputacaoEAtualizar(perfilAnuncianteId);
    }

    @Test
    void naoDeveDesativarAvaliacaoDeOutroAvaliador() {
        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacao));

        assertThrows(AcessoNegadoException.class,
                () -> avaliacaoService.desativar(avaliacaoId, UUID.randomUUID()));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    void deveGerarResumoPorAnunciante() {
        Avaliacao outra = new Avaliacao();
        outra.setNotaGeral(3);
        outra.setNotaComunicacao(5);
        outra.setNotaFidelidadeAnuncio(3);
        outra.setNotaEstadoMoradia(5);
        outra.setNotaCustoBeneficio(3);

        when(perfilAnuncianteRepository.findById(perfilAnuncianteId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(avaliacaoRepository.findByAnuncianteAvaliado_IdAndAtivaTrue(perfilAnuncianteId))
                .thenReturn(List.of(avaliacao, outra));

        ResumoAvaliacoesAnuncianteResponseDTO resumo =
                avaliacaoService.resumoPorAnunciante(perfilAnuncianteId);

        assertEquals(perfilAnuncianteId, resumo.anuncianteId());
        assertEquals(2, resumo.totalAvaliacoes());
        assertEquals(4.0, resumo.notaMediaGeral());
        assertEquals(4.5, resumo.notaMediaComunicacao());
    }

    @Test
    void naoDeveGerarResumoPorAnuncianteInexistente() {
        when(perfilAnuncianteRepository.findById(perfilAnuncianteId)).thenReturn(Optional.empty());

        assertThrows(AnuncianteNaoEncontradoException.class,
                () -> avaliacaoService.resumoPorAnunciante(perfilAnuncianteId));
    }
}
