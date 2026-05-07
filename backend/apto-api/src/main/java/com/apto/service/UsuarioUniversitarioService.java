package com.apto.service;

import com.apto.dto.request.AlterarStatusUsuarioRequestDTO;
import com.apto.dto.request.AtualizarUsuarioUniversitarioRequestDTO;
import com.apto.dto.request.CriarUsuarioUniversitarioRequestDTO;
import com.apto.dto.response.UsuarioUniversitarioResponseDTO;
import com.apto.exception.EmailInstitucionalJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.UsuarioRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioUniversitarioService {

    private final UsuarioUniversitarioRepository repository;
    private final UsuarioRepository usuarioRepository;

    public UsuarioUniversitarioService(UsuarioUniversitarioRepository repository,
                                       UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioUniversitarioResponseDTO criar(CriarUsuarioUniversitarioRequestDTO dto) {
        validarDuplicidadeEmail(dto.email());
        validarDuplicidadeEmailInstitucional(dto.emailInstitucional());

        UsuarioUniversitario usuario = new UsuarioUniversitario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setTelefone(dto.telefone());
        usuario.setAtivo(true);
        usuario.setEmailInstitucional(dto.emailInstitucional());
        usuario.setCurso(dto.curso());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGenero(dto.genero());

        UsuarioUniversitario salvo = repository.save(usuario);
        return toResponseDTO(salvo);
    }

    public List<UsuarioUniversitarioResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UsuarioUniversitarioResponseDTO buscarPorId(UUID id) {
        UsuarioUniversitario usuario = buscarEntidadePorId(id);
        return toResponseDTO(usuario);
    }

    public UsuarioUniversitarioResponseDTO atualizar(UUID id, AtualizarUsuarioUniversitarioRequestDTO dto) {
        UsuarioUniversitario usuario = buscarEntidadePorId(id);

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
        usuario.setTelefone(dto.telefone());
        usuario.setEmailInstitucional(dto.emailInstitucional());
        usuario.setCurso(dto.curso());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGenero(dto.genero());

        UsuarioUniversitario atualizado = repository.save(usuario);
        return toResponseDTO(atualizado);
    }

    public UsuarioUniversitarioResponseDTO alterarStatus(UUID id, AlterarStatusUsuarioRequestDTO dto) {
        UsuarioUniversitario usuario = buscarEntidadePorId(id);
        usuario.setAtivo(dto.ativo());

        UsuarioUniversitario atualizado = repository.save(usuario);
        return toResponseDTO(atualizado);
    }

    public void deletar(UUID id) {
        UsuarioUniversitario usuario = buscarEntidadePorId(id);
        repository.delete(usuario);
    }

    private UsuarioUniversitario buscarEntidadePorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuário universitário não encontrado com id: " + id)
                );
    }

    private void validarDuplicidadeEmail(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException("Já existe usuário com o email: " + email);
        }
    }

    private void validarDuplicidadeEmailInstitucional(String emailInstitucional) {
        if (repository.existsByEmailInstitucional(emailInstitucional)) {
            throw new EmailInstitucionalJaCadastradoException(
                    "Já existe usuário com o email institucional: " + emailInstitucional
            );
        }
    }

    private UsuarioUniversitarioResponseDTO toResponseDTO(UsuarioUniversitario usuario) {
        return new UsuarioUniversitarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.isAtivo(),
                usuario.getEmailInstitucional(),
                usuario.getCurso(),
                usuario.getDataNascimento(),
                usuario.getGenero()
        );
    }
}
