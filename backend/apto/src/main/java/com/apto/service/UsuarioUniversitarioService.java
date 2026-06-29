package com.apto.service;

import com.apto.dto.request.AlterarStatusUsuarioRequestDTO;
import com.apto.dto.request.AtualizarUsuarioUniversitarioRequestDTO;
import com.apto.dto.request.CriarUsuarioUniversitarioRequestDTO;
import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.exception.EmailInstitucionalJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.UsuarioUniversitarioMapper;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.UsuarioRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import com.elo.usuario.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioUniversitarioService extends UsuarioService<UsuarioUniversitario, CriarUsuarioUniversitarioRequestDTO, AtualizarUsuarioUniversitarioRequestDTO, UsuarioUniversitarioResponseDTO> {

    private final UsuarioUniversitarioRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioUniversitarioMapper usuarioMapper;

    public UsuarioUniversitarioService(UsuarioUniversitarioRepository repository,
                                       UsuarioRepository usuarioRepository,
                                       UsuarioUniversitarioMapper usuarioMapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioUniversitarioResponseDTO alterarStatus(UUID id, AlterarStatusUsuarioRequestDTO dto) {
        return alterarStatus(id, dto.ativo());
    }

    @Override
    protected UsuarioUniversitario construirEntidade(CriarUsuarioUniversitarioRequestDTO dto) {
        return new UsuarioUniversitario();
    }

    @Override
    protected UsuarioUniversitarioRepository repositorio() {
        return repository;
    }

    @Override
    protected UsuarioUniversitarioMapper mapperResposta() {
        return usuarioMapper;
    }

    @Override
    protected RuntimeException erroUsuarioNaoEncontrado(UUID id) {
        return new UsuarioNaoEncontradoException("Usuario universitario nao encontrado com id: " + id);
    }

    @Override
    protected boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    protected RuntimeException erroEmailDuplicado(String email) {
        return new EmailJaCadastradoException("Ja existe usuario com o email: " + email);
    }

    @Override
    protected String nomeCriacao(CriarUsuarioUniversitarioRequestDTO dto) {
        return dto.nome();
    }

    @Override
    protected String emailCriacao(CriarUsuarioUniversitarioRequestDTO dto) {
        return dto.email();
    }

    @Override
    protected String telefoneCriacao(CriarUsuarioUniversitarioRequestDTO dto) {
        return dto.telefone();
    }

    @Override
    protected String nomeAtualizacao(AtualizarUsuarioUniversitarioRequestDTO dto) {
        return dto.nome();
    }

    @Override
    protected String emailAtualizacao(AtualizarUsuarioUniversitarioRequestDTO dto) {
        return dto.email();
    }

    @Override
    protected String telefoneAtualizacao(AtualizarUsuarioUniversitarioRequestDTO dto) {
        return dto.telefone();
    }

    @Override
    protected void validarCriacaoEspecifica(CriarUsuarioUniversitarioRequestDTO dto) {
        validarDuplicidadeEmailInstitucional(dto.emailInstitucional());
    }

    @Override
    protected void validarAtualizacaoEspecifica(UsuarioUniversitario usuario, AtualizarUsuarioUniversitarioRequestDTO dto) {
        if (!usuario.getEmailInstitucional().equals(dto.emailInstitucional())
                && repository.existsByEmailInstitucional(dto.emailInstitucional())) {
            throw new EmailInstitucionalJaCadastradoException(
                    "Ja existe usuario com o email institucional: " + dto.emailInstitucional()
            );
        }
    }

    @Override
    protected void aplicarDadosEspecificosCriacao(UsuarioUniversitario usuario, CriarUsuarioUniversitarioRequestDTO dto) {
        usuario.setEmailInstitucional(dto.emailInstitucional());
        usuario.setCurso(dto.curso());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGenero(dto.genero());
    }

    @Override
    protected void aplicarDadosEspecificosAtualizacao(UsuarioUniversitario usuario, AtualizarUsuarioUniversitarioRequestDTO dto) {
        usuario.setEmailInstitucional(dto.emailInstitucional());
        usuario.setCurso(dto.curso());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGenero(dto.genero());
    }

    private void validarDuplicidadeEmailInstitucional(String emailInstitucional) {
        if (repository.existsByEmailInstitucional(emailInstitucional)) {
            throw new EmailInstitucionalJaCadastradoException(
                    "Ja existe usuario com o email institucional: " + emailInstitucional
            );
        }
    }
}
