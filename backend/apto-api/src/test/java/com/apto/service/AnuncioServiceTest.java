package com.apto.service;

import com.apto.dto.request.AtualizarAnuncioRequestDTO;
import com.apto.dto.request.CriarAnuncioRequestDTO;
import com.apto.dto.response.AnuncioResponseDTO;
import com.apto.event.AnuncioIndisponibilizadoEvent;
import com.apto.event.MotivoIndisponibilizacaoAnuncio;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.AnuncioNaoEncontradoException;
import com.apto.exception.MoradiaAssociadaComAnuncioException;
import com.apto.exception.MoradiaNaoEncontradaException;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Locador;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.enums.StatusAnuncio;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.observer.DomainEventPublisher;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.MoradiaRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnuncioServiceTest {

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private MoradiaRepository moradiaRepository;

    @Mock
    private PerfilAnuncianteRepository perfilAnuncianteRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private AnuncioService anuncioService;

    private UUID usuarioId;
    private UUID perfilAnuncianteId;
    private UUID moradiaId;
    private UUID anuncioId;

    private Locador locador;
    private PerfilAnunciante perfilAnunciante;
    private Moradia moradia;
    private Anuncio anuncio;

    private CriarAnuncioRequestDTO criarDTO;
    private AtualizarAnuncioRequestDTO atualizarDTO;

    @BeforeEach
    void setUp() {
        usuarioId          = UUID.randomUUID();
        perfilAnuncianteId = UUID.randomUUID();
        moradiaId          = UUID.randomUUID();
        anuncioId          = UUID.randomUUID();

        locador = new Locador();
        locador.setId(usuarioId);
        locador.setNome("Uirá Kulesza");
        locador.setEmail("uira@email.com");
        locador.setDocumentoIdentificacao("12345678900");
        locador.setNomeExibicaoOuRazao("Uirá Imóveis");

        perfilAnunciante = new PerfilAnunciante();
        perfilAnunciante.setId(perfilAnuncianteId);
        perfilAnunciante.setUsuario(locador);
        perfilAnunciante.setAtivo(true);

        moradia = new Moradia();
        moradia.setId(moradiaId);
        moradia.setBairro("Centro");
        moradia.setTipoMoradia(TipoMoradia.APARTAMENTO);
        moradia.setMobiliado(true);
        moradia.setAceitaAnimais(false);
        moradia.setQuantidadeVagas(1);
        moradia.setRegrasMoradia("Sem festas");

        anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setTitulo("Apto centro");
        anuncio.setDescricao("Ótima localização");
        anuncio.setValorMensal(new BigDecimal("1200.00"));
        anuncio.setTipoAnuncio(TipoAnuncio.IMOVEL_COMPLETO);
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDataPublicacao(LocalDate.now());
        anuncio.setAnunciante(perfilAnunciante);
        anuncio.setMoradia(moradia);
        criarDTO = new CriarAnuncioRequestDTO(
                "Apto centro",
                "Ótima localização",
                new BigDecimal("1200.00"),
                TipoAnuncio.IMOVEL_COMPLETO,
                moradiaId,
                usuarioId
        );

        atualizarDTO = new AtualizarAnuncioRequestDTO(
                "Titulo novo",
                "Descrição nova",
                new BigDecimal("1500.00"),
                TipoAnuncio.IMOVEL_COMPLETO
        );
    }

    @Test
    void deveCriarAnuncioComDadosValidos() {
        when(perfilAnuncianteRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.of(moradia));
        when(anuncioRepository.existsByMoradia(moradia)).thenReturn(false);
        when(anuncioRepository.save(any(Anuncio.class))).thenReturn(anuncio);

        AnuncioResponseDTO response = anuncioService.criar(criarDTO);

        assertNotNull(response);
        assertEquals(anuncioId, response.id());
        assertEquals(StatusAnuncio.ATIVO, response.status());
        verify(anuncioRepository).save(any(Anuncio.class));
    }

    @Test
    void naoDeveCriarAnuncioSeAnuncianteNaoTiverPerfil() {
        when(perfilAnuncianteRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.empty());

        assertThrows(AnuncianteNaoEncontradoException.class, () -> anuncioService.criar(criarDTO));
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAnuncioSePerfilAnuncianteEstiverInativo() {
        perfilAnunciante.setAtivo(false);
        when(perfilAnuncianteRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.of(perfilAnunciante));

        assertThrows(AnuncianteNaoEncontradoException.class, () -> anuncioService.criar(criarDTO));
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAnuncioSeMoradiaNaoEncontrada() {
        when(perfilAnuncianteRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.empty());

        assertThrows(MoradiaNaoEncontradaException.class, () -> anuncioService.criar(criarDTO));
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarAnuncioSeMoradiaJaAssociada() {
        when(perfilAnuncianteRepository.findByUsuario_Id(usuarioId))
                .thenReturn(Optional.of(perfilAnunciante));
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.of(moradia));
        when(anuncioRepository.existsByMoradia(moradia)).thenReturn(true);

        assertThrows(MoradiaAssociadaComAnuncioException.class, () -> anuncioService.criar(criarDTO));
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    void deveListarAnuncios() {
        when(anuncioRepository.findAll()).thenReturn(List.of(anuncio));

        var result = anuncioService.listarTodos();

        assertEquals(1, result.size());
        verify(anuncioRepository).findAll();
    }

    @Test
    void deveBuscarAnuncioPorId() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));

        AnuncioResponseDTO response = anuncioService.buscarPorId(anuncioId);

        assertEquals(anuncioId, response.id());
    }

    @Test
    void naoDeveBuscarAnuncioInexistente() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(AnuncioNaoEncontradoException.class,
                () -> anuncioService.buscarPorId(anuncioId));
    }

    @Test
    void deveDeletarAnuncioValido() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));

        anuncioService.deletar(anuncioId);

        verify(eventPublisher).publish(new AnuncioIndisponibilizadoEvent(
                anuncioId, StatusAnuncio.ATIVO, null, MotivoIndisponibilizacaoAnuncio.DELETADO));
        verify(anuncioRepository).delete(anuncio);
    }

    @Test
    void naoDeveDeletarAnuncioInexistente() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(AnuncioNaoEncontradoException.class, () -> anuncioService.deletar(anuncioId));
        verify(anuncioRepository, never()).delete(any());
    }

    @Test
    void deveAtualizarAnuncioComDadosValidos() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.save(any(Anuncio.class))).thenReturn(anuncio);

        AnuncioResponseDTO response = anuncioService.atualizar(anuncioId, usuarioId, atualizarDTO);

        assertNotNull(response);
        verify(anuncioRepository).save(anuncio);
    }

    @Test
    void naoDeveAtualizarCasoAnuncianteSejaDiferenteDoCriador() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));

        assertThrows(AcessoNegadoException.class,
                () -> anuncioService.atualizar(anuncioId, UUID.randomUUID(), atualizarDTO));
        verify(anuncioRepository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarCasoAnuncioNaoExista() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(AnuncioNaoEncontradoException.class,
                () -> anuncioService.atualizar(anuncioId, usuarioId, atualizarDTO));
    }

    @Test
    void deveAtualizarStatusComAnuncioEncontrado() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(anuncioRepository.save(any(Anuncio.class))).thenReturn(anuncio);

        AnuncioResponseDTO response = anuncioService.atualizarStatus(anuncioId, StatusAnuncio.PAUSADO);

        assertNotNull(response);
        verify(anuncioRepository).save(anuncio);
        verify(eventPublisher).publish(new AnuncioIndisponibilizadoEvent(
                anuncioId, StatusAnuncio.ATIVO, StatusAnuncio.PAUSADO, MotivoIndisponibilizacaoAnuncio.PAUSADO));
    }

    @Test
    void naoDeveAtualizarStatusCasoAnuncioNaoExista() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(AnuncioNaoEncontradoException.class,
                () -> anuncioService.atualizarStatus(anuncioId, StatusAnuncio.PAUSADO));
    }
}
