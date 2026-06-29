package com.apto.service;

import com.apto.dto.request.CriarManifestacaoInteresseRequestDTO;
import com.apto.dto.response.ManifestacaoInteresseDetalheResponseDTO;
import com.apto.dto.response.ManifestacaoInteresseResponseDTO;
import com.apto.exception.AcessoNegadoException;
import com.apto.exception.AnuncioNaoAtivoException;
import com.apto.exception.AnuncioNaoEncontradoException;
import com.apto.exception.ManifestacaoInteresseDuplicadaException;
import com.apto.exception.ManifestacaoInteresseInvalidaException;
import com.apto.exception.ManifestacaoInteresseNaoEncontradaException;
import com.apto.exception.TransicaoInvalidaManifestacaoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.ManifestacaoInteresseMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Locador;
import com.apto.model.entity.ManifestacaoInteresse;
import com.apto.model.entity.Moradia;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.Genero;
import com.apto.model.enums.StatusAnuncio;
import com.elo.manifestacao.StatusManifestacaoInteresse;
import com.apto.model.enums.TipoAnuncio;
import com.apto.model.enums.TipoMoradia;
import com.apto.repository.ManifestacaoInteresseRepository;
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
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManifestacaoInteresseServiceTest {

    @Mock
    private ManifestacaoInteresseRepository manifestacaoRepository;

    @Mock
    private AnuncioService anuncioService;

    @Mock
    private UsuarioUniversitarioRepository universitarioRepository;

    @Spy
    private ManifestacaoInteresseMapper manifestacaoMapper = new ManifestacaoInteresseMapper();

    @InjectMocks
    private ManifestacaoInteresseService manifestacaoInteresseService;

    private UUID anuncioId;
    private UUID anuncianteUsuarioId;
    private UUID interessadoId;
    private UUID manifestacaoId;

    private Locador anunciante;
    private PerfilAnunciante perfilAnunciante;
    private UsuarioUniversitario interessado;
    private Moradia moradia;
    private Anuncio anuncio;
    private ManifestacaoInteresse manifestacao;
    private CriarManifestacaoInteresseRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        anuncioId          = UUID.randomUUID();
        anuncianteUsuarioId = UUID.randomUUID();
        interessadoId      = UUID.randomUUID();
        manifestacaoId     = UUID.randomUUID();

        anunciante = new Locador();
        anunciante.setId(anuncianteUsuarioId);
        anunciante.setNome("Ana Locadora");
        anunciante.setEmail("ana@example.com");
        anunciante.setTelefone("+5583999990001");
        anunciante.setDocumentoIdentificacao("11122233344");
        anunciante.setNomeExibicaoOuRazao("Ana Imóveis");

        // PerfilAnunciante wrapping o Locador
        perfilAnunciante = new PerfilAnunciante();
        perfilAnunciante.setId(UUID.randomUUID());
        perfilAnunciante.setUsuario(anunciante);
        perfilAnunciante.setAtivo(true);

        interessado = new UsuarioUniversitario();
        interessado.setId(interessadoId);
        interessado.setNome("Bruno Estudante");
        interessado.setEmail("bruno@example.com");
        interessado.setTelefone("+5583999990002");
        interessado.setEmailInstitucional("bruno@academico.ufpb.br");
        interessado.setCurso("Ciência da Computação");
        interessado.setDataNascimento(LocalDate.of(2002, 5, 20));
        interessado.setGenero(Genero.MASCULINO);

        moradia = new Moradia();
        moradia.setId(UUID.randomUUID());
        moradia.setTipoMoradia(TipoMoradia.APARTAMENTO);
        moradia.setBairro("Manaíra");

        anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setTitulo("Apto 2 quartos em Manaíra");
        anuncio.setDescricao("Próximo à praia");
        anuncio.setValorMensal(new BigDecimal("1800.00"));
        anuncio.setTipoAnuncio(TipoAnuncio.IMOVEL_COMPLETO);
        anuncio.setStatus(StatusAnuncio.ATIVO);
        anuncio.setDataPublicacao(LocalDate.now());
        anuncio.setAnunciante(perfilAnunciante);
        anuncio.setMoradia(moradia);

        manifestacao = new ManifestacaoInteresse();
        manifestacao.setId(manifestacaoId);
        manifestacao.setAnuncio(anuncio);
        manifestacao.setInteressado(interessado);
        manifestacao.setStatus(StatusManifestacaoInteresse.PENDENTE);
        manifestacao.setMensagem("Tenho interesse");
        manifestacao.setDataManifestacao(LocalDateTime.now());

        criarDTO = new CriarManifestacaoInteresseRequestDTO(
                anuncioId, interessadoId, "Tenho interesse, posso visitar?");
    }

    // ---------- criar ----------

    @Test
    void deveCriarManifestacaoComDadosValidos() {
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(universitarioRepository.findById(interessadoId)).thenReturn(Optional.of(interessado));
        when(manifestacaoRepository.existsByAnuncio_IdAndInteressado_IdAndStatusIn(
                eq(anuncioId), eq(interessadoId), anyCollection())).thenReturn(false);
        when(manifestacaoRepository.save(any(ManifestacaoInteresse.class))).thenReturn(manifestacao);

        ManifestacaoInteresseDetalheResponseDTO response = manifestacaoInteresseService.criar(criarDTO);

        assertNotNull(response);
        assertEquals(manifestacaoId, response.id());
        assertEquals(StatusManifestacaoInteresse.PENDENTE, response.status());
        assertNull(response.contatoInteressado());
        assertNull(response.contatoAnunciante());

        ArgumentCaptor<ManifestacaoInteresse> captor = ArgumentCaptor.forClass(ManifestacaoInteresse.class);
        verify(manifestacaoRepository).save(captor.capture());
        ManifestacaoInteresse salva = captor.getValue();
        assertEquals(StatusManifestacaoInteresse.PENDENTE, salva.getStatus());
        assertNotNull(salva.getDataManifestacao());
        assertEquals(anuncio, salva.getAnuncio());
        assertEquals(interessado, salva.getInteressado());
        assertEquals("Tenho interesse, posso visitar?", salva.getMensagem());
    }

    @Test
    void naoDeveCriarManifestacaoSeAnuncioNaoExistir() {
        when(anuncioService.buscarEntidadePorId(anuncioId))
                .thenThrow(new AnuncioNaoEncontradoException("Anuncio não encontrado"));

        assertThrows(AnuncioNaoEncontradoException.class,
                () -> manifestacaoInteresseService.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarManifestacaoSeInteressadoNaoExistir() {
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(universitarioRepository.findById(interessadoId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> manifestacaoInteresseService.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarManifestacaoSeAnuncioNaoEstiverAtivo() {
        anuncio.setStatus(StatusAnuncio.PAUSADO);
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(universitarioRepository.findById(interessadoId)).thenReturn(Optional.of(interessado));

        assertThrows(AnuncioNaoAtivoException.class,
                () -> manifestacaoInteresseService.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarManifestacaoNoProprioAnuncio() {
        PerfilAnunciante perfilDoInteressado = new PerfilAnunciante();
        perfilDoInteressado.setId(UUID.randomUUID());
        perfilDoInteressado.setUsuario(interessado);
        perfilDoInteressado.setAtivo(true);
        anuncio.setAnunciante(perfilDoInteressado);

        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(universitarioRepository.findById(interessadoId)).thenReturn(Optional.of(interessado));

        assertThrows(ManifestacaoInteresseInvalidaException.class,
                () -> manifestacaoInteresseService.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarManifestacaoSeJaExistirAtiva() {
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(universitarioRepository.findById(interessadoId)).thenReturn(Optional.of(interessado));
        when(manifestacaoRepository.existsByAnuncio_IdAndInteressado_IdAndStatusIn(
                eq(anuncioId), eq(interessadoId), anyCollection())).thenReturn(true);

        assertThrows(ManifestacaoInteresseDuplicadaException.class,
                () -> manifestacaoInteresseService.criar(criarDTO));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void deveConsultarDuplicidadeComPendenteEAceita() {
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(universitarioRepository.findById(interessadoId)).thenReturn(Optional.of(interessado));
        when(manifestacaoRepository.existsByAnuncio_IdAndInteressado_IdAndStatusIn(
                eq(anuncioId), eq(interessadoId), anyCollection())).thenReturn(false);
        when(manifestacaoRepository.save(any(ManifestacaoInteresse.class))).thenReturn(manifestacao);

        manifestacaoInteresseService.criar(criarDTO);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<StatusManifestacaoInteresse>> captor =
                ArgumentCaptor.forClass(Collection.class);
        verify(manifestacaoRepository).existsByAnuncio_IdAndInteressado_IdAndStatusIn(
                eq(anuncioId), eq(interessadoId), captor.capture());
        Collection<StatusManifestacaoInteresse> statuses = captor.getValue();
        assertEquals(2, statuses.size());
        assertTrue(statuses.contains(StatusManifestacaoInteresse.PENDENTE));
        assertTrue(statuses.contains(StatusManifestacaoInteresse.ACEITA));
    }

    // ---------- aceitar ----------

    @Test
    void deveAceitarManifestacaoPendente() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(any(ManifestacaoInteresse.class))).thenReturn(manifestacao);

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.aceitar(manifestacaoId, anuncianteUsuarioId);

        assertEquals(StatusManifestacaoInteresse.ACEITA, response.status());
        assertNotNull(response.dataResposta());
        assertNotNull(response.contatoInteressado());
        assertEquals("bruno@academico.ufpb.br", response.contatoInteressado().emailInstitucional());
        assertNotNull(response.contatoAnunciante());
        assertEquals("ana@example.com", response.contatoAnunciante().email());
        assertNull(response.contatoAnunciante().emailInstitucional());
        verify(manifestacaoRepository).save(manifestacao);
    }

    @Test
    void deveExporEmailInstitucionalDoAnuncianteQuandoElePerUniversitario() {
        UsuarioUniversitario anuncianteUniv = new UsuarioUniversitario();
        anuncianteUniv.setId(anuncianteUsuarioId);
        anuncianteUniv.setNome("Carla Anunciante");
        anuncianteUniv.setEmail("carla@example.com");
        anuncianteUniv.setTelefone("+5583999990003");
        anuncianteUniv.setEmailInstitucional("carla@academico.ufpb.br");

        PerfilAnunciante perfilUniv = new PerfilAnunciante();
        perfilUniv.setId(UUID.randomUUID());
        perfilUniv.setUsuario(anuncianteUniv);
        perfilUniv.setAtivo(true);
        anuncio.setAnunciante(perfilUniv);

        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(any(ManifestacaoInteresse.class))).thenReturn(manifestacao);

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.aceitar(manifestacaoId, anuncianteUsuarioId);

        assertEquals("carla@academico.ufpb.br", response.contatoAnunciante().emailInstitucional());
    }

    @Test
    void naoDeveAceitarManifestacaoInexistente() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.empty());

        assertThrows(ManifestacaoInteresseNaoEncontradaException.class,
                () -> manifestacaoInteresseService.aceitar(manifestacaoId, anuncianteUsuarioId));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveAceitarQuandoAnuncianteForOutroUsuario() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(AcessoNegadoException.class,
                () -> manifestacaoInteresseService.aceitar(manifestacaoId, UUID.randomUUID()));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveAceitarQuandoManifestacaoNaoEstiverPendente() {
        manifestacao.setStatus(StatusManifestacaoInteresse.RECUSADA);
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(TransicaoInvalidaManifestacaoException.class,
                () -> manifestacaoInteresseService.aceitar(manifestacaoId, anuncianteUsuarioId));
        verify(manifestacaoRepository, never()).save(any());
    }

    // ---------- recusar ----------

    @Test
    void deveRecusarManifestacaoPendente() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(any(ManifestacaoInteresse.class))).thenReturn(manifestacao);

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.recusar(manifestacaoId, anuncianteUsuarioId);

        assertEquals(StatusManifestacaoInteresse.RECUSADA, response.status());
        assertNotNull(response.dataResposta());
        assertNull(response.contatoInteressado());
        assertNull(response.contatoAnunciante());
        verify(manifestacaoRepository).save(manifestacao);
    }

    @Test
    void naoDeveRecusarQuandoAnuncianteForOutroUsuario() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(AcessoNegadoException.class,
                () -> manifestacaoInteresseService.recusar(manifestacaoId, UUID.randomUUID()));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveRecusarQuandoManifestacaoNaoEstiverPendente() {
        manifestacao.setStatus(StatusManifestacaoInteresse.ACEITA);
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(TransicaoInvalidaManifestacaoException.class,
                () -> manifestacaoInteresseService.recusar(manifestacaoId, anuncianteUsuarioId));
        verify(manifestacaoRepository, never()).save(any());
    }

    // ---------- cancelar ----------

    @Test
    void deveCancelarManifestacaoPendentePeloInteressado() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));
        when(manifestacaoRepository.save(any(ManifestacaoInteresse.class))).thenReturn(manifestacao);

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.cancelar(manifestacaoId, interessadoId);

        assertEquals(StatusManifestacaoInteresse.CANCELADA, response.status());
        assertNotNull(response.dataResposta());
        assertNull(response.contatoInteressado());
        verify(manifestacaoRepository).save(manifestacao);
    }

    @Test
    void naoDeveCancelarQuandoSolicitanteNaoForOInteressado() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(AcessoNegadoException.class,
                () -> manifestacaoInteresseService.cancelar(manifestacaoId, UUID.randomUUID()));
        verify(manifestacaoRepository, never()).save(any());
    }

    @Test
    void naoDeveCancelarQuandoManifestacaoNaoEstiverPendente() {
        manifestacao.setStatus(StatusManifestacaoInteresse.CANCELADA);
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(TransicaoInvalidaManifestacaoException.class,
                () -> manifestacaoInteresseService.cancelar(manifestacaoId, interessadoId));
        verify(manifestacaoRepository, never()).save(any());
    }

    // ---------- listarPorAnuncio ----------

    @Test
    void deveListarManifestacoesDoAnuncioParaAnunciante() {
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);
        when(manifestacaoRepository.findByAnuncio_IdOrderByDataManifestacaoDesc(anuncioId))
                .thenReturn(List.of(manifestacao));

        List<ManifestacaoInteresseResponseDTO> result =
                manifestacaoInteresseService.listarPorAnuncio(anuncioId, anuncianteUsuarioId);

        assertEquals(1, result.size());
        assertEquals(manifestacaoId, result.get(0).id());
    }

    @Test
    void naoDeveListarManifestacoesQuandoSolicitanteNaoForAnunciante() {
        when(anuncioService.buscarEntidadePorId(anuncioId)).thenReturn(anuncio);

        assertThrows(AcessoNegadoException.class,
                () -> manifestacaoInteresseService.listarPorAnuncio(anuncioId, UUID.randomUUID()));
        verify(manifestacaoRepository, never()).findByAnuncio_IdOrderByDataManifestacaoDesc(any());
    }

    @Test
    void naoDeveListarPorAnuncioQuandoAnuncioNaoExistir() {
        when(anuncioService.buscarEntidadePorId(anuncioId))
                .thenThrow(new AnuncioNaoEncontradoException("Anuncio não encontrado"));

        assertThrows(AnuncioNaoEncontradoException.class,
                () -> manifestacaoInteresseService.listarPorAnuncio(anuncioId, anuncianteUsuarioId));
    }

    // ---------- listarPorInteressado ----------

    @Test
    void deveListarManifestacoesDoInteressado() {
        when(manifestacaoRepository.findByInteressado_IdOrderByDataManifestacaoDesc(interessadoId))
                .thenReturn(List.of(manifestacao));

        List<ManifestacaoInteresseResponseDTO> result =
                manifestacaoInteresseService.listarPorInteressado(interessadoId);

        assertEquals(1, result.size());
        assertEquals(manifestacaoId, result.get(0).id());
        assertEquals("Bruno Estudante", result.get(0).interessadoNome());
    }

    // ---------- buscarPorId ----------

    @Test
    void deveRetornarDetalheParaOInteressado() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.buscarPorId(manifestacaoId, interessadoId);

        assertEquals(manifestacaoId, response.id());
        assertNull(response.contatoInteressado());
        assertNull(response.contatoAnunciante());
    }

    @Test
    void deveRetornarDetalheParaOAnunciante() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.buscarPorId(manifestacaoId, anuncianteUsuarioId);

        assertEquals(manifestacaoId, response.id());
    }

    @Test
    void naoDeveRetornarDetalheParaUsuarioNaoRelacionado() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        assertThrows(AcessoNegadoException.class,
                () -> manifestacaoInteresseService.buscarPorId(manifestacaoId, UUID.randomUUID()));
    }

    @Test
    void naoDeveRetornarDetalheSeManifestacaoNaoExistir() {
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.empty());

        assertThrows(ManifestacaoInteresseNaoEncontradaException.class,
                () -> manifestacaoInteresseService.buscarPorId(manifestacaoId, interessadoId));
    }

    @Test
    void deveLiberarContatosQuandoStatusForAceita() {
        manifestacao.setStatus(StatusManifestacaoInteresse.ACEITA);
        manifestacao.setDataResposta(LocalDateTime.now());
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.buscarPorId(manifestacaoId, interessadoId);

        assertNotNull(response.contatoInteressado());
        assertEquals("Bruno Estudante", response.contatoInteressado().nome());
        assertEquals("bruno@example.com", response.contatoInteressado().email());
        assertEquals("bruno@academico.ufpb.br", response.contatoInteressado().emailInstitucional());
        assertNotNull(response.contatoAnunciante());
        assertEquals("Ana Locadora", response.contatoAnunciante().nome());
        assertNull(response.contatoAnunciante().emailInstitucional());
    }

    @Test
    void naoDeveLiberarContatosQuandoStatusForRecusada() {
        manifestacao.setStatus(StatusManifestacaoInteresse.RECUSADA);
        when(manifestacaoRepository.findById(manifestacaoId)).thenReturn(Optional.of(manifestacao));

        ManifestacaoInteresseDetalheResponseDTO response =
                manifestacaoInteresseService.buscarPorId(manifestacaoId, interessadoId);

        assertNull(response.contatoInteressado());
        assertNull(response.contatoAnunciante());
    }
}
