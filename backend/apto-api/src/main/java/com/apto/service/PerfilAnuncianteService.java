package com.apto.service;

import com.apto.dto.response.PerfilAnuncianteResponseDTO;
import com.apto.exception.AnuncianteNaoEncontradoException;
import com.apto.exception.UsuarioNaoEncontradoException;
import com.apto.mapper.PerfilAnuncianteMapper;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.model.entity.UsuarioUniversitario;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.UsuarioUniversitarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PerfilAnuncianteService {

    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final UsuarioUniversitarioRepository universitarioRepository;
    private final PerfilAnuncianteMapper perfilAnuncianteMapper;

    public PerfilAnuncianteService(PerfilAnuncianteRepository perfilAnuncianteRepository,
                                   UsuarioUniversitarioRepository universitarioRepository,
                                   PerfilAnuncianteMapper perfilAnuncianteMapper) {
        this.perfilAnuncianteRepository = perfilAnuncianteRepository;
        this.universitarioRepository = universitarioRepository;
        this.perfilAnuncianteMapper = perfilAnuncianteMapper;
    }

    @Transactional
    public PerfilAnuncianteResponseDTO habilitarAnunciante(UUID universitarioId) {
        UsuarioUniversitario universitario = universitarioRepository.findById(universitarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(
                        "Usuário universitário não encontrado com id: " + universitarioId));

        PerfilAnunciante perfil = perfilAnuncianteRepository
                .findByUsuario_Id(universitarioId)
                .orElseGet(() -> {
                    PerfilAnunciante novo = new PerfilAnunciante();
                    novo.setUsuario(universitario);
                    return novo;
        });

        perfil.setAtivo(true);
        return perfilAnuncianteMapper.toResponseDTO(perfilAnuncianteRepository.save(perfil));
    }

    @Transactional
    public PerfilAnuncianteResponseDTO desabilitarAnunciante(UUID universitarioId) {
        PerfilAnunciante perfil = perfilAnuncianteRepository
                .findByUsuario_Id(universitarioId)
                .orElseThrow(() -> new AnuncianteNaoEncontradoException(
                        "Este universitário não possui perfil de anunciante: " + universitarioId));

        perfil.setAtivo(false);
        return perfilAnuncianteMapper.toResponseDTO(perfilAnuncianteRepository.save(perfil));
    }

    public PerfilAnuncianteResponseDTO buscarPorUsuario(UUID usuarioId) {
        PerfilAnunciante perfil = perfilAnuncianteRepository
                .findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new AnuncianteNaoEncontradoException("Anunciante não encontrado com id: " + usuarioId));

        return perfilAnuncianteMapper.toResponseDTO(perfil);
    }
}
