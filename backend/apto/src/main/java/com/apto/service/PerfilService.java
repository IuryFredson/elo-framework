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
public class PerfilService extends com.elo.perfil.PerfilService<UsuarioUniversitario, PerfilConvivencia, AtualizarPerfilRequestDTO, PerfilResponseDTO> {

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

    @Override
    protected UsuarioUniversitario buscarDonoPerfil(UUID usuarioId) {
        return repository.findById(usuarioId)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuario universitario nao encontrado com id: " + usuarioId
                        )
                );
    }

    @Override
    protected PerfilConvivencia obterPerfil(UsuarioUniversitario usuario) {
        return usuario.getPerfilConvivencia();
    }

    @Override
    protected PerfilConvivencia criarPerfil(AtualizarPerfilRequestDTO dto) {
        return new PerfilConvivencia();
    }

    @Override
    protected void associarPerfil(UsuarioUniversitario usuario, PerfilConvivencia perfil) {
        usuario.setPerfilConvivencia(perfil);
    }

    @Override
    protected void validarAtualizacao(UsuarioUniversitario usuario, AtualizarPerfilRequestDTO dto) {
        if (!usuario.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("Ja existe usuario com o email: " + dto.email());
        }

        if (!usuario.getEmailInstitucional().equals(dto.emailInstitucional())
                && repository.existsByEmailInstitucional(dto.emailInstitucional())) {
            throw new EmailInstitucionalJaCadastradoException(
                    "Ja existe usuario com o email institucional: " + dto.emailInstitucional()
            );
        }
    }

    @Override
    protected void aplicarDadosDonoPerfil(UsuarioUniversitario usuario, AtualizarPerfilRequestDTO dto) {
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setEmailInstitucional(dto.emailInstitucional());
        usuario.setTelefone(dto.telefone());
        usuario.setCurso(dto.curso());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGenero(dto.genero());
    }

    @Override
    protected void aplicarDadosPerfil(PerfilConvivencia perfil, AtualizarPerfilRequestDTO dto) {
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
    }

    @Override
    protected UsuarioUniversitario salvarDonoPerfil(UsuarioUniversitario usuario) {
        repository.save(usuario);
        return usuario;
    }

    @Override
    protected PerfilResponseDTO mapearResposta(UsuarioUniversitario usuario, PerfilConvivencia perfil) {
        return perfilMapper.toResponseDTO(usuario);
    }
}
