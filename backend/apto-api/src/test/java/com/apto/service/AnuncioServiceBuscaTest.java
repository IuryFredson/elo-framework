package com.apto.service;

import com.apto.dto.request.FiltroBuscaAnuncioDTO;
import com.apto.dto.response.BuscaAnuncioResponseDTO;
import com.apto.dto.response.PaginaResponseDTO;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Locador;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.MoradiaRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnuncioServiceBuscaTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private MoradiaRepository moradiaRepository;

    @Mock
    private LocadorRepository locadorRepository;

    @Mock
    private UsuarioUniversitarioRepository universitarioRepository;

    @InjectMocks
    private AnuncioService anuncioService;

    private Locador locador;
    private Moradia moradia;
    private Anuncio anuncio;
    private FiltroBuscaAnuncioDTO filtroVazio;
    private PerfilAnunciante perfilAnunciante;

    @BeforeEach
    void setUp() {
        locador = new Locador();
        locador.setId(UUID.randomUUID());
        locador.setNome("João Silva");
        locador.setEmail("joao@email.com");
        locador.setDocumentoIdentificacao("12345678900");
        locador.setNomeExibicaoOuRazao("João Imóveis");

        moradia = new Moradia();
        moradia.setId(UUID.randomUUID());
        moradia.setTipoMoradia(TipoMoradia.APARTAMENTO);
        moradia.setBairro("Centro");
        moradia.setEnderecoResumo("Rua das Flores, 123");
        moradia.setMobiliado(true);
        moradia.setAceitaAnimais(false);
        moradia.setQuantidadeVagas(2);
        moradia.setRegrasMoradia("Sem festas após 22h");

        perfilAnunciante = new PerfilAnunciante();
        perfilAnunciante.setId(UUID.randomUUID());
        perfilAnunciante.setUsuario(locador);
        perfilAnunciante.setAtivo(true);

        anuncio = new Anuncio();
        anuncio.setId(UUID.randomUUID());
        anuncio.setTitulo("Apartamento no centro");
        anuncio.setDescricao("Ótimo apartamento");
        anuncio.setValorMensal(new BigDecimal("850.00"));
        anuncio.setTipoAnuncio(TipoAnuncio.IMOVEL_COMPLETO);
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDataPublicacao(LocalDate.now());
        anuncio.setAnunciante(perfilAnunciante);
        anuncio.setMoradia(moradia);

        filtroVazio = new FiltroBuscaAnuncioDTO(null, null, null, null, null, null, null, null);
    }

    @Test
    void buscarAnuncios_semFiltros_deveRetornarTodosAtivos() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtroVazio, pageable);

        assertEquals(1, resultado.conteudo().size());
        assertEquals(0, resultado.paginaAtual());
        assertEquals(1, resultado.totalPaginas());
        assertEquals(1, resultado.totalElementos());
        assertEquals(10, resultado.tamanhoPagina());

        BuscaAnuncioResponseDTO dto = resultado.conteudo().get(0);
        assertEquals("Apartamento no centro", dto.titulo());
        assertEquals(new BigDecimal("850.00"), dto.valorMensal());
        assertEquals("Centro", dto.bairro());
        assertEquals("João Imóveis", dto.nomeAnunciante());
    }

    @Test
    void buscarAnuncios_semFiltros_deveRetornarPaginaVazia() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Anuncio> pageVazia = new PageImpl<>(List.of(), pageable, 0);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(pageVazia);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtroVazio, pageable);

        assertTrue(resultado.conteudo().isEmpty());
        assertEquals(0, resultado.totalElementos());
    }

    @Test
    void buscarAnuncios_comFiltroValorMax_devePassarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal valorMax = new BigDecimal("1000.00");
        FiltroBuscaAnuncioDTO filtro = new FiltroBuscaAnuncioDTO(null, valorMax, null, null, null, null, null, null);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                isNull(), eq(valorMax), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtro, pageable);

        assertEquals(1, resultado.conteudo().size());
        verify(anuncioRepository).buscarComFiltros(
                isNull(), eq(valorMax), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable));
    }

    @Test
    void buscarAnuncios_comFiltroBairro_devePassarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltroBuscaAnuncioDTO filtro = new FiltroBuscaAnuncioDTO(null, null, "Centro", null, null, null, null, null);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), eq("Centro"), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtro, pageable);

        assertEquals(1, resultado.conteudo().size());
        assertEquals("Centro", resultado.conteudo().get(0).bairro());
    }

    @Test
    void buscarAnuncios_comFiltroTipoMoradia_devePassarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltroBuscaAnuncioDTO filtro = new FiltroBuscaAnuncioDTO(null, null, null, TipoMoradia.APARTAMENTO, null, null, null, null);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), isNull(), eq(TipoMoradia.APARTAMENTO), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtro, pageable);

        assertEquals(1, resultado.conteudo().size());
        assertEquals(TipoMoradia.APARTAMENTO, resultado.conteudo().get(0).tipoMoradia());
    }

    @Test
    void buscarAnuncios_comFiltroMobiliado_devePassarParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        FiltroBuscaAnuncioDTO filtro = new FiltroBuscaAnuncioDTO(null, null, null, null, null, true, null, null);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), isNull(), isNull(), isNull(), eq(true), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtro, pageable);

        assertEquals(1, resultado.conteudo().size());
        assertTrue(resultado.conteudo().get(0).mobiliado());
    }

    @Test
    void buscarAnuncios_comMultiplosFiltros_devePassarTodosParaRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        BigDecimal valorMin = new BigDecimal("500.00");
        BigDecimal valorMax = new BigDecimal("1000.00");
        FiltroBuscaAnuncioDTO filtro = new FiltroBuscaAnuncioDTO(
                valorMin, valorMax, "Centro", TipoMoradia.APARTAMENTO,
                TipoAnuncio.IMOVEL_COMPLETO, true, false, 1);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                eq(valorMin), eq(valorMax), eq("Centro"), eq(TipoMoradia.APARTAMENTO),
                eq(TipoAnuncio.IMOVEL_COMPLETO), eq(true), eq(false), eq(1), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtro, pageable);

        assertEquals(1, resultado.conteudo().size());
        verify(anuncioRepository).buscarComFiltros(
                eq(valorMin), eq(valorMax), eq("Centro"), eq(TipoMoradia.APARTAMENTO),
                eq(TipoAnuncio.IMOVEL_COMPLETO), eq(true), eq(false), eq(1), eq(pageable));
    }

    @Test
    void buscarAnuncios_deveRespeitarPaginacao() {
        Pageable pageable = PageRequest.of(2, 5);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 11);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        PaginaResponseDTO<BuscaAnuncioResponseDTO> resultado = anuncioService.buscarAnuncios(filtroVazio, pageable);

        assertEquals(2, resultado.paginaAtual());
        assertEquals(5, resultado.tamanhoPagina());
        assertEquals(11, resultado.totalElementos());
        assertEquals(3, resultado.totalPaginas());
    }

    @Test
    void buscarAnuncios_deveMapearCamposDaMoradiaCorretamente() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Anuncio> page = new PageImpl<>(List.of(anuncio), pageable, 1);

        when(anuncioRepository.buscarComFiltros(
                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
                .thenReturn(page);

        BuscaAnuncioResponseDTO dto = anuncioService.buscarAnuncios(filtroVazio, pageable).conteudo().get(0);

        assertEquals(moradia.getId(), dto.moradiaId());
        assertEquals(moradia.getTipoMoradia(), dto.tipoMoradia());
        assertEquals(moradia.getEnderecoResumo(), dto.enderecoResumo());
        assertEquals(moradia.isMobiliado(), dto.mobiliado());
        assertEquals(moradia.isAceitaAnimais(), dto.aceitaAnimais());
        assertEquals(moradia.getQuantidadeVagas(), dto.quantidadeVagas());
    }
}
