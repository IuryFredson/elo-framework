package com.apto.service;

import com.apto.dto.request.AtualizarPerfilRequestDTO;
import com.apto.dto.response.PerfilResponseDTO;
import com.apto.exception.EmailInstitucionalJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.mapper.PerfilMapper;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.model.enums.FrequenciaVisitas;
import com.apto.model.enums.Genero;
import com.apto.model.enums.HorarioSono;
import com.apto.model.enums.NivelBarulho;
import com.apto.model.enums.NivelOrganizacao;
import com.apto.model.enums.PreferenciaGeneroConvivencia;
import com.apto.model.enums.RotinaEstudos;
import com.apto.repository.UsuarioRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private UsuarioUniversitarioRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private PerfilService service;

    private UUID usuarioId;
    private UsuarioUniversitario usuario;
    private AtualizarPerfilRequestDTO atualizarDTO;

    @BeforeEach
    void setUp() {
        service = new PerfilService(repository, usuarioRepository, new PerfilMapper());

        usuarioId = UUID.randomUUID();

        usuario = new UsuarioUniversitario();
        usuario.setId(usuarioId);
        usuario.setNome("Maria");
        usuario.setEmail("maria@email.com");
        usuario.setEmailInstitucional("maria@universidade.edu");
        usuario.setTelefone("85999999999");
        usuario.setCurso("Computação");
        usuario.setDataNascimento(LocalDate.of(2001, 3, 12));
        usuario.setGenero(Genero.FEMININO);

        atualizarDTO = new AtualizarPerfilRequestDTO(
                "Maria Silva",
                "maria.novo@email.com",
                "maria.novo@universidade.edu",
                "85888888888",
                "Engenharia de Software",
                LocalDate.of(2001, 3, 12),
                Genero.FEMININO,
                HorarioSono.CEDO,
                NivelBarulho.MEDIO,
                FrequenciaVisitas.AS_VEZES,
                NivelOrganizacao.ALTO,
                RotinaEstudos.MISTA,
                false,
                false,
                true,
                PreferenciaGeneroConvivencia.SEM_PREFERENCIA,
                "Prefiro ambiente tranquilo"
        );
    }

    @Test
    void deveAtualizarPerfilECriarPerfilConvivenciaQuandoAusente() {
        when(repository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(atualizarDTO.email())).thenReturn(false);
        when(repository.existsByEmailInstitucional(atualizarDTO.emailInstitucional())).thenReturn(false);
        when(repository.save(usuario)).thenReturn(usuario);

        PerfilResponseDTO response = service.atualizarPerfil(usuarioId, atualizarDTO);

        assertEquals("Maria Silva", response.nome());
        assertEquals("maria.novo@email.com", response.email());
        assertEquals("maria.novo@universidade.edu", response.emailInstitucional());
        assertNotNull(usuario.getPerfilConvivencia());
        assertEquals(HorarioSono.CEDO, response.horarioSono());
        verify(repository).save(usuario);
    }

    @Test
    void deveAtualizarPerfilMantendoMesmoEmailSemValidarDuplicidade() {
        usuario.setPerfilConvivencia(new PerfilConvivencia());
        AtualizarPerfilRequestDTO dto = new AtualizarPerfilRequestDTO(
                "Maria Silva",
                usuario.getEmail(),
                usuario.getEmailInstitucional(),
                "85888888888",
                "Engenharia de Software",
                LocalDate.of(2001, 3, 12),
                Genero.FEMININO,
                HorarioSono.CEDO,
                NivelBarulho.MEDIO,
                FrequenciaVisitas.AS_VEZES,
                NivelOrganizacao.ALTO,
                RotinaEstudos.MISTA,
                false,
                false,
                true,
                PreferenciaGeneroConvivencia.SEM_PREFERENCIA,
                "Prefiro ambiente tranquilo"
        );

        when(repository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        PerfilResponseDTO response = service.atualizarPerfil(usuarioId, dto);

        assertEquals(usuario.getEmail(), response.email());
        assertEquals(usuario.getEmailInstitucional(), response.emailInstitucional());
        verify(usuarioRepository, never()).existsByEmail(usuario.getEmail());
        verify(repository, never()).existsByEmailInstitucional(usuario.getEmailInstitucional());
        verify(repository).save(usuario);
    }

    @Test
    void naoDeveAtualizarPerfilComEmailJaCadastrado() {
        when(repository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(atualizarDTO.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class,
                () -> service.atualizarPerfil(usuarioId, atualizarDTO));
        verify(repository, never()).save(usuario);
    }

    @Test
    void naoDeveAtualizarPerfilComEmailInstitucionalJaCadastrado() {
        when(repository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail(atualizarDTO.email())).thenReturn(false);
        when(repository.existsByEmailInstitucional(atualizarDTO.emailInstitucional())).thenReturn(true);

        assertThrows(EmailInstitucionalJaCadastradoException.class,
                () -> service.atualizarPerfil(usuarioId, atualizarDTO));
        verify(repository, never()).save(usuario);
    }
}
