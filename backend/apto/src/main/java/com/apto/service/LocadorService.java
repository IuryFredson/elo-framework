package com.apto.service;

import com.apto.dto.request.AlterarStatusUsuarioRequestDTO;
import com.apto.dto.request.AtualizarLocadorRequestDTO;
import com.apto.dto.request.CriarLocadorRequestDTO;
import com.apto.dto.response.LocadorResponseDTO;
import com.apto.exception.DocumentoIdentificacaoJaCadastradoException;
import com.apto.exception.EmailJaCadastradoException;
import com.apto.exception.LocadorNaoEncontradoException;
import com.apto.mapper.LocadorMapper;
import com.apto.model.entity.Locador;
import com.apto.model.entity.PerfilAnunciante;
import com.apto.repository.LocadorRepository;
import com.apto.repository.PerfilAnuncianteRepository;
import com.apto.repository.UsuarioRepository;
import com.elo.usuario.UsuarioService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LocadorService extends UsuarioService<Locador, CriarLocadorRequestDTO, AtualizarLocadorRequestDTO, LocadorResponseDTO> {

    private final LocadorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilAnuncianteRepository perfilAnuncianteRepository;
    private final LocadorMapper locadorMapper;

    public LocadorService(LocadorRepository repository,
                          UsuarioRepository usuarioRepository,
                          PerfilAnuncianteRepository perfilAnuncianteRepository,
                          LocadorMapper locadorMapper) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.perfilAnuncianteRepository = perfilAnuncianteRepository;
        this.locadorMapper = locadorMapper;
    }

    public LocadorResponseDTO alterarStatus(UUID id, AlterarStatusUsuarioRequestDTO dto) {
        return alterarStatus(id, dto.ativo());
    }

    @Override
    protected Locador construirEntidade(CriarLocadorRequestDTO dto) {
        return new Locador();
    }

    @Override
    protected LocadorRepository repositorio() {
        return repository;
    }

    @Override
    protected LocadorMapper mapperResposta() {
        return locadorMapper;
    }

    @Override
    protected RuntimeException erroUsuarioNaoEncontrado(UUID id) {
        return new LocadorNaoEncontradoException("Locador nao encontrado com id: " + id);
    }

    @Override
    protected boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    protected RuntimeException erroEmailDuplicado(String email) {
        return new EmailJaCadastradoException("Ja existe locador com o email: " + email);
    }

    @Override
    protected String nomeCriacao(CriarLocadorRequestDTO dto) {
        return dto.nome();
    }

    @Override
    protected String emailCriacao(CriarLocadorRequestDTO dto) {
        return dto.email();
    }

    @Override
    protected String telefoneCriacao(CriarLocadorRequestDTO dto) {
        return dto.telefone();
    }

    @Override
    protected String nomeAtualizacao(AtualizarLocadorRequestDTO dto) {
        return dto.nome();
    }

    @Override
    protected String emailAtualizacao(AtualizarLocadorRequestDTO dto) {
        return dto.email();
    }

    @Override
    protected String telefoneAtualizacao(AtualizarLocadorRequestDTO dto) {
        return dto.telefone();
    }

    @Override
    protected void validarCriacaoEspecifica(CriarLocadorRequestDTO dto) {
        validarDuplicidadeDocumento(dto.documentoIdentificacao());
    }

    @Override
    protected void validarAtualizacaoEspecifica(Locador locador, AtualizarLocadorRequestDTO dto) {
        if (!locador.getDocumentoIdentificacao().equals(dto.documentoIdentificacao())
                && repository.existsByDocumentoIdentificacao(dto.documentoIdentificacao())) {
            throw new DocumentoIdentificacaoJaCadastradoException(
                    "Ja existe locador com o documento: " + dto.documentoIdentificacao());
        }
    }

    @Override
    protected void aplicarDadosEspecificosCriacao(Locador locador, CriarLocadorRequestDTO dto) {
        locador.setDocumentoIdentificacao(dto.documentoIdentificacao());
        locador.setNomeExibicaoOuRazao(dto.nomeExibicaoOuRazao());
    }

    @Override
    protected void aplicarDadosEspecificosAtualizacao(Locador locador, AtualizarLocadorRequestDTO dto) {
        locador.setDocumentoIdentificacao(dto.documentoIdentificacao());
        locador.setNomeExibicaoOuRazao(dto.nomeExibicaoOuRazao());
    }

    @Override
    protected void aposCriar(Locador locador, CriarLocadorRequestDTO dto) {
        PerfilAnunciante perfil = new PerfilAnunciante();
        perfil.setUsuario(locador);
        perfil.setAtivo(true);
        perfilAnuncianteRepository.save(perfil);
    }

    private void validarDuplicidadeDocumento(String documento) {
        if (repository.existsByDocumentoIdentificacao(documento)) {
            throw new DocumentoIdentificacaoJaCadastradoException(
                    "Ja existe locador com o documento: " + documento);
        }
    }
}
