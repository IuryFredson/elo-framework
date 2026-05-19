package com.apto.service;

import com.apto.dto.request.AtualizarMoradiaRequestDTO;
import com.apto.dto.request.CriarMoradiaRequestDTO;
import com.apto.dto.response.MoradiaResponseDTO;
import com.apto.exception.MoradiaAssociadaComAnuncioException;
import com.apto.exception.MoradiaNaoEncontradaException;
import com.apto.mapper.MoradiaMapper;
import com.apto.model.entity.Moradia;
import com.apto.model.enums.TipoMoradia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.MoradiaRepository;
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
public class MoradiaServiceTest {

    @Mock
    private MoradiaRepository moradiaRepository;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Spy
    private MoradiaMapper moradiaMapper = new MoradiaMapper();

    @InjectMocks
    private MoradiaService moradiaService;

    private UUID moradiaId;
    private Moradia moradia;
    private CriarMoradiaRequestDTO criarDTO;
    private AtualizarMoradiaRequestDTO atualizarDTO;

    @BeforeEach
    void setUp() {
        moradiaId = UUID.randomUUID();
        moradia = new Moradia();
        moradia.setId(moradiaId);
        moradia.setTipoMoradia(TipoMoradia.APARTAMENTO);
        moradia.setBairro("Centro");
        moradia.setEnderecoResumo("Rua A, 123");
        moradia.setMobiliado(true);
        moradia.setAceitaAnimais(false);
        moradia.setQuantidadeVagas(1);
        moradia.setRegrasMoradia("Sem festas");

        criarDTO = new CriarMoradiaRequestDTO(
                TipoMoradia.APARTAMENTO,
                "Centro",
                "Rua dos abacates, 123",
                true,
                false,
                1,
                "Sem fumantes e sem barulho depois das 22h"
        );

        atualizarDTO = new AtualizarMoradiaRequestDTO(
                TipoMoradia.APARTAMENTO,
                "Ponta Negra",
                "Rua B, 456",
                false,
                true,
                2,
                "Proibído fazer barulho excessivo após as 22h, incluindo som alto e festas"
        );
    }

    @Test
    void deveCriarMoradiaComDadosValidos() {
        when(moradiaRepository.save(any(Moradia.class))).thenReturn(moradia);

        MoradiaResponseDTO response = moradiaService.criar(criarDTO);

        assertNotNull(response);
        assertEquals(moradiaId, response.id());
        assertEquals("Centro", response.bairro());
        verify(moradiaRepository).save(any(Moradia.class));
    }

    @Test
    void deveListarMoradias() {
        when(moradiaRepository.findAll()).thenReturn(List.of(moradia));

        var result = moradiaService.listarTodos();

        assertEquals(1, result.size());
        verify(moradiaRepository).findAll();
    }

    @Test
    void deveBuscarMoradiaPorIdValido() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.of(moradia));

        MoradiaResponseDTO response = moradiaService.buscarPorId(moradiaId);

        assertNotNull(response);
        assertEquals(moradiaId, response.id());
    }

    @Test
    void naoDeveBuscarMoradiaPorIdInexistente() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.empty());

        assertThrows(MoradiaNaoEncontradaException.class,
                () -> moradiaService.buscarPorId(moradiaId));
    }

    @Test
    void deveAtualizarMoradiaComDadosValidos() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.of(moradia));
        when(moradiaRepository.save(any(Moradia.class))).thenReturn(moradia);

        MoradiaResponseDTO response = moradiaService.atualizar(moradiaId, atualizarDTO);

        assertNotNull(response);
        verify(moradiaRepository).save(moradia);
    }

    @Test
    void naoDeveAtualizarMoradiaInexistente() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.empty());

        assertThrows(MoradiaNaoEncontradaException.class,
                () -> moradiaService.atualizar(moradiaId, atualizarDTO));
        verify(moradiaRepository, never()).save(any());
    }

    @Test
    void deveDeletarMoradiaSemAnuncioAssociado() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.of(moradia));
        when(anuncioRepository.existsByMoradia(moradia)).thenReturn(false);

        moradiaService.deletar(moradiaId);

        verify(moradiaRepository).delete(moradia);
    }

    @Test
    void naoDeveDeletarMoradiaComAnuncioAssociado() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.of(moradia));
        when(anuncioRepository.existsByMoradia(moradia)).thenReturn(true);

        assertThrows(MoradiaAssociadaComAnuncioException.class,
                () -> moradiaService.deletar(moradiaId));
        verify(moradiaRepository, never()).delete(any());
    }

    @Test
    void naoDeveDeletarMoradiaInexistente() {
        when(moradiaRepository.findById(moradiaId)).thenReturn(Optional.empty());

        assertThrows(MoradiaNaoEncontradaException.class,
                () -> moradiaService.deletar(moradiaId));
        verify(moradiaRepository, never()).delete(any());
    }
}
