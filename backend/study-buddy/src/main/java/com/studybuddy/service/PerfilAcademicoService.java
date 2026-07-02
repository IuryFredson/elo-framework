package com.studybuddy.service;

import com.studybuddy.dto.request.AtualizarPerfilAcademicoRequestDTO;
import com.studybuddy.dto.response.PerfilAcademicoResponseDTO;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.mapper.PerfilAcademicoMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.PerfilAcademico;
import com.studybuddy.repository.EstudanteRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.UUID;

@Service
public class PerfilAcademicoService extends com.elo.perfil.PerfilService<Estudante, PerfilAcademico, AtualizarPerfilAcademicoRequestDTO, PerfilAcademicoResponseDTO> {

    private final EstudanteRepository estudanteRepository;
    private final PerfilAcademicoMapper perfilAcademicoMapper;

    public PerfilAcademicoService(EstudanteRepository estudanteRepository,
                                  PerfilAcademicoMapper perfilAcademicoMapper) {
        this.estudanteRepository = estudanteRepository;
        this.perfilAcademicoMapper = perfilAcademicoMapper;
    }

    @Override
    protected EstudanteRepository repositorioDonoPerfil() {
        return estudanteRepository;
    }

    @Override
    protected PerfilAcademicoMapper mapperResposta() {
        return perfilAcademicoMapper;
    }

    @Override
    protected RuntimeException erroDonoPerfilNaoEncontrado(UUID usuarioId) {
        return new EstudanteNaoEncontradoException("Estudante nao encontrado com id: " + usuarioId);
    }

    @Override
    protected PerfilAcademico obterPerfil(Estudante estudante) {
        return estudante.getPerfilAcademico();
    }

    @Override
    protected PerfilAcademico criarPerfil(AtualizarPerfilAcademicoRequestDTO dto) {
        return new PerfilAcademico();
    }

    @Override
    protected void associarPerfil(Estudante estudante, PerfilAcademico perfil) {
        estudante.setPerfilAcademico(perfil);
    }

    @Override
    protected void validarAtualizacao(Estudante estudante, AtualizarPerfilAcademicoRequestDTO dto) {
    }

    @Override
    protected void aplicarDadosDonoPerfil(Estudante estudante, AtualizarPerfilAcademicoRequestDTO dto) {
    }

    @Override
    protected void aplicarDadosPerfil(PerfilAcademico perfil, AtualizarPerfilAcademicoRequestDTO dto) {
        perfil.setCurso(dto.curso());
        perfil.setDisciplinasInteresse(new LinkedHashSet<>(dto.disciplinasInteresse()));
        perfil.setDisponibilidade(new LinkedHashSet<>(dto.disponibilidade()));
        perfil.setObjetivoEstudo(dto.objetivoEstudo());
        perfil.setNivelConhecimento(dto.nivelConhecimento());
        perfil.setModalidadePreferida(dto.modalidadePreferida());
        perfil.setDescricao(dto.descricao());
    }
}
