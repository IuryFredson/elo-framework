package com.mentormatch.service;

import com.elo.usuario.UsuarioService;
import com.mentormatch.dto.request.*;
import com.mentormatch.dto.response.ParticipanteResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.AlunoMapper;
import com.mentormatch.model.entity.Aluno;
import com.mentormatch.repository.*;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class AlunoService extends UsuarioService<Aluno, CriarParticipanteRequestDTO, AtualizarParticipanteRequestDTO, ParticipanteResponseDTO> {
    private final AlunoRepository repository; private final UsuarioRepository usuarios; private final AlunoMapper mapper;
    public AlunoService(AlunoRepository repository, UsuarioRepository usuarios, AlunoMapper mapper) { this.repository=repository; this.usuarios=usuarios; this.mapper=mapper; }
    @Override protected Aluno construirEntidade(CriarParticipanteRequestDTO dto) { return new Aluno(); }
    @Override protected AlunoRepository repositorio() { return repository; }
    @Override protected AlunoMapper mapperResposta() { return mapper; }
    @Override protected RuntimeException erroUsuarioNaoEncontrado(UUID id) { return new MentorMatchException("Aluno não encontrado: " + id); }
    @Override protected boolean existeEmail(String email) { return usuarios.existsByEmail(email); }
    @Override protected RuntimeException erroEmailDuplicado(String email) { return new MentorMatchException("E-mail já cadastrado: " + email); }
    @Override protected String nomeCriacao(CriarParticipanteRequestDTO d) { return d.nome(); }
    @Override protected String emailCriacao(CriarParticipanteRequestDTO d) { return d.email(); }
    @Override protected String telefoneCriacao(CriarParticipanteRequestDTO d) { return d.telefone(); }
    @Override protected String nomeAtualizacao(AtualizarParticipanteRequestDTO d) { return d.nome(); }
    @Override protected String emailAtualizacao(AtualizarParticipanteRequestDTO d) { return d.email(); }
    @Override protected String telefoneAtualizacao(AtualizarParticipanteRequestDTO d) { return d.telefone(); }
}
