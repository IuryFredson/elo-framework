package com.elo.perfil;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PerfilServiceTest {

    @Test
    void deveBuscarPerfilUsandoHooksDaInstancia() {
        FakePerfilService service = new FakePerfilService();
        UUID id = UUID.randomUUID();
        FakeUsuario usuario = new FakeUsuario(id);
        usuario.perfil = new FakePerfil("silencioso");
        service.usuario = usuario;

        String resposta = service.buscarPerfil(id);

        assertEquals("silencioso", resposta);
        assertEquals(List.of("buscarDonoPerfil", "mapearResposta"), service.eventos);
    }

    @Test
    void deveCriarPerfilQuandoAusenteDuranteAtualizacao() {
        FakePerfilService service = new FakePerfilService();
        UUID id = UUID.randomUUID();
        service.usuario = new FakeUsuario(id);

        String resposta = service.atualizarPerfil(id, new PerfilDto("organizado"));

        assertEquals("organizado", resposta);
        assertEquals(List.of(
                "buscarDonoPerfil",
                "validarAtualizacao",
                "aplicarDadosDonoPerfil",
                "obterPerfil",
                "criarPerfil",
                "associarPerfil",
                "aplicarDadosPerfil",
                "salvarDonoPerfil",
                "mapearResposta"), service.eventos);
        assertEquals("organizado", service.usuario.perfil.descricao);
    }

    @Test
    void deveAtualizarPerfilExistenteSemCriarOutro() {
        FakePerfilService service = new FakePerfilService();
        UUID id = UUID.randomUUID();
        service.usuario = new FakeUsuario(id);
        service.usuario.perfil = new FakePerfil("antigo");

        String resposta = service.atualizarPerfil(id, new PerfilDto("novo"));

        assertEquals("novo", resposta);
        assertEquals(false, service.eventos.contains("criarPerfil"));
        assertEquals(false, service.eventos.contains("associarPerfil"));
        assertEquals("novo", service.usuario.perfil.descricao);
    }

    @Test
    void deveInterromperAtualizacaoQuandoValidacaoFalhar() {
        FakePerfilService service = new FakePerfilService();
        service.usuario = new FakeUsuario(UUID.randomUUID());
        service.validacaoFalha = true;

        assertThrows(IllegalStateException.class,
                () -> service.atualizarPerfil(service.usuario.id, new PerfilDto("novo")));

        assertEquals(List.of("buscarDonoPerfil", "validarAtualizacao"), service.eventos);
    }

    private record PerfilDto(String descricao) {
    }

    private static class FakeUsuario {
        private final UUID id;
        private FakePerfil perfil;

        private FakeUsuario(UUID id) {
            this.id = id;
        }
    }

    private static class FakePerfil implements Perfil {
        private String descricao;

        private FakePerfil(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String tipoPerfil() {
            return "FAKE";
        }
    }

    private static class FakePerfilService extends PerfilService<FakeUsuario, FakePerfil, PerfilDto, String> {
        private final List<String> eventos = new ArrayList<>();
        private FakeUsuario usuario;
        private boolean validacaoFalha;
        private final RepositorioBase<FakeUsuario, UUID> repositorio = mock(RepositorioBase.class);

        private FakePerfilService() {
            when(repositorio.findById(any(UUID.class))).thenAnswer(invocacao -> {
                eventos.add("buscarDonoPerfil");
                return Optional.ofNullable(usuario);
            });
            when(repositorio.save(any(FakeUsuario.class))).thenAnswer(invocacao -> {
                eventos.add("salvarDonoPerfil");
                return invocacao.getArgument(0);
            });
        }

        @Override
        protected RepositorioBase<FakeUsuario, UUID> repositorioDonoPerfil() {
            return repositorio;
        }

        @Override
        protected MapperResposta<FakeUsuario, String> mapperResposta() {
            return this::mapear;
        }

        @Override
        protected RuntimeException erroDonoPerfilNaoEncontrado(UUID usuarioId) {
            return new IllegalStateException(usuarioId.toString());
        }

        @Override
        protected FakePerfil obterPerfil(FakeUsuario usuario) {
            eventos.add("obterPerfil");
            return usuario.perfil;
        }

        @Override
        protected FakePerfil criarPerfil(PerfilDto dto) {
            eventos.add("criarPerfil");
            return new FakePerfil(null);
        }

        @Override
        protected void associarPerfil(FakeUsuario usuario, FakePerfil perfil) {
            eventos.add("associarPerfil");
            usuario.perfil = perfil;
        }

        @Override
        protected void validarAtualizacao(FakeUsuario usuario, PerfilDto dto) {
            eventos.add("validarAtualizacao");
            if (validacaoFalha) {
                throw new IllegalStateException("perfil invalido");
            }
        }

        @Override
        protected void aplicarDadosDonoPerfil(FakeUsuario usuario, PerfilDto dto) {
            eventos.add("aplicarDadosDonoPerfil");
        }

        @Override
        protected void aplicarDadosPerfil(FakePerfil perfil, PerfilDto dto) {
            eventos.add("aplicarDadosPerfil");
            perfil.descricao = dto.descricao();
        }

        private String mapear(FakeUsuario usuario) {
            eventos.add("mapearResposta");
            FakePerfil perfil = usuario.perfil;
            return perfil == null ? null : perfil.descricao;
        }
    }
}
