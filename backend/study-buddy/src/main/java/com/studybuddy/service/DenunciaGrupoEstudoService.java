package com.studybuddy.service;

import com.elo.denuncia.CriterioDenuncia;
import com.elo.denuncia.StatusDenuncia;
import com.elo.persistencia.RepositorioBase;
import com.studybuddy.dto.request.CriarDenunciaGrupoEstudoRequestDTO;
import com.studybuddy.dto.response.DenunciaGrupoEstudoResponseDTO;
import com.studybuddy.exception.DenunciaGrupoEstudoNaoEncontradaException;
import com.studybuddy.exception.EstudanteNaoEncontradoException;
import com.studybuddy.exception.GrupoEstudoNaoEncontradoException;
import com.studybuddy.exception.TransicaoInvalidaDenunciaException;
import com.studybuddy.mapper.DenunciaGrupoEstudoMapper;
import com.studybuddy.model.entity.DenunciaGrupoEstudo;
import com.studybuddy.model.entity.Estudante;
import com.studybuddy.model.entity.GrupoEstudo;
import com.studybuddy.model.enums.CriterioDenunciaStudyBuddy;
import com.studybuddy.repository.DenunciaGrupoEstudoRepository;
import com.studybuddy.repository.EstudanteRepository;
import com.studybuddy.repository.GrupoEstudoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DenunciaGrupoEstudoService extends com.elo.denuncia.DenunciaService<
        DenunciaGrupoEstudo,
        GrupoEstudo,
        Estudante,
        CriarDenunciaGrupoEstudoRequestDTO,
        DenunciaGrupoEstudoResponseDTO> {

    private final DenunciaGrupoEstudoRepository denunciaRepository;
    private final GrupoEstudoRepository grupoRepository;
    private final EstudanteRepository estudanteRepository;
    private final DenunciaGrupoEstudoMapper denunciaMapper;

    public DenunciaGrupoEstudoService(
            DenunciaGrupoEstudoRepository denunciaRepository,
            GrupoEstudoRepository grupoRepository,
            EstudanteRepository estudanteRepository,
            DenunciaGrupoEstudoMapper denunciaMapper) {
        this.denunciaRepository = denunciaRepository;
        this.grupoRepository = grupoRepository;
        this.estudanteRepository = estudanteRepository;
        this.denunciaMapper = denunciaMapper;
    }

    public List<DenunciaGrupoEstudoResponseDTO> buscarPorGrupoId(UUID grupoId) {
        return buscarPorOfertaId(grupoId);
    }

    @Override
    protected RepositorioBase<DenunciaGrupoEstudo, UUID> repositorio() {
        return denunciaRepository;
    }

    @Override
    protected UUID denuncianteId(CriarDenunciaGrupoEstudoRequestDTO dto) {
        return dto.denuncianteId();
    }

    @Override
    protected UUID ofertaId(CriarDenunciaGrupoEstudoRequestDTO dto) {
        return dto.grupoId();
    }

    @Override
    protected Estudante buscarDenunciante(UUID denuncianteId) {
        return estudanteRepository.findById(denuncianteId)
                .orElseThrow(() -> new EstudanteNaoEncontradoException(
                        "Estudante nao encontrado com id: " + denuncianteId));
    }

    @Override
    protected GrupoEstudo buscarOferta(UUID ofertaId) {
        return grupoRepository.findById(ofertaId)
                .orElseThrow(() -> new GrupoEstudoNaoEncontradoException(
                        "Grupo de estudo nao encontrado com id: " + ofertaId));
    }

    @Override
    protected DenunciaGrupoEstudo construirDenuncia(CriarDenunciaGrupoEstudoRequestDTO dto) {
        return new DenunciaGrupoEstudo();
    }

    @Override
    protected CriterioDenuncia criterio(CriarDenunciaGrupoEstudoRequestDTO dto) {
        return dto.criterioOuOutro();
    }

    @Override
    protected void aplicarCriacao(
            DenunciaGrupoEstudo denuncia,
            Estudante denunciante,
            GrupoEstudo oferta,
            CriarDenunciaGrupoEstudoRequestDTO dto,
            CriterioDenuncia criterio,
            StatusDenuncia status,
            LocalDateTime criadoEm) {
        denuncia.setDenunciante(denunciante);
        denuncia.setGrupoEstudo(oferta);
        denuncia.setTitulo(dto.titulo());
        denuncia.setCorpo(dto.corpo());
        denuncia.setCriterio((CriterioDenunciaStudyBuddy) criterio);
        denuncia.setStatusDenuncia(status);
        denuncia.setCriadoEm(criadoEm);
        denuncia.setStatusAtualizadoEm(criadoEm);
    }

    @Override
    protected void aplicarStatus(DenunciaGrupoEstudo denuncia, StatusDenuncia status, LocalDateTime atualizadoEm) {
        denuncia.setStatusDenuncia(status);
        denuncia.setStatusAtualizadoEm(atualizadoEm);
    }

    @Override
    protected List<DenunciaGrupoEstudo> buscarPorOferta(GrupoEstudo oferta) {
        return denunciaRepository.findByGrupoEstudo(oferta);
    }

    @Override
    protected List<DenunciaGrupoEstudo> buscarPorDenunciante(Estudante denunciante) {
        return denunciaRepository.findByDenunciante(denunciante);
    }

    @Override
    protected List<DenunciaGrupoEstudo> buscarPorStatusDenuncia(StatusDenuncia status) {
        return denunciaRepository.findByStatusDenuncia(status);
    }

    @Override
    protected DenunciaGrupoEstudoResponseDTO mapear(DenunciaGrupoEstudo denuncia) {
        return denunciaMapper.toResponseDTO(denuncia);
    }

    @Override
    protected RuntimeException erroDenunciaNaoEncontrada(UUID id) {
        return new DenunciaGrupoEstudoNaoEncontradaException(
                "Denuncia de grupo de estudo nao encontrada com id: " + id);
    }

    @Override
    protected RuntimeException erroTransicaoInvalida(StatusDenuncia atual, StatusDenuncia novo) {
        return new TransicaoInvalidaDenunciaException(
                "Transicao invalida de Status " + atual + " para " + novo + ".");
    }
}
