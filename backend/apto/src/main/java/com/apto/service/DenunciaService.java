package com.apto.service;

import com.apto.dto.request.CriarDenunciaRequestDTO;
import com.apto.dto.response.DenunciaResponseDTO;
import com.apto.exception.AnuncioNaoEncontradoException;
import com.apto.exception.DenunciaNaoEncontradaException;
import com.apto.exception.TransicaoInvalidaStatusException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.DenunciaMapper;
import com.apto.model.entity.Anuncio;
import com.apto.model.entity.Denuncia;
import com.apto.model.enums.CriterioDenunciaApto;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.DenunciaRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import com.elo.denuncia.CriterioDenuncia;
import com.elo.denuncia.StatusDenuncia;
import com.elo.persistencia.RepositorioBase;
import com.elo.usuario.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DenunciaService extends com.elo.denuncia.DenunciaService<
        Denuncia,
        Anuncio,
        Usuario,
        CriarDenunciaRequestDTO,
        DenunciaResponseDTO> {

    private final DenunciaRepository denunciaRepository;
    private final UsuarioUniversitarioRepository universitarioRepository;
    private final LocadorRepository locadorRepository;
    private final AnuncioRepository anuncioRepository;
    private final DenunciaMapper denunciaMapper;

    public DenunciaService(DenunciaRepository denunciaRepository,
                           UsuarioUniversitarioRepository universitarioRepository,
                           LocadorRepository locadorRepository,
                           AnuncioRepository anuncioRepository,
                           DenunciaMapper denunciaMapper) {
        this.denunciaRepository = denunciaRepository;
        this.universitarioRepository = universitarioRepository;
        this.locadorRepository = locadorRepository;
        this.anuncioRepository = anuncioRepository;
        this.denunciaMapper = denunciaMapper;
    }

    public List<DenunciaResponseDTO> buscarPorAnuncioId(UUID anuncioId) {
        return buscarPorOfertaId(anuncioId);
    }

    @Override
    protected RepositorioBase<Denuncia, UUID> repositorio() {
        return denunciaRepository;
    }

    @Override
    protected UUID denuncianteId(CriarDenunciaRequestDTO dto) {
        return dto.denuncianteId();
    }

    @Override
    protected UUID ofertaId(CriarDenunciaRequestDTO dto) {
        return dto.anuncioId();
    }

    @Override
    protected Usuario buscarDenunciante(UUID denuncianteId) {
        return locadorRepository.findById(denuncianteId)
                .map(locador -> (Usuario) locador)
                .orElseGet(() -> universitarioRepository.findById(denuncianteId)
                        .orElseThrow(() -> new UsuarioNaoEncontradoException(
                                "Usuário não encontrado com id " + denuncianteId)));
    }

    @Override
    protected Anuncio buscarOferta(UUID ofertaId) {
        return anuncioRepository.findById(ofertaId)
                .orElseThrow(() -> new AnuncioNaoEncontradoException(
                        "Anuncio não encontrado com id " + ofertaId));
    }

    @Override
    protected Denuncia construirDenuncia(CriarDenunciaRequestDTO dto) {
        return new Denuncia();
    }

    @Override
    protected CriterioDenuncia criterio(CriarDenunciaRequestDTO dto) {
        return dto.criterioOuOutro();
    }

    @Override
    protected void aplicarCriacao(
            Denuncia denuncia,
            Usuario denunciante,
            Anuncio oferta,
            CriarDenunciaRequestDTO dto,
            CriterioDenuncia criterio,
            StatusDenuncia status,
            LocalDateTime criadoEm) {
        denuncia.setStatusDenuncia(status);
        denuncia.setStatusAtualizadoEm(criadoEm);
        denuncia.setAnuncio(oferta);
        denuncia.setDenunciante(denunciante);
        denuncia.setTitulo(dto.titulo());
        denuncia.setCorpo(dto.corpo());
        denuncia.setCriterio(criterio instanceof CriterioDenunciaApto apto ? apto : CriterioDenunciaApto.OUTRO);
        denuncia.setCriadoEm(criadoEm);
    }

    @Override
    protected void aplicarStatus(Denuncia denuncia, StatusDenuncia status, LocalDateTime atualizadoEm) {
        denuncia.setStatusDenuncia(status);
        denuncia.setStatusAtualizadoEm(atualizadoEm);
    }

    @Override
    protected List<Denuncia> buscarPorOferta(Anuncio oferta) {
        return denunciaRepository.findByAnuncio(oferta);
    }

    @Override
    protected List<Denuncia> buscarPorDenunciante(Usuario denunciante) {
        return denunciaRepository.findByDenunciante(denunciante);
    }

    @Override
    protected List<Denuncia> buscarPorStatusDenuncia(StatusDenuncia status) {
        return denunciaRepository.findByStatusDenuncia(status);
    }

    @Override
    protected DenunciaResponseDTO mapear(Denuncia denuncia) {
        return denunciaMapper.toResponseDTO(denuncia);
    }

    @Override
    protected RuntimeException erroDenunciaNaoEncontrada(UUID id) {
        return new DenunciaNaoEncontradaException("Denuncia não encontrada com id " + id);
    }

    @Override
    protected RuntimeException erroTransicaoInvalida(StatusDenuncia atual, StatusDenuncia novo) {
        return new TransicaoInvalidaStatusException(
                "Transição inválida de Status " + atual + " para " + novo + ".");
    }
}
