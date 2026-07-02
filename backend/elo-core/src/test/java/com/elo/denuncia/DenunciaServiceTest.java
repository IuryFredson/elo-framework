package com.elo.denuncia;

import com.elo.oferta.Oferta;
import com.elo.persistencia.RepositorioBase;
import com.elo.usuario.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
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

class DenunciaServiceTest {

    @Test
    void deveCriarDenunciaComFluxoFixo() {
        FakeDenunciaService service = new FakeDenunciaService();
        UUID denuncianteId = UUID.randomUUID();
        UUID ofertaId = UUID.randomUUID();
        service.denunciante = new FakeUsuario(denuncianteId);
        service.oferta = new FakeOferta(ofertaId);

        String resposta = service.criar(new FakeCriarDenuncia(denuncianteId, ofertaId, FakeCriterio.OUTRO));

        assertEquals("PENDENTE", resposta);
        assertEquals(StatusDenuncia.PENDENTE, service.denuncia.status);
        assertEquals("OUTRO", service.denuncia.getCriterioCodigo());
        assertNotNull(service.denuncia.criadoEm);
        verify(service.repositorio).save(service.denuncia);
    }

    @Test
    void deveAplicarMaquinaDeEstadosValida() {
        FakeDenunciaService service = new FakeDenunciaService();
        service.denuncia = new FakeDenuncia(UUID.randomUUID());
        service.denuncia.status = StatusDenuncia.PENDENTE;
        when(service.repositorio.findById(service.denuncia.id)).thenReturn(Optional.of(service.denuncia));

        String resposta = service.atualizarStatus(service.denuncia.id, StatusDenuncia.EM_ANALISE);

        assertEquals("EM_ANALISE", resposta);
        assertEquals(StatusDenuncia.EM_ANALISE, service.denuncia.status);
        assertNotNull(service.denuncia.atualizadoEm);
    }

    @Test
    void deveBloquearTransicaoInvalida() {
        FakeDenunciaService service = new FakeDenunciaService();
        service.denuncia = new FakeDenuncia(UUID.randomUUID());
        service.denuncia.status = StatusDenuncia.PENDENTE;
        when(service.repositorio.findById(service.denuncia.id)).thenReturn(Optional.of(service.denuncia));

        assertThrows(IllegalStateException.class,
                () -> service.atualizarStatus(service.denuncia.id, StatusDenuncia.PROCEDENTE));
    }

    private enum FakeCriterio implements CriterioDenuncia {
        OUTRO;

        @Override
        public String codigo() {
            return name();
        }
    }

    private record FakeCriarDenuncia(UUID denuncianteId, UUID ofertaId, CriterioDenuncia criterio) {
    }

    private static class FakeUsuario extends Usuario {
        private FakeUsuario(UUID id) {
            setId(id);
        }
    }

    private record FakeOferta(UUID id) implements Oferta {
        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getPublicadorId() {
            return UUID.randomUUID();
        }

        @Override
        public String tipoOferta() {
            return "FAKE";
        }

        @Override
        public boolean isAtiva() {
            return true;
        }
    }

    private static class FakeDenuncia implements Denuncia {
        private final UUID id;
        private UUID denuncianteId;
        private UUID ofertaId;
        private CriterioDenuncia criterio;
        private StatusDenuncia status;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;

        private FakeDenuncia(UUID id) {
            this.id = id;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getDenuncianteId() {
            return denuncianteId;
        }

        @Override
        public UUID getOfertaId() {
            return ofertaId;
        }

        @Override
        public StatusDenuncia getStatus() {
            return status;
        }

        @Override
        public String getCriterioCodigo() {
            return criterio == null ? null : criterio.codigo();
        }
    }

    private static class FakeDenunciaService extends DenunciaService<
            FakeDenuncia,
            FakeOferta,
            FakeUsuario,
            FakeCriarDenuncia,
            String> {

        private final RepositorioBase<FakeDenuncia, UUID> repositorio = mock(RepositorioBase.class);
        private FakeUsuario denunciante;
        private FakeOferta oferta;
        private FakeDenuncia denuncia;

        private FakeDenunciaService() {
            when(repositorio.save(any(FakeDenuncia.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Override
        protected RepositorioBase<FakeDenuncia, UUID> repositorio() {
            return repositorio;
        }

        @Override
        protected UUID denuncianteId(FakeCriarDenuncia dto) {
            return dto.denuncianteId();
        }

        @Override
        protected UUID ofertaId(FakeCriarDenuncia dto) {
            return dto.ofertaId();
        }

        @Override
        protected FakeUsuario buscarDenunciante(UUID denuncianteId) {
            return denunciante;
        }

        @Override
        protected FakeOferta buscarOferta(UUID ofertaId) {
            return oferta;
        }

        @Override
        protected FakeDenuncia construirDenuncia(FakeCriarDenuncia dto) {
            denuncia = new FakeDenuncia(UUID.randomUUID());
            return denuncia;
        }

        @Override
        protected CriterioDenuncia criterio(FakeCriarDenuncia dto) {
            return dto.criterio();
        }

        @Override
        protected void aplicarCriacao(
                FakeDenuncia denuncia,
                FakeUsuario denunciante,
                FakeOferta oferta,
                FakeCriarDenuncia dto,
                CriterioDenuncia criterio,
                StatusDenuncia status,
                LocalDateTime criadoEm) {
            denuncia.denuncianteId = denunciante.getId();
            denuncia.ofertaId = oferta.getId();
            denuncia.criterio = criterio;
            denuncia.status = status;
            denuncia.criadoEm = criadoEm;
        }

        @Override
        protected void aplicarStatus(FakeDenuncia denuncia, StatusDenuncia status, LocalDateTime atualizadoEm) {
            denuncia.status = status;
            denuncia.atualizadoEm = atualizadoEm;
        }

        @Override
        protected List<FakeDenuncia> buscarPorOferta(FakeOferta oferta) {
            return List.of();
        }

        @Override
        protected List<FakeDenuncia> buscarPorDenunciante(FakeUsuario denunciante) {
            return List.of();
        }

        @Override
        protected List<FakeDenuncia> buscarPorStatusDenuncia(StatusDenuncia status) {
            return List.of();
        }

        @Override
        protected String mapear(FakeDenuncia denuncia) {
            return denuncia.status.name();
        }

        @Override
        protected RuntimeException erroDenunciaNaoEncontrada(UUID id) {
            return new IllegalStateException(id.toString());
        }

        @Override
        protected RuntimeException erroTransicaoInvalida(StatusDenuncia atual, StatusDenuncia novo) {
            return new IllegalStateException(atual + " -> " + novo);
        }
    }
}
