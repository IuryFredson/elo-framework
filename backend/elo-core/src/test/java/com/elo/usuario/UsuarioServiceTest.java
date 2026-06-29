package com.elo.usuario;

import com.elo.porta.MapperResposta;
import com.elo.persistencia.RepositorioBase;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @Test
    void deveExecutarFluxoDeCriacaoNaOrdemEsperada() {
        FakeUsuarioService service = new FakeUsuarioService();
        CriarUsuarioDto dto = new CriarUsuarioDto("Ana", "ana@email.com", "8599", "DOC-1");

        String resposta = service.criar(dto);

        assertEquals("Ana|ana@email.com|DOC-1", resposta);
        assertEquals(List.of(
                "existeEmail",
                "validarCriacaoEspecifica",
                "construirEntidade",
                "aplicarDadosEspecificosCriacao",
                "salvar",
                "aposCriar",
                "mapearResposta"), service.eventos);
        assertEquals("Ana", service.usuarioSalvo.getNome());
        assertEquals("ana@email.com", service.usuarioSalvo.getEmail());
        assertEquals("8599", service.usuarioSalvo.getTelefone());
        assertEquals("DOC-1", service.usuarioSalvo.documento);
        assertEquals(true, service.usuarioSalvo.isAtivo());
    }

    @Test
    void deveInterromperCriacaoQuandoEmailJaExiste() {
        FakeUsuarioService service = new FakeUsuarioService();
        service.emailExiste = true;
        CriarUsuarioDto dto = new CriarUsuarioDto("Ana", "ana@email.com", "8599", "DOC-1");

        assertThrows(IllegalStateException.class, () -> service.criar(dto));

        assertEquals(List.of("existeEmail", "erroEmailDuplicado"), service.eventos);
    }

    @Test
    void deveExecutarFluxoDeAtualizacaoComHooksEspecificos() {
        FakeUsuarioService service = new FakeUsuarioService();
        FakeUsuario usuario = new FakeUsuario();
        UUID id = UUID.randomUUID();
        usuario.setId(id);
        usuario.setNome("Ana");
        usuario.setEmail("ana@email.com");
        usuario.setTelefone("8599");
        usuario.documento = "DOC-1";
        service.entidadeExistente = usuario;

        String resposta = service.atualizar(id, new AtualizarUsuarioDto("Bia", "bia@email.com", "8899", "DOC-2"));

        assertEquals("Bia|bia@email.com|DOC-2", resposta);
        assertEquals(List.of(
                "buscarEntidadePorId",
                "existeEmail",
                "validarAtualizacaoEspecifica",
                "aplicarDadosEspecificosAtualizacao",
                "salvar",
                "mapearResposta"), service.eventos);
        assertEquals("Bia", usuario.getNome());
        assertEquals("bia@email.com", usuario.getEmail());
        assertEquals("8899", usuario.getTelefone());
        assertEquals("DOC-2", usuario.documento);
    }

    private record CriarUsuarioDto(String nome, String email, String telefone, String documento) {
    }

    private record AtualizarUsuarioDto(String nome, String email, String telefone, String documento) {
    }

    private static class FakeUsuario extends Usuario {
        private String documento;
    }

    private static class FakeUsuarioService extends UsuarioService<FakeUsuario, CriarUsuarioDto, AtualizarUsuarioDto, String> {
        private final List<String> eventos = new ArrayList<>();
        private boolean emailExiste;
        private FakeUsuario usuarioSalvo;
        private FakeUsuario entidadeExistente;
        private final RepositorioBase<FakeUsuario, UUID> repositorio = mock(RepositorioBase.class);

        private FakeUsuarioService() {
            when(repositorio.save(any(FakeUsuario.class))).thenAnswer(invocacao -> {
                FakeUsuario usuario = invocacao.getArgument(0);
                eventos.add("salvar");
                usuarioSalvo = usuario;
                return usuario;
            });
            when(repositorio.findById(any(UUID.class))).thenAnswer(invocacao -> {
                eventos.add("buscarEntidadePorId");
                return Optional.ofNullable(entidadeExistente);
            });
            when(repositorio.findAll()).thenAnswer(invocacao -> {
                eventos.add("listarEntidades");
                return List.of();
            });
            doAnswer(invocacao -> {
                eventos.add("excluir");
                return null;
            }).when(repositorio).delete(any(FakeUsuario.class));
        }

        @Override
        protected FakeUsuario construirEntidade(CriarUsuarioDto dto) {
            eventos.add("construirEntidade");
            return new FakeUsuario();
        }

        @Override
        protected RepositorioBase<FakeUsuario, UUID> repositorio() {
            return repositorio;
        }

        @Override
        protected MapperResposta<FakeUsuario, String> mapperResposta() {
            return this::mapear;
        }

        @Override
        protected RuntimeException erroUsuarioNaoEncontrado(UUID id) {
            return new IllegalStateException(id.toString());
        }

        private String mapear(FakeUsuario usuario) {
            eventos.add("mapearResposta");
            return usuario.getNome() + "|" + usuario.getEmail() + "|" + usuario.documento;
        }

        @Override
        protected boolean existeEmail(String email) {
            eventos.add("existeEmail");
            return emailExiste;
        }

        @Override
        protected RuntimeException erroEmailDuplicado(String email) {
            eventos.add("erroEmailDuplicado");
            return new IllegalStateException(email);
        }

        @Override
        protected String nomeCriacao(CriarUsuarioDto dto) {
            return dto.nome();
        }

        @Override
        protected String emailCriacao(CriarUsuarioDto dto) {
            return dto.email();
        }

        @Override
        protected String telefoneCriacao(CriarUsuarioDto dto) {
            return dto.telefone();
        }

        @Override
        protected String nomeAtualizacao(AtualizarUsuarioDto dto) {
            return dto.nome();
        }

        @Override
        protected String emailAtualizacao(AtualizarUsuarioDto dto) {
            return dto.email();
        }

        @Override
        protected String telefoneAtualizacao(AtualizarUsuarioDto dto) {
            return dto.telefone();
        }

        @Override
        protected void validarCriacaoEspecifica(CriarUsuarioDto dto) {
            eventos.add("validarCriacaoEspecifica");
        }

        @Override
        protected void validarAtualizacaoEspecifica(FakeUsuario usuario, AtualizarUsuarioDto dto) {
            eventos.add("validarAtualizacaoEspecifica");
        }

        @Override
        protected void aplicarDadosEspecificosCriacao(FakeUsuario usuario, CriarUsuarioDto dto) {
            eventos.add("aplicarDadosEspecificosCriacao");
            usuario.documento = dto.documento();
        }

        @Override
        protected void aplicarDadosEspecificosAtualizacao(FakeUsuario usuario, AtualizarUsuarioDto dto) {
            eventos.add("aplicarDadosEspecificosAtualizacao");
            usuario.documento = dto.documento();
        }

        @Override
        protected void aposCriar(FakeUsuario usuario, CriarUsuarioDto dto) {
            eventos.add("aposCriar");
        }
    }
}
