package com.apto.service;

import com.apto.dto.request.ModerarDenunciaRequestDTO;
import com.apto.dto.response.ModeracaoResponseDTO;
import com.apto.mapper.ModeracaoMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.model.enums.AcaoModeracaoAnuncio;
import com.apto.model.enums.StatusAnuncio;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.DenunciaRepository;
import com.elo.denuncia.StatusDenuncia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModeracaoServiceTest {

    @Mock
    private DenunciaRepository denunciaRepository;

    @Mock
    private AnuncioRepository anuncioRepository;

    @Mock
    private ManifestacaoInteresseService manifestacaoInteresseService;

    @Spy
    private ModeracaoMapper moderacaoMapper = new ModeracaoMapper();

    @InjectMocks
    private ModeracaoService moderacaoService;

    private UUID denunciaId;
    private UUID anuncioId;
    private Denuncia denuncia;
    private Anuncio anuncio;

    @BeforeEach
    void setUp() {
        denunciaId = UUID.randomUUID();
        anuncioId = UUID.randomUUID();

        anuncio = new Anuncio();
        anuncio.setId(anuncioId);
        anuncio.setStatus(StatusAnuncio.ATIVO);

        denuncia = new Denuncia();
        denuncia.setId(denunciaId);
        denuncia.setAnuncio(anuncio);
        denuncia.setStatusDenuncia(StatusDenuncia.EM_ANALISE);
    }

    @Test
    void deveCancelarManifestacoesPendentesAoPausarAnuncioPorModeracaoProcedente() {
        ModerarDenunciaRequestDTO dto = new ModerarDenunciaRequestDTO(
                StatusDenuncia.PROCEDENTE,
                AcaoModeracaoAnuncio.PAUSAR_ANUNCIO,
                "Denúncia procedente");

        when(denunciaRepository.findById(denunciaId)).thenReturn(Optional.of(denuncia));
        when(anuncioRepository.save(anuncio)).thenReturn(anuncio);
        when(denunciaRepository.save(denuncia)).thenReturn(denuncia);

        ModeracaoResponseDTO response = moderacaoService.moderar(denunciaId, dto);

        assertNotNull(response);
        assertEquals(StatusDenuncia.PROCEDENTE, denuncia.getStatusDenuncia());
        assertEquals(StatusAnuncio.PAUSADO, anuncio.getStatus());
        verify(manifestacaoInteresseService).cancelarPendentesDaOferta(anuncioId);
        verify(anuncioRepository).save(anuncio);
        verify(denunciaRepository).save(denuncia);
    }
}
