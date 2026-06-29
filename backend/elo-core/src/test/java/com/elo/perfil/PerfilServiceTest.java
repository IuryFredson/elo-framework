package com.elo.perfil;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertEquals(List.of("buscarDonoPerfil", "obterPerfil", "mapearResposta"), service.eventos);
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
                "obterPerfil",
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

        @Override
        protected FakeUsuario buscarDonoPerfil(UUID usuarioId) {
            eventos.add("buscarDonoPerfil");
            return usuario;
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

        @Override
        protected FakeUsuario salvarDonoPerfil(FakeUsuario usuario) {
            eventos.add("salvarDonoPerfil");
            return usuario;
        }

        @Override
        protected String mapearResposta(FakeUsuario usuario, FakePerfil perfil) {
            eventos.add("mapearResposta");
            return perfil == null ? null : perfil.descricao;
        }
    }
}
