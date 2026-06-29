package com.elo.manifestacao;

import com.elo.oferta.Oferta;
import com.elo.persistencia.RepositorioBase;
import com.elo.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManifestacaoInteresseServiceTest {

    @Test
    void deveCriarManifestacaoAplicandoRegrasFixas() {
        FakeManifestacaoService service = new FakeManifestacaoService();
        UUID ofertaId = UUID.randomUUID();
        UUID publicadorId = UUID.randomUUID();
        UUID interessadoId = UUID.randomUUID();
        service.oferta = new FakeOferta(ofertaId, publicadorId, true);
        service.interessado = new FakeUsuario(interessadoId);

        String resposta = service.criar(new FakeCriarManifestacao(ofertaId, interessadoId, "tenho interesse"));

        assertEquals("PENDENTE", resposta);
        assertEquals(StatusManifestacaoInteresse.PENDENTE, service.manifestacao.status);
        assertEquals(ofertaId, service.manifestacao.ofertaId);
        assertEquals(interessadoId, service.manifestacao.interessadoId);
        assertNotNull(service.manifestacao.dataManifestacao);
        verify(service.repositorio).save(service.manifestacao);
    }

    @Test
    void naoDeveCriarManifestacaoParaOfertaInativa() {
        FakeManifestacaoService service = new FakeManifestacaoService();
        UUID usuarioId = UUID.randomUUID();
        service.oferta = new FakeOferta(UUID.randomUUID(), UUID.randomUUID(), false);
        service.interessado = new FakeUsuario(usuarioId);

        assertThrows(IllegalStateException.class,
                () -> service.criar(new FakeCriarManifestacao(service.oferta.id, usuarioId, "msg")));
    }

    @Test
    void naoDeveCriarManifestacaoNoProprioAnuncio() {
        FakeManifestacaoService service = new FakeManifestacaoService();
        UUID usuarioId = UUID.randomUUID();
        service.oferta = new FakeOferta(UUID.randomUUID(), usuarioId, true);
        service.interessado = new FakeUsuario(usuarioId);

        assertThrows(IllegalArgumentException.class,
                () -> service.criar(new FakeCriarManifestacao(service.oferta.id, usuarioId, "msg")));
    }

    @Test
    void deveResponderSomenteManifestacaoPendente() {
        FakeManifestacaoService service = new FakeManifestacaoService();
        UUID publicadorId = UUID.randomUUID();
        service.manifestacao = new FakeManifestacao(UUID.randomUUID(), UUID.randomUUID(), publicadorId);
        service.manifestacao.status = StatusManifestacaoInteresse.PENDENTE;
        when(service.repositorio.findById(service.manifestacao.id)).thenReturn(Optional.of(service.manifestacao));

        String resposta = service.aceitar(service.manifestacao.id, publicadorId);

        assertEquals("ACEITA", resposta);
        assertEquals(StatusManifestacaoInteresse.ACEITA, service.manifestacao.status);
        assertNotNull(service.manifestacao.dataResposta);
        verify(service.repositorio).save(service.manifestacao);
    }

    @Test
    void deveCancelarManifestacoesPendentesDaOferta() {
        FakeManifestacaoService service = new FakeManifestacaoService();
        UUID ofertaId = UUID.randomUUID();
        FakeManifestacao primeira = new FakeManifestacao(UUID.randomUUID(), ofertaId, UUID.randomUUID());
        FakeManifestacao segunda = new FakeManifestacao(UUID.randomUUID(), ofertaId, UUID.randomUUID());
        service.pendentes = List.of(primeira, segunda);

        service.cancelarPendentesDaOferta(ofertaId);

        assertEquals(StatusManifestacaoInteresse.CANCELADA, primeira.status);
        assertEquals(StatusManifestacaoInteresse.CANCELADA, segunda.status);
        verify(service.repositorio).saveAll(service.pendentes);
    }

    private record FakeCriarManifestacao(UUID ofertaId, UUID interessadoId, String mensagem) {
    }

    private static class FakeUsuario extends Usuario {
        private FakeUsuario(UUID id) {
            setId(id);
        }
    }

    private static class FakeOferta implements Oferta {
        private final UUID id;
        private final UUID publicadorId;
        private final boolean ativa;

        private FakeOferta(UUID id, UUID publicadorId, boolean ativa) {
            this.id = id;
            this.publicadorId = publicadorId;
            this.ativa = ativa;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getPublicadorId() {
            return publicadorId;
        }

        @Override
        public String tipoOferta() {
            return "FAKE";
        }

        @Override
        public boolean isAtiva() {
            return ativa;
        }
    }

    private static class FakeManifestacao implements ManifestacaoInteresse {
        private final UUID id;
        private UUID ofertaId;
        private UUID interessadoId;
        private UUID publicadorId;
        private StatusManifestacaoInteresse status;
        private LocalDateTime dataManifestacao;
        private LocalDateTime dataResposta;

        private FakeManifestacao(UUID id, UUID ofertaId, UUID publicadorId) {
            this.id = id;
            this.ofertaId = ofertaId;
            this.publicadorId = publicadorId;
            this.status = StatusManifestacaoInteresse.PENDENTE;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getInteressadoId() {
            return interessadoId;
        }

        @Override
        public UUID getOfertaId() {
            return ofertaId;
        }

        @Override
        public StatusManifestacaoInteresse getStatus() {
            return status;
        }
    }

    private static class FakeManifestacaoService extends ManifestacaoInteresseService<
            FakeManifestacao,
            FakeOferta,
            FakeUsuario,
            FakeCriarManifestacao,
            String,
            String> {

        private final RepositorioBase<FakeManifestacao, UUID> repositorio = mock(RepositorioBase.class);
        private FakeOferta oferta;
        private FakeUsuario interessado;
        private FakeManifestacao manifestacao;
        private List<FakeManifestacao> pendentes = List.of();

        private FakeManifestacaoService() {
            when(repositorio.save(any(FakeManifestacao.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        }

        @Override
        protected RepositorioBase<FakeManifestacao, UUID> repositorio() {
            return repositorio;
        }

        @Override
        protected UUID ofertaId(FakeCriarManifestacao dto) {
            return dto.ofertaId();
        }

        @Override
        protected UUID interessadoId(FakeCriarManifestacao dto) {
            return dto.interessadoId();
        }

        @Override
        protected FakeOferta buscarOferta(UUID ofertaId) {
            return oferta;
        }

        @Override
        protected FakeUsuario buscarInteressado(UUID interessadoId) {
            return interessado;
        }

        @Override
        protected FakeManifestacao construirManifestacao(
                FakeOferta oferta,
                FakeUsuario interessado,
                FakeCriarManifestacao dto) {
            manifestacao = new FakeManifestacao(UUID.randomUUID(), oferta.getId(), oferta.getPublicadorId());
            return manifestacao;
        }

        @Override
        protected void aplicarCriacao(
                FakeManifestacao manifestacao,
                FakeOferta oferta,
                FakeUsuario interessado,
                FakeCriarManifestacao dto,
                StatusManifestacaoInteresse status,
                LocalDateTime dataManifestacao) {
            manifestacao.ofertaId = oferta.getId();
            manifestacao.interessadoId = interessado.getId();
            manifestacao.publicadorId = oferta.getPublicadorId();
            manifestacao.status = status;
            manifestacao.dataManifestacao = dataManifestacao;
        }

        @Override
        protected void aplicarResposta(
                FakeManifestacao manifestacao,
                StatusManifestacaoInteresse status,
                LocalDateTime dataResposta) {
            manifestacao.status = status;
            manifestacao.dataResposta = dataResposta;
        }

        @Override
        protected boolean existeManifestacaoAtiva(
                UUID ofertaId,
                UUID interessadoId,
                Collection<StatusManifestacaoInteresse> statusAtivos) {
            return false;
        }

        @Override
        protected List<FakeManifestacao> buscarPorOfertaOrdenado(UUID ofertaId) {
            return List.of();
        }

        @Override
        protected List<FakeManifestacao> buscarPorInteressadoOrdenado(UUID interessadoId) {
            return List.of();
        }

        @Override
        protected List<FakeManifestacao> buscarPorOfertaEStatus(
                UUID ofertaId,
                StatusManifestacaoInteresse status) {
            return pendentes;
        }

        @Override
        protected UUID publicadorId(FakeManifestacao manifestacao) {
            return manifestacao.publicadorId;
        }

        @Override
        protected String mapearResumo(FakeManifestacao manifestacao) {
            return manifestacao.status.name();
        }

        @Override
        protected String mapearDetalhe(FakeManifestacao manifestacao) {
            return manifestacao.status.name();
        }

        @Override
        protected RuntimeException erroManifestacaoNaoEncontrada(UUID id) {
            return new IllegalStateException(id.toString());
        }

        @Override
        protected RuntimeException erroOfertaInativa() {
            return new IllegalStateException("oferta inativa");
        }

        @Override
        protected RuntimeException erroInteresseProprio() {
            return new IllegalArgumentException("interesse proprio");
        }

        @Override
        protected RuntimeException erroManifestacaoDuplicada() {
            return new IllegalStateException("duplicada");
        }

        @Override
        protected RuntimeException erroAcessoNegado() {
            return new SecurityException("acesso negado");
        }

        @Override
        protected RuntimeException erroTransicaoInvalida() {
            return new IllegalStateException("transicao invalida");
        }
    }
}
