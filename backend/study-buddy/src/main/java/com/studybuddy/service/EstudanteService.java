package com.studybuddy.service;

import com.elo.usuario.UsuarioService;
import com.studybuddy.dto.request.AtualizarEstudanteRequestDTO;
import com.studybuddy.dto.request.CriarEstudanteRequestDTO;
import com.studybuddy.dto.response.EstudanteResponseDTO;
import com.studybuddy.exception.EmailJaCadastradoException;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.MatriculaJaCadastradaException;
import com.studybuddy.mapper.EstudanteMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EstudanteService extends UsuarioService<Estudante, CriarEstudanteRequestDTO, AtualizarEstudanteRequestDTO, EstudanteResponseDTO> {

    private final EstudanteRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final EstudanteMapper estudanteMapper;

    public EstudanteService(EstudanteRepository repository,
                            UsuarioRepository usuarioRepository,
                            EstudanteMapper estudanteMapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.estudanteMapper = estudanteMapper;
    }

    @Override
    protected Estudante construirEntidade(CriarEstudanteRequestDTO dto) {
        return new Estudante();
    }

    @Override
    protected EstudanteRepository repositorio() {
        return repository;
    }

    @Override
    protected EstudanteMapper mapperResposta() {
        return estudanteMapper;
    }

    @Override
    protected RuntimeException erroUsuarioNaoEncontrado(UUID id) {
        return new EstudanteNaoEncontradoException("Estudante nao encontrado com id: " + id);
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
    protected String nomeCriacao(CriarEstudanteRequestDTO dto) {
        return dto.nome();
    }

    @Override
    protected String emailCriacao(CriarEstudanteRequestDTO dto) {
        return dto.email();
    }

    @Override
    protected String telefoneCriacao(CriarEstudanteRequestDTO dto) {
        return dto.telefone();
    }

    @Override
    protected String nomeAtualizacao(AtualizarEstudanteRequestDTO dto) {
        return dto.nome();
    }

    @Override
    protected String emailAtualizacao(AtualizarEstudanteRequestDTO dto) {
        return dto.email();
    }

    @Override
    protected String telefoneAtualizacao(AtualizarEstudanteRequestDTO dto) {
        return dto.telefone();
    }

    @Override
    protected void validarCriacaoEspecifica(CriarEstudanteRequestDTO dto) {
        validarMatriculaDisponivel(dto.matricula());
    }

    @Override
    protected void validarAtualizacaoEspecifica(Estudante estudante, AtualizarEstudanteRequestDTO dto) {
        if (!estudante.getMatricula().equals(dto.matricula()) && repository.existsByMatricula(dto.matricula())) {
            throw erroMatriculaDuplicada(dto.matricula());
        }
    }

    @Override
    protected void aplicarDadosEspecificosCriacao(Estudante estudante, CriarEstudanteRequestDTO dto) {
        estudante.setMatricula(dto.matricula());
        estudante.setInstituicao(dto.instituicao());
    }

    @Override
    protected void aplicarDadosEspecificosAtualizacao(Estudante estudante, AtualizarEstudanteRequestDTO dto) {
        estudante.setMatricula(dto.matricula());
        estudante.setInstituicao(dto.instituicao());
    }

    private void validarMatriculaDisponivel(String matricula) {
        if (repository.existsByMatricula(matricula)) {
            throw erroMatriculaDuplicada(matricula);
        }
    }

    private RuntimeException erroMatriculaDuplicada(String matricula) {
        return new MatriculaJaCadastradaException("Ja existe estudante com a matricula: " + matricula);
    }
}
