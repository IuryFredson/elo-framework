package com.apto.service;

import com.apto.dto.request.AtualizarPerfilRequestDTO;
import com.apto.dto.response.PerfilResponseDTO;
import com.apto.exception.EmailInstitucionalJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.model.entity.PerfilConvivencia;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.UsuarioRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PerfilService {

    private final UsuarioUniversitarioRepository repository;
    private final UsuarioRepository usuarioRepository;

    public PerfilService(UsuarioUniversitarioRepository repository,
                         UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public PerfilResponseDTO buscarPerfil(UUID id) {
        UsuarioUniversitario usuario = buscarUsuarioPorId(id);
        return toResponseDTO(usuario);
    }

    public PerfilResponseDTO atualizarPerfil(UUID id, AtualizarPerfilRequestDTO dto) {
        UsuarioUniversitario usuario = buscarUsuarioPorId(id);

        if (!usuario.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("Já existe usuário com o email: " + dto.email());
        }

        if (!usuario.getEmailInstitucional().equals(dto.emailInstitucional())
                && repository.existsByEmailInstitucional(dto.emailInstitucional())) {
            throw new EmailInstitucionalJaCadastradoException(
                    "Já existe usuário com o email institucional: " + dto.emailInstitucional()
            );
        }

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setEmailInstitucional(dto.emailInstitucional());
        usuario.setTelefone(dto.telefone());
        usuario.setCurso(dto.curso());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGenero(dto.genero());

        PerfilConvivencia perfil = usuario.getPerfilConvivencia();

        if (perfil == null) {
            perfil = new PerfilConvivencia();
            usuario.setPerfilConvivencia(perfil);
        }

        perfil.setHorarioSono(dto.horarioSono());
        perfil.setNivelBarulhoAceitavel(dto.nivelBarulhoAceitavel());
        perfil.setFrequenciaVisitas(dto.frequenciaVisitas());
        perfil.setNivelOrganizacao(dto.nivelOrganizacao());
        perfil.setRotinaEstudos(dto.rotinaEstudos());
        perfil.setConsomeAlcool(dto.consomeAlcool());
        perfil.setFumante(dto.fumante());
        perfil.setAceitaAnimais(dto.aceitaAnimais());
        perfil.setPreferenciaGeneroConvivencia(dto.preferenciaGeneroConvivencia());
        perfil.setDescricaoLivre(dto.descricaoLivre());

        repository.save(usuario);

        return toResponseDTO(usuario);
    }

    private UsuarioUniversitario buscarUsuarioPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário universitário não encontrado com id: " + id
                        )
                );
    }

    private PerfilResponseDTO toResponseDTO(UsuarioUniversitario usuario) {

        PerfilConvivencia perfil = usuario.getPerfilConvivencia();

        return new PerfilResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getEmailInstitucional(),
                usuario.getTelefone(),
                usuario.getCurso(),
                usuario.getDataNascimento(),
                usuario.getGenero(),

                perfil != null ? perfil.getHorarioSono() : null,
                perfil != null ? perfil.getNivelBarulhoAceitavel() : null,
                perfil != null ? perfil.getFrequenciaVisitas() : null,
                perfil != null ? perfil.getNivelOrganizacao() : null,
                perfil != null ? perfil.getRotinaEstudos() : null,
                perfil != null ? perfil.getConsomeAlcool() : null,
                perfil != null ? perfil.getFumante() : null,
                perfil != null ? perfil.getAceitaAnimais() : null,
                perfil != null ? perfil.getPreferenciaGeneroConvivencia() : null,
                perfil != null ? perfil.getDescricaoLivre() : null
        );
    }
}
