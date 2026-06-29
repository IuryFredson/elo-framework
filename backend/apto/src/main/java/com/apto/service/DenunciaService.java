package com.apto.service;

import com.apto.dto.request.CriarDenunciaRequestDTO;
import com.apto.dto.response.DenunciaResponseDTO;
import com.apto.exception.*;
import com.apto.mapper.DenunciaMapper;
import com.apto.model.entity.*;
import com.elo.denuncia.StatusDenuncia;
import com.apto.repository.AnuncioRepository;
import com.apto.repository.DenunciaRepository;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DenunciaService {
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

    public List<DenunciaResponseDTO> listarTodas(){
        return denunciaRepository.findAll()
                .stream()
                .map(denunciaMapper::toResponseDTO)
                .toList();
    }

    public DenunciaResponseDTO criar(CriarDenunciaRequestDTO dto){
        Usuario denunciante = buscarUsuarioPorId(dto.denuncianteId());

        Anuncio anuncio = anuncioRepository.findById(dto.anuncioId())
                .orElseThrow(() -> new AnuncioNaoEncontradoException("Anuncio não encontrado com id " + dto.anuncioId()));

        LocalDateTime atual = LocalDateTime.now();
        Denuncia denuncia = new Denuncia();
        denuncia.setStatusDenuncia(StatusDenuncia.PENDENTE);
        denuncia.setStatusAtualizadoEm(atual);
        denuncia.setAnuncio(anuncio);
        denuncia.setDenunciante(denunciante);
        denuncia.setTitulo(dto.titulo());
        denuncia.setCorpo(dto.corpo());
        denuncia.setCriadoEm(atual);
        return denunciaMapper.toResponseDTO(denunciaRepository.save(denuncia));
    }

    public DenunciaResponseDTO atualizarStatus(UUID id, StatusDenuncia novoStatus){
        Denuncia denuncia = buscarEntidadePorId(id);
        StatusDenuncia statusAtual = denuncia.getStatusDenuncia();
        if(!transicaoValida(statusAtual, novoStatus)){
            throw new TransicaoInvalidaStatusException("Transição inválida de Status " + statusAtual + " para " + novoStatus + ".");
        }
        denuncia.setStatusDenuncia(novoStatus);
        denuncia.setStatusAtualizadoEm(LocalDateTime.now());
        return denunciaMapper.toResponseDTO(denunciaRepository.save(denuncia));
    }

    public void deletar(UUID id){
        Denuncia denuncia = buscarEntidadePorId(id);
        denunciaRepository.delete(denuncia);
    }

    public Denuncia buscarEntidadePorId(UUID id){
        return denunciaRepository.findById(id)
                .orElseThrow( () -> new DenunciaNaoEncontradaException("Denuncia não encontrada com id " + id));
    }

    public DenunciaResponseDTO buscarPorId(UUID id){
        Denuncia denuncia = buscarEntidadePorId(id);
        return denunciaMapper.toResponseDTO(denuncia);
    }

    public List<DenunciaResponseDTO> buscarPorAnuncioId(UUID anuncioId){
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
                .orElseThrow(() -> new AnuncioNaoEncontradoException("Anuncio não encontrado com id " + anuncioId));

        return denunciaRepository.findByAnuncio(anuncio)
                .stream()
                .map(denunciaMapper::toResponseDTO)
                .toList();
    }

    public List<DenunciaResponseDTO> buscarPorUsuarioId(UUID usuarioId){
        Usuario usuario = buscarUsuarioPorId(usuarioId);
        return denunciaRepository.findByDenunciante(usuario)
                .stream()
                .map(denunciaMapper::toResponseDTO)
                .toList();
    }

    public List<DenunciaResponseDTO> buscarPorStatus(StatusDenuncia status){
        List<Denuncia> denuncias = denunciaRepository.findByStatusDenuncia(status);
        return denuncias.stream().map(denunciaMapper::toResponseDTO).toList();
    }

    //valida se a ida do status atual para outro novo é válida
    private boolean transicaoValida(StatusDenuncia atual, StatusDenuncia novo) {
        switch (atual) {
            case PENDENTE:
                return novo == StatusDenuncia.EM_ANALISE;
            case EM_ANALISE:
                return novo == StatusDenuncia.PROCEDENTE || novo == StatusDenuncia.IMPROCEDENTE;
            case PROCEDENTE:
            case IMPROCEDENTE:
                return novo == StatusDenuncia.ARQUIVADA;
            case ARQUIVADA:
                return false;
            default:
                return false;
        }
    }

    private Usuario buscarUsuarioPorId(UUID id) {
        return locadorRepository.findById(id)
                .map(locador -> (Usuario) locador)
                .orElseGet(() -> universitarioRepository.findById(id)
                        .orElseThrow(() -> new UsuarioNaoEncontradoException(
                                "Usuário não encontrado com id " + id)));
    }
}
