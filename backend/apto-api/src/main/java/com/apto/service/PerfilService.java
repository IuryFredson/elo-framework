package com.apto.service;

import com.apto.dto.request.AtualizarPerfilRequestDTO;
import com.apto.dto.response.PerfilResponseDTO;
import com.apto.exception.EmailInstitucionalJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.PerfilMapper;
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
    private final PerfilMapper perfilMapper;

    public PerfilService(UsuarioUniversitarioRepository repository,
                         UsuarioRepository usuarioRepository,
                         PerfilMapper perfilMapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.perfilMapper = perfilMapper;
    }

    public PerfilResponseDTO buscarPerfil(UUID id) {
        UsuarioUniversitario usuario = buscarUsuarioPorId(id);
        return perfilMapper.toResponseDTO(usuario);
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

        return perfilMapper.toResponseDTO(usuario);
    }

    private UsuarioUniversitario buscarUsuarioPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário universitário não encontrado com id: " + id
                        )
                );
    }

}
