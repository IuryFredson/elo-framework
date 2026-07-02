package com.studybuddy.service;

import com.elo.oferta.OfertaService;
import com.studybuddy.dto.request.AtualizarGrupoEstudoRequestDTO;
import com.studybuddy.dto.request.CriarGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.GrupoEstudoResponseDTO;
import com.studybuddy.exception.AcessoNegadoException;
import com.studybuddy.exception.EstudanteInativoException;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.GrupoEstudoNaoEncontradoException;
import com.studybuddy.mapper.GrupoEstudoMapper;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.StatusGrupoEstudo;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.GrupoEstudoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class GrupoEstudoService extends OfertaService<
        GrupoEstudo,
        CriarGrupoEstudoRequestDTO,
        AtualizarGrupoEstudoRequestDTO,
        GrupoEstudoResponseDTO,
        StatusGrupoEstudo> {

    private final GrupoEstudoRepository grupoRepository;
    private final EstudanteRepository estudanteRepository;
    private final GrupoEstudoMapper grupoMapper;

    public GrupoEstudoService(GrupoEstudoRepository grupoRepository,
                              EstudanteRepository estudanteRepository,
                              GrupoEstudoMapper grupoMapper) {
        this.grupoRepository = grupoRepository;
        this.estudanteRepository = estudanteRepository;
        this.grupoMapper = grupoMapper;
    }

    @Override
    protected GrupoEstudoRepository repositorio() {
        return grupoRepository;
    }

    @Override
    protected GrupoEstudoMapper mapperResposta() {
        return grupoMapper;
    }

    @Override
    protected RuntimeException erroOfertaNaoEncontrada(UUID id) {
        return new GrupoEstudoNaoEncontradoException("Grupo de estudo nao encontrado com id: " + id);
    }

    @Override
    protected GrupoEstudo construirOferta(CriarGrupoEstudoRequestDTO dto) {
        Estudante publicador = estudanteRepository.findById(dto.publicadorId())
                .orElseThrow(() -> new EstudanteNaoEncontradoException(
                        "Estudante nao encontrado com id: " + dto.publicadorId()));

        if (!publicador.isAtivo()) {
            throw new EstudanteInativoException(
                    "Estudante inativo nao pode publicar grupo de estudo: " + dto.publicadorId());
        }

        GrupoEstudo grupo = new GrupoEstudo();
        grupo.setPublicador(publicador);
        grupo.setTitulo(dto.titulo());
        grupo.setDescricao(dto.descricao());
        grupo.setDisciplina(dto.disciplina());
        grupo.setQuantidadeVagas(dto.quantidadeVagas());
        grupo.setModalidade(dto.modalidade());
        grupo.setPeriodo(dto.periodo());
        grupo.setStatus(StatusGrupoEstudo.ATIVO);
        grupo.setDataPublicacao(LocalDate.now());
        return grupo;
    }

    @Override
    protected void validarAtualizacao(
            GrupoEstudo grupo,
            UUID publicadorId,
            AtualizarGrupoEstudoRequestDTO dto) {
        if (!grupo.getPublicadorId().equals(publicadorId)) {
            throw new AcessoNegadoException(
                    "Estudante nao tem permissao para editar este grupo de estudo.");
        }
    }

    @Override
    protected void aplicarAtualizacao(GrupoEstudo grupo, AtualizarGrupoEstudoRequestDTO dto) {
        grupo.setTitulo(dto.titulo());
        grupo.setDescricao(dto.descricao());
        grupo.setDisciplina(dto.disciplina());
        grupo.setQuantidadeVagas(dto.quantidadeVagas());
        grupo.setModalidade(dto.modalidade());
        grupo.setPeriodo(dto.periodo());
    }

    @Override
    protected StatusGrupoEstudo obterStatus(GrupoEstudo grupo) {
        return grupo.getStatus();
    }

    @Override
    protected void aplicarStatus(GrupoEstudo grupo, StatusGrupoEstudo status) {
        grupo.setStatus(status);
    }

    @Override
    protected boolean deveExcluirFisicamente(GrupoEstudo grupo) {
        return true;
    }

    @Override
    protected void aplicarExclusaoLogica(GrupoEstudo grupo) {
        grupo.setStatus(StatusGrupoEstudo.ENCERRADO);
    }
}
