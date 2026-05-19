package com.apto.service;

import com.apto.dto.response.PerfilAnuncianteResponseDTO;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.PerfilAnuncianteMapper;
import com.apto.model.entity.Locador;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.Genero;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilAnuncianteServiceTest {

    @Mock
    private PerfilAnuncianteRepository perfilAnuncianteRepository;

    @Mock
    private UsuarioUniversitarioRepository universitarioRepository;

    @Spy
    private PerfilAnuncianteMapper perfilAnuncianteMapper = new PerfilAnuncianteMapper();

    @InjectMocks
    private PerfilAnuncianteService perfilAnuncianteService;

    private UUID universitarioId;
    private UUID perfilId;
    private UsuarioUniversitario universitario;
    private PerfilAnunciante perfilExistente;

    @BeforeEach
    void setUp() {
        universitarioId = UUID.randomUUID();
        perfilId        = UUID.randomUUID();

        universitario = new UsuarioUniversitario();
        universitario.setId(universitarioId);
        universitario.setNome("Ana Subloca");
        universitario.setEmail("ana@email.com");
        universitario.setEmailInstitucional("ana@univ.edu");
        universitario.setCurso("Engenharia");
        universitario.setDataNascimento(LocalDate.of(2001, 5, 10));
        universitario.setGenero(Genero.FEMININO);

        perfilExistente = new PerfilAnunciante();
        perfilExistente.setId(perfilId);
        perfilExistente.setUsuario(universitario);
        perfilExistente.setAtivo(true);
    }

    @Test
    void deveCriarPerfilAnuncianteParaUniversitarioSemPerfil() {
        when(universitarioRepository.findById(universitarioId))
                .thenReturn(Optional.of(universitario));
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.empty());
        when(perfilAnuncianteRepository.save(any(PerfilAnunciante.class)))
                .thenAnswer(i -> {
                    PerfilAnunciante p = i.getArgument(0);
                    p.setId(UUID.randomUUID());
                    return p;
                });

        PerfilAnuncianteResponseDTO response =
                perfilAnuncianteService.habilitarAnunciante(universitarioId);

        assertNotNull(response);
        assertEquals(universitarioId, response.usuarioId());
        assertTrue(response.ativo());

        ArgumentCaptor<PerfilAnunciante> captor = ArgumentCaptor.forClass(PerfilAnunciante.class);
        verify(perfilAnuncianteRepository).save(captor.capture());
        assertTrue(captor.getValue().isAtivo());
        assertEquals(universitario, captor.getValue().getUsuario());
    }

    @Test
    void deveReativarPerfilAnuncianteInativo() {
        perfilExistente.setAtivo(false);

        when(universitarioRepository.findById(universitarioId))
                .thenReturn(Optional.of(universitario));
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.of(perfilExistente));
        when(perfilAnuncianteRepository.save(any(PerfilAnunciante.class)))
                .thenAnswer(i -> i.getArgument(0));

        PerfilAnuncianteResponseDTO response =
                perfilAnuncianteService.habilitarAnunciante(universitarioId);

        assertTrue(response.ativo());
        verify(perfilAnuncianteRepository).save(perfilExistente);
    }

    @Test
    void naoDeveHabilitarAnuncianteSeUniversitarioNaoExistir() {
        when(universitarioRepository.findById(universitarioId)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
                () -> perfilAnuncianteService.habilitarAnunciante(universitarioId));
        verify(perfilAnuncianteRepository, never()).save(any());
    }

    @Test
    void deveManterPerfilAtivoAoHabilitarUniversitarioQueJaEraAnunciante() {
        when(universitarioRepository.findById(universitarioId))
                .thenReturn(Optional.of(universitario));
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.of(perfilExistente));
        when(perfilAnuncianteRepository.save(any(PerfilAnunciante.class)))
                .thenAnswer(i -> i.getArgument(0));

        PerfilAnuncianteResponseDTO response =
                perfilAnuncianteService.habilitarAnunciante(universitarioId);

        assertTrue(response.ativo());
        verify(perfilAnuncianteRepository).save(perfilExistente);
    }

    @Test
    void deveDesabilitarAnuncianteComPerfilAtivo() {
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.of(perfilExistente));
        when(perfilAnuncianteRepository.save(any(PerfilAnunciante.class)))
                .thenAnswer(i -> i.getArgument(0));

        PerfilAnuncianteResponseDTO response =
                perfilAnuncianteService.desabilitarAnunciante(universitarioId);

        assertFalse(response.ativo());

        ArgumentCaptor<PerfilAnunciante> captor = ArgumentCaptor.forClass(PerfilAnunciante.class);
        verify(perfilAnuncianteRepository).save(captor.capture());
        assertFalse(captor.getValue().isAtivo());
    }

    @Test
    void naoDeveDesabilitarAnuncianteQueNaoPossuiPerfil() {
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.empty());

        assertThrows(AnuncianteNaoEncontradoException.class,
                () -> perfilAnuncianteService.desabilitarAnunciante(universitarioId));
        verify(perfilAnuncianteRepository, never()).save(any());
    }

    @Test
    void devePreservarPerfilNoBancoAoDesabilitarMantendoHistoricoReputacao() {
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.of(perfilExistente));
        when(perfilAnuncianteRepository.save(any(PerfilAnunciante.class)))
                .thenAnswer(i -> i.getArgument(0));

        perfilAnuncianteService.desabilitarAnunciante(universitarioId);

        verify(perfilAnuncianteRepository, never()).delete(any());
        verify(perfilAnuncianteRepository, never()).deleteById(any());
        verify(perfilAnuncianteRepository).save(any());
    }


    @Test
    void deveBuscarPerfilAnunciantePorUsuario() {
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.of(perfilExistente));

        PerfilAnuncianteResponseDTO response =
                perfilAnuncianteService.buscarPorUsuario(universitarioId);

        assertNotNull(response);
        assertEquals(perfilId, response.id());
        assertEquals(universitarioId, response.usuarioId());
        assertEquals("Ana Subloca", response.nomeUsuario());
        assertTrue(response.ativo());
    }

    @Test
    void naoDeveBuscarPerfilAnuncianteDeUsuarioSemPerfil() {
        when(perfilAnuncianteRepository.findByUsuario_Id(universitarioId))
                .thenReturn(Optional.empty());

        assertThrows(AnuncianteNaoEncontradoException.class,
                () -> perfilAnuncianteService.buscarPorUsuario(universitarioId));
    }

    @Test
    void deveBuscarPerfilDeLocadorPeloUsuarioId() {
        Locador locador = new Locador();
        locador.setId(UUID.randomUUID());
        locador.setNome("Carlos Locador");
        locador.setEmail("carlos@email.com");
        locador.setDocumentoIdentificacao("12345678900");
        locador.setNomeExibicaoOuRazao("Carlos Imóveis");

        PerfilAnunciante perfilLocador = new PerfilAnunciante();
        perfilLocador.setId(UUID.randomUUID());
        perfilLocador.setUsuario(locador);
        perfilLocador.setAtivo(true);

        when(perfilAnuncianteRepository.findByUsuario_Id(locador.getId()))
                .thenReturn(Optional.of(perfilLocador));

        PerfilAnuncianteResponseDTO response =
                perfilAnuncianteService.buscarPorUsuario(locador.getId());

        assertNotNull(response);
        assertEquals("Carlos Locador", response.nomeUsuario());
        assertTrue(response.ativo());
    }
}
