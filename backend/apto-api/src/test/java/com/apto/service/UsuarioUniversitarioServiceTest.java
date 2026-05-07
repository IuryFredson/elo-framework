package com.apto.service;

import com.apto.dto.request.CriarUsuarioUniversitarioRequestDTO;
import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.exception.EmailInstitucionalJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.Genero;
import com.apto.repository.UsuarioRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioUniversitarioServiceTest {

    @Mock
    private UsuarioUniversitarioRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioUniversitarioService service;
    private CriarUsuarioUniversitarioRequestDTO criarDTO;

    @BeforeEach
    void setUp() {
        service = new UsuarioUniversitarioService(repository, usuarioRepository);
        criarDTO = new CriarUsuarioUniversitarioRequestDTO(
                "Maria",
                "maria@email.com",
                "85999999999",
                "maria@universidade.edu",
                "Computação",
                LocalDate.of(2001, 3, 12),
                Genero.FEMININO
        );
    }

    @Test
    void deveCriarUsuarioUniversitarioComEmailDisponivel() {
        UsuarioUniversitario usuarioSalvo = new UsuarioUniversitario();
        usuarioSalvo.setId(UUID.randomUUID());
        usuarioSalvo.setNome(criarDTO.nome());
        usuarioSalvo.setEmail(criarDTO.email());
        usuarioSalvo.setTelefone(criarDTO.telefone());
        usuarioSalvo.setAtivo(true);
        usuarioSalvo.setEmailInstitucional(criarDTO.emailInstitucional());
        usuarioSalvo.setCurso(criarDTO.curso());
        usuarioSalvo.setDataNascimento(criarDTO.dataNascimento());
        usuarioSalvo.setGenero(criarDTO.genero());

        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(false);
        when(repository.existsByEmailInstitucional(criarDTO.emailInstitucional())).thenReturn(false);
        when(repository.save(any(UsuarioUniversitario.class))).thenReturn(usuarioSalvo);

        UsuarioUniversitarioResponseDTO response = service.criar(criarDTO);

        assertEquals(usuarioSalvo.getId(), response.id());
        assertEquals(criarDTO.email(), response.email());
        verify(repository).save(any(UsuarioUniversitario.class));
    }

    @Test
    void naoDeveCriarUsuarioUniversitarioComEmailJaUsadoPorQualquerUsuario() {
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> service.criar(criarDTO));
        verify(repository, never()).save(any());
        verify(repository, never()).existsByEmailInstitucional(criarDTO.emailInstitucional());
    }

    @Test
    void naoDeveCriarUsuarioUniversitarioComEmailInstitucionalJaCadastrado() {
        when(usuarioRepository.existsByEmail(criarDTO.email())).thenReturn(false);
        when(repository.existsByEmailInstitucional(criarDTO.emailInstitucional())).thenReturn(true);

        assertThrows(EmailInstitucionalJaCadastradoException.class, () -> service.criar(criarDTO));
        verify(repository, never()).save(any());
    }
}
