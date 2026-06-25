package com.apto.service;

import com.apto.dto.request.CriarDenunciaRequestDTO;
import com.apto.dto.response.DenunciaResponseDTO;
import com.apto.exception.*;
import com.apto.mapper.DenunciaMapper;
import com.apto.model.entity.*;
import com.apto.model.enums.StatusDenuncia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.DenunciaRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DenunciaServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;
    @Mock
    private UsuarioUniversitarioRepository universitarioRepository;
    @Mock
    private LocadorRepository locadorRepository;
    @Mock
    private AnuncioRepository anuncioRepository;

    @Spy
    private DenunciaMapper denunciaMapper = new DenunciaMapper();

    @InjectMocks
    private DenunciaService denunciaService;

    private UUID denuncianteId;
    private UUID anuncioId;
    private UUID denunciaId;

    private Locador denunciante;
    private Anuncio anuncio;
    private Denuncia denuncia;

    private CriarDenunciaRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        denuncianteId = UUID.randomUUID();
        anuncioId = UUID.randomUUID();
        denunciaId = UUID.randomUUID();

        denunciante = new Locador();
        denunciante.setId(denuncianteId);
        denunciante.setNome("João Silva");

        anuncio = new Anuncio();
        anuncio.setId(anuncioId);

        denuncia = new Denuncia();
        denuncia.setId(denunciaId);
        denuncia.setDenunciante(denunciante);
        denuncia.setAnuncio(anuncio);
        denuncia.setTitulo("Anúncio enganoso");
        denuncia.setCorpo("O anúncio contém informações falsas sobre o imóvel.");
        denuncia.setStatusDenuncia(StatusDenuncia.PENDENTE);

        criarDTO = new CriarDenunciaRequestDTO(
                denuncianteId,
                anuncioId,
                "Anúncio enganoso",
                "O anúncio contém informações falsas sobre o imóvel."
        );
    }

    @Test
    void deveCriarDenunciaComDadosValidos() {
        when(locadorRepository.findById(denuncianteId)).thenReturn(Optional.of(denunciante));
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.criar(criarDTO);

        assertNotNull(response);
        assertEquals(denunciaId, response.id());
        assertEquals(StatusDenuncia.PENDENTE, response.statusDenuncia());
        verify(denunciaRepository).save(any(Denuncia.class));
    }

    @Test
    void deveCriarDenunciaQuandoDenuncianteEhUniversitario() {
        UsuarioUniversitario universitario = new UsuarioUniversitario();
        universitario.setId(denuncianteId);
        denuncia.setDenunciante(universitario);

        when(locadorRepository.findById(denuncianteId)).thenReturn(Optional.empty());
        when(universitarioRepository.findById(denuncianteId)).thenReturn(Optional.of(universitario));
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.criar(criarDTO);

        assertNotNull(response);
        verify(denunciaRepository).save(any(Denuncia.class));
    }

    @Test
    void naoDeveCriarDenunciaSeUsuarioNaoEncontrado() {
        when(locadorRepository.findById(denuncianteId)).thenReturn(Optional.empty());
        when(universitarioRepository.findById(denuncianteId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> denunciaService.criar(criarDTO));
        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void naoDeveCriarDenunciaSeAnuncioNaoEncontrado() {
        when(locadorRepository.findById(denuncianteId)).thenReturn(Optional.of(denunciante));
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(AnuncioNaoEncontradoException.class, () -> denunciaService.criar(criarDTO));
        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void deveListarTodasDenuncias() {
        when(denunciaRepository.findAll()).thenReturn(List.of(denuncia));

        List<DenunciaResponseDTO> result = denunciaService.listarTodas();

        assertEquals(1, result.size());
        verify(denunciaRepository).findAll();
    }

    @Test
    void deveBuscarDenunciaPorId() {
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));

        DenunciaResponseDTO response = denunciaService.buscarPorId(denunciaId);

        assertNotNull(response);
        assertEquals(denunciaId, response.id());
    }

    @Test
    void naoDeveBuscarDenunciaComIdInexistente() {
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.empty());

        assertThrows(DenunciaNaoEncontradaException.class, () -> denunciaService.buscarPorId(denunciaId));
    }

    @Test
    void deveBuscarDenunciasPorAnuncio() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.of(anuncio));
        when(denunciaRepository.findByAnuncio(anuncio)).thenReturn(List.of(denuncia));

        List<DenunciaResponseDTO> result = denunciaService.buscarPorAnuncioId(anuncioId);

        assertEquals(1, result.size());
    }

    @Test
    void naoDeveBuscarDenunciasPorAnuncioInexistente() {
        when(anuncioRepository.findById(anuncioId)).thenReturn(Optional.empty());

        assertThrows(AnuncioNaoEncontradoException.class, () -> denunciaService.buscarPorAnuncioId(anuncioId));
        verify(denunciaRepository, never()).findByAnuncio(any());
    }


    @Test
    void deveBuscarDenunciasPorUsuario() {
        when(locadorRepository.findById(denuncianteId)).thenReturn(Optional.of(denunciante));
        when(denunciaRepository.findByDenunciante(denunciante)).thenReturn(List.of(denuncia));

        List<DenunciaResponseDTO> result = denunciaService.buscarPorUsuarioId(denuncianteId);

        assertEquals(1, result.size());
    }

    @Test
    void naoDeveBuscarDenunciasPorUsuarioInexistente() {
        when(locadorRepository.findById(denuncianteId)).thenReturn(Optional.empty());
        when(universitarioRepository.findById(denuncianteId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> denunciaService.buscarPorUsuarioId(denuncianteId));
        verify(denunciaRepository, never()).findByDenunciante(any());
    }

    @Test
    void deveBuscarDenunciasPorStatus() {
        when(denunciaRepository.findByStatusDenuncia(StatusDenuncia.PENDENTE)).thenReturn(List.of(denuncia));

        List<DenunciaResponseDTO> result = denunciaService.buscarPorStatus(StatusDenuncia.PENDENTE);

        assertEquals(1, result.size());
        verify(denunciaRepository).findByStatusDenuncia(StatusDenuncia.PENDENTE);
    }

    @Test
    void deveAtualizarStatusDePendenteParaEmAnalise() {
        denuncia.setStatusDenuncia(StatusDenuncia.PENDENTE);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.atualizarStatus(denunciaId, StatusDenuncia.EM_ANALISE);

        assertNotNull(response);
        verify(denunciaRepository).save(denuncia);
    }

    @Test
    void deveAtualizarStatusDeEmAnaliseParaProcedente() {
        denuncia.setStatusDenuncia(StatusDenuncia.EM_ANALISE);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.atualizarStatus(denunciaId, StatusDenuncia.PROCEDENTE);

        assertNotNull(response);
        verify(denunciaRepository).save(denuncia);
    }

    @Test
    void deveAtualizarStatusDeEmAnaliseParaImprocedente() {
        denuncia.setStatusDenuncia(StatusDenuncia.EM_ANALISE);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.atualizarStatus(denunciaId, StatusDenuncia.IMPROCEDENTE);

        assertNotNull(response);
        verify(denunciaRepository).save(denuncia);
    }

    @Test
    void deveAtualizarStatusDeProcedenteParaArquivada() {
        denuncia.setStatusDenuncia(StatusDenuncia.PROCEDENTE);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.atualizarStatus(denunciaId, StatusDenuncia.ARQUIVADA);

        assertNotNull(response);
        verify(denunciaRepository).save(denuncia);
    }

    @Test
    void deveAtualizarStatusDeImprocedenteParaArquivada() {
        denuncia.setStatusDenuncia(StatusDenuncia.IMPROCEDENTE);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));
        when(denunciaRepository.save(any(Denuncia.class))).thenReturn(denuncia);

        DenunciaResponseDTO response = denunciaService.atualizarStatus(denunciaId, StatusDenuncia.ARQUIVADA);

        assertNotNull(response);
        verify(denunciaRepository).save(denuncia);
    }

    @Test
    void naoDeveAtualizarStatusComTransicaoInvalidaDePendenteParaProcedente() {
        denuncia.setStatusDenuncia(StatusDenuncia.PENDENTE);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));

        assertThrows(TransicaoInvalidaStatusException.class,
                () -> denunciaService.atualizarStatus(denunciaId, StatusDenuncia.PROCEDENTE));
        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarStatusComTransicaoInvalidaDeArquivadaParaQualquerStatus() {
        denuncia.setStatusDenuncia(StatusDenuncia.ARQUIVADA);
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));

        assertThrows(TransicaoInvalidaStatusException.class,
                () -> denunciaService.atualizarStatus(denunciaId, StatusDenuncia.PENDENTE));
        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void naoDeveAtualizarStatusCasoDenunciaNaoExista() {
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.empty());

        assertThrows(DenunciaNaoEncontradaException.class,
                () -> denunciaService.atualizarStatus(denunciaId, StatusDenuncia.EM_ANALISE));
        verify(denunciaRepository, never()).save(any());
    }

    @Test
    void deveDeletarDenunciaValida() {
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));

        denunciaService.deletar(denunciaId);

        verify(denunciaRepository).delete(denuncia);
    }

    @Test
    void naoDeveDeletarDenunciaInexistente() {
        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.empty());

        assertThrows(DenunciaNaoEncontradaException.class, () -> denunciaService.deletar(denunciaId));
        verify(denunciaRepository, never()).delete(any());
    }
}
