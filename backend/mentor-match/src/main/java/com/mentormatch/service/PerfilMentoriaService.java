package com.mentormatch.service;

import com.elo.perfil.PerfilService;
import com.mentormatch.dto.request.AtualizarPerfilMentoriaRequestDTO;
import com.mentormatch.dto.response.PerfilMentoriaResponseDTO;
import com.mentormatch.exception.MentorMatchException;
import com.mentormatch.mapper.PerfilMentoriaMapper;
import com.mentormatch.model.entity.ParticipanteMentoria;
import com.mentormatch.model.entity.PerfilMentoria;
import com.mentormatch.repository.ParticipanteMentoriaRepository;
import org.springframework.stereotype.Service;
import java.util.LinkedHashSet;
import java.util.UUID;

@Service
public class PerfilMentoriaService extends PerfilService<ParticipanteMentoria, PerfilMentoria, AtualizarPerfilMentoriaRequestDTO, PerfilMentoriaResponseDTO> {
    private final ParticipanteMentoriaRepository repository; private final PerfilMentoriaMapper mapper;
    public PerfilMentoriaService(ParticipanteMentoriaRepository repository, PerfilMentoriaMapper mapper) { this.repository=repository; this.mapper=mapper; }
    @Override protected ParticipanteMentoriaRepository repositorioDonoPerfil() { return repository; }
    @Override protected PerfilMentoriaMapper mapperResposta() { return mapper; }
    @Override protected RuntimeException erroDonoPerfilNaoEncontrado(UUID id) { return new MentorMatchException("Participante não encontrado: " + id); }
    @Override protected PerfilMentoria obterPerfil(ParticipanteMentoria u) { return u.getPerfilMentoria(); }
    @Override protected PerfilMentoria criarPerfil(AtualizarPerfilMentoriaRequestDTO dto) { return new PerfilMentoria(); }
    @Override protected void associarPerfil(ParticipanteMentoria u, PerfilMentoria p) { u.setPerfilMentoria(p); }
    @Override protected void validarAtualizacao(ParticipanteMentoria u, AtualizarPerfilMentoriaRequestDTO dto) { }
    @Override protected void aplicarDadosDonoPerfil(ParticipanteMentoria u, AtualizarPerfilMentoriaRequestDTO dto) { }
    @Override protected void aplicarDadosPerfil(PerfilMentoria p, AtualizarPerfilMentoriaRequestDTO d) {
        p.setAreas(d.areas() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(d.areas()));
        p.setObjetivos(d.objetivos() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(d.objetivos()));
        p.setNivelConhecimento(d.nivelConhecimento());
        p.setModalidades(d.modalidades() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(d.modalidades()));
        p.setDisponibilidades(d.disponibilidades() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(d.disponibilidades()));
        p.setIdiomas(d.idiomas() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(d.idiomas()));
        p.setDescricao(d.descricao());
    }
}
