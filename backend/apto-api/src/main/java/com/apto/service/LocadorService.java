package com.apto.service;

import com.apto.dto.request.AlterarStatusUsuarioRequestDTO;
import com.apto.dto.request.AtualizarLocadorRequestDTO;
import com.apto.dto.request.CriarLocadorRequestDTO;
import com.apto.dto.response.LocadorResponseDTO;
import com.apto.exception.DocumentoIdentificacaoJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.exception.LocadorNaoEncontradoException;
import com.apto.model.entity.Locador;
import com.apto.repository.LocadorRepository;
import com.apto.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LocadorService {

    private final LocadorRepository repository;
    private final UsuarioRepository usuarioRepository;

    public LocadorService(LocadorRepository repository,
                          UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public LocadorResponseDTO criar(CriarLocadorRequestDTO dto) {
        validarDuplicidadeEmail(dto.email());
        validarDuplicidadeDocumento(dto.documentoIdentificacao());

        Locador locador = new Locador();
        locador.setNome(dto.nome());
        locador.setEmail(dto.email());
        locador.setTelefone(dto.telefone());
        locador.setAtivo(true);
        locador.setDocumentoIdentificacao(dto.documentoIdentificacao());
        locador.setNomeExibicaoOuRazao(dto.nomeExibicaoOuRazao());

        Locador salvo = repository.save(locador);
        return toResponseDTO(salvo);
    }

    public List<LocadorResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public LocadorResponseDTO buscarPorId(UUID id) {
        Locador locador = buscarEntidadePorId(id);
        return toResponseDTO(locador);
    }

    public LocadorResponseDTO atualizar(UUID id, AtualizarLocadorRequestDTO dto) {
        Locador locador = buscarEntidadePorId(id);

        if (!locador.getEmail().equals(dto.email()) && usuarioRepository.existsByEmail(dto.email())) {
            throw new EmailJaCadastradoException("Já existe locador com o email: " + dto.email());
        }

        if (!locador.getDocumentoIdentificacao().equals(dto.documentoIdentificacao())
                && repository.existsByDocumentoIdentificacao(dto.documentoIdentificacao())) {
            throw new DocumentoIdentificacaoJaCadastradoException(
                    "Já existe locador com o documento: " + dto.documentoIdentificacao()
            );
        }

        locador.setNome(dto.nome());
        locador.setEmail(dto.email());
        locador.setTelefone(dto.telefone());
        locador.setDocumentoIdentificacao(dto.documentoIdentificacao());
        locador.setNomeExibicaoOuRazao(dto.nomeExibicaoOuRazao());

        Locador atualizado = repository.save(locador);
        return toResponseDTO(atualizado);
    }

    public LocadorResponseDTO alterarStatus(UUID id, AlterarStatusUsuarioRequestDTO dto) {
        Locador locador = buscarEntidadePorId(id);
        locador.setAtivo(dto.ativo());

        Locador atualizado = repository.save(locador);
        return toResponseDTO(atualizado);
    }

    public void deletar(UUID id) {
        Locador locador = buscarEntidadePorId(id);
        repository.delete(locador);
    }

    private Locador buscarEntidadePorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new LocadorNaoEncontradoException("Locador não encontrado com id: " + id)
                );
    }

    private void validarDuplicidadeEmail(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException("Já existe locador com o email: " + email);
        }
    }

    private void validarDuplicidadeDocumento(String documento) {
        if (repository.existsByDocumentoIdentificacao(documento)) {
            throw new DocumentoIdentificacaoJaCadastradoException(
                    "Já existe locador com o documento: " + documento
            );
        }
    }

    private LocadorResponseDTO toResponseDTO(Locador locador) {
        return new LocadorResponseDTO(
                locador.getId(),
                locador.getNome(),
                locador.getEmail(),
                locador.getTelefone(),
                locador.isAtivo(),
                locador.getDocumentoIdentificacao(),
                locador.getNomeExibicaoOuRazao()
        );
    }
}
