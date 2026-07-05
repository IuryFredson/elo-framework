package com.mentormatch.service;

import com.elo.usuario.UsuarioService;
import com.mentormatch.dto.request.*;
import com.mentormatch.dto.response.ParticipanteResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.MentorMapper;
import com.mentormatch.model.entity.Mentor;
import com.mentormatch.repository.*;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class MentorService extends UsuarioService<Mentor, CriarParticipanteRequestDTO, AtualizarParticipanteRequestDTO, ParticipanteResponseDTO> {
    private final MentorRepository repository; private final UsuarioRepository usuarios; private final MentorMapper mapper;
    public MentorService(MentorRepository repository, UsuarioRepository usuarios, MentorMapper mapper) { this.repository=repository; this.usuarios=usuarios; this.mapper=mapper; }
    @Override protected Mentor construirEntidade(CriarParticipanteRequestDTO dto) { return new Mentor(); }
    @Override protected MentorRepository repositorio() { return repository; }
    @Override protected MentorMapper mapperResposta() { return mapper; }
    @Override protected RuntimeException erroUsuarioNaoEncontrado(UUID id) { return new MentorMatchException("Mentor não encontrado: " + id); }
    @Override protected boolean existeEmail(String email) { return usuarios.existsByEmail(email); }
    @Override protected RuntimeException erroEmailDuplicado(String email) { return new MentorMatchException("E-mail já cadastrado: " + email); }
    @Override protected String nomeCriacao(CriarParticipanteRequestDTO d) { return d.nome(); }
    @Override protected String emailCriacao(CriarParticipanteRequestDTO d) { return d.email(); }
    @Override protected String telefoneCriacao(CriarParticipanteRequestDTO d) { return d.telefone(); }
    @Override protected String nomeAtualizacao(AtualizarParticipanteRequestDTO d) { return d.nome(); }
    @Override protected String emailAtualizacao(AtualizarParticipanteRequestDTO d) { return d.email(); }
    @Override protected String telefoneAtualizacao(AtualizarParticipanteRequestDTO d) { return d.telefone(); }
}
