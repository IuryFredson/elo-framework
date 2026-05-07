package com.apto.service;

import com.apto.dto.request.CriarLocadorRequestDTO;
import com.apto.dto.response.LocadorResponseDTO;
import com.apto.exception.DocumentoIdentificacaoJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.model.entity.Locador;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocadorServiceTest {

    @Mock
    private LocadorRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private LocadorService service;
    private CriarLocadorRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        service = new LocadorService(repository, usuarioRepository);
        criarDTO = new CriarLocadorRequestDTO(
                "Carlos",
                "carlos@email.com",
                "85999999999",
                "12345678900",
                "Carlos Imóveis"
        );
    }

    @Test
    void deveCriarLocadorComEmailDisponivel() {
        Locador locadorSalvo = new Locador();
        locadorSalvo.setId(UUID.randomUUID());
        locadorSalvo.setNome(criarDTO.nome());
        locadorSalvo.setEmail(criarDTO.email());
        locadorSalvo.setTelefone(criarDTO.telefone());
        locadorSalvo.setAtivo(true);
        locadorSalvo.setDocumentoIdentificacao(criarDTO.documentoIdentificacao());
        locadorSalvo.setNomeExibicaoOuRazao(criarDTO.nomeExibicaoOuRazao());

        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(false);
        when(repository.existsByDocumentoIdentificacao(criarDTO.documentoIdentificacao())).thenReturn(false);
        when(repository.save(any(Locador.class))).thenReturn(locadorSalvo);

        LocadorResponseDTO response = service.criar(criarDTO);

        assertEquals(locadorSalvo.getId(), response.id());
        assertEquals(criarDTO.email(), response.email());
        verify(repository).save(any(Locador.class));
    }

    @Test
    void naoDeveCriarLocadorComEmailJaUsadoPorQualquerUsuario() {
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> service.criar(criarDTO));
        verify(repository, never()).save(any());
        verify(repository, never()).existsByDocumentoIdentificacao(criarDTO.documentoIdentificacao());
    }

    @Test
    void naoDeveCriarLocadorComDocumentoJaCadastrado() {
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(false);
        when(repository.existsByDocumentoIdentificacao(criarDTO.documentoIdentificacao())).thenReturn(true);

        assertThrows(DocumentoIdentificacaoJaCadastradoException.class, () -> service.criar(criarDTO));
        verify(repository, never()).save(any());
    }
}
