package com.elo.moderacao;

import com.elo.denuncia.Denuncia;
import com.elo.denuncia.StatusDenuncia;
import com.elo.oferta.Oferta;
import com.elo.persistencia.RepositorioBase;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModeracaoServiceTest {

    @Test
    void deveAplicarProcedenciaEPausarOferta() {
        FakeModeracaoService service = new FakeModeracaoService();
        service.denuncia = new FakeDenuncia(UUID.randomUUID(), StatusDenuncia.EM_ANALISE);
        service.oferta = new FakeOferta(UUID.randomUUID());
        when(service.denunciaRepository.findById(service.denuncia.id)).thenReturn(Optional.of(service.denuncia));

        String resposta = service.moderar(
                service.denuncia.id,
                new FakeModeracao(StatusDenuncia.PROCEDENTE, AcaoModeracaoOferta.PAUSAR));

        assertEquals("PROCEDENTE:PAUSADA", resposta);
        assertEquals(StatusDenuncia.PROCEDENTE, service.denuncia.status);
        assertEquals("PAUSADA", service.oferta.status);
        verify(service.ofertaRepository).save(service.oferta);
        verify(service.denunciaRepository).save(service.denuncia);
    }

    @Test
    void deveBloquearProcedenciaSemAcaoNaOferta() {
        FakeModeracaoService service = new FakeModeracaoService();
        service.denuncia = new FakeDenuncia(UUID.randomUUID(), StatusDenuncia.EM_ANALISE);
        service.oferta = new FakeOferta(UUID.randomUUID());
        when(service.denunciaRepository.findById(service.denuncia.id)).thenReturn(Optional.of(service.denuncia));

        assertThrows(IllegalStateException.class,
                () -> service.moderar(
                        service.denuncia.id,
                        new FakeModeracao(StatusDenuncia.PROCEDENTE, AcaoModeracaoOferta.NENHUMA)));
    }

    private record FakeModeracao(StatusDenuncia novoStatus, AcaoModeracaoOferta acao) {
    }

    private static class FakeDenuncia implements Denuncia {
        private final UUID id;
        private StatusDenuncia status;
        private LocalDateTime atualizadoEm;

        private FakeDenuncia(UUID id, StatusDenuncia status) {
            this.id = id;
            this.status = status;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public UUID getDenuncianteId() {
            return UUID.randomUUID();
        }

        @Override
        public UUID getOfertaId() {
            return UUID.randomUUID();
        }

        @Override
        public StatusDenuncia getStatus() {
            return status;
        }

        @Override
        public String getCriterioCodigo() {
            return "OUTRO";
        }
    }

    private static class FakeOferta implements Oferta {
        private final UUID id;
        private String status = "ATIVA";

        private FakeOferta(UUID id) {
            this.id = id;
        }

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
            return "ATIVA".equals(status);
        }
    }

    private static class FakeModeracaoService extends ModeracaoService<
            FakeDenuncia,
            FakeOferta,
            FakeModeracao,
            String> {

        private final RepositorioBase<FakeDenuncia, UUID> denunciaRepository = mock(RepositorioBase.class);
        private final RepositorioBase<FakeOferta, UUID> ofertaRepository = mock(RepositorioBase.class);
        private FakeDenuncia denuncia;
        private FakeOferta oferta;

        private FakeModeracaoService() {
            when(denunciaRepository.save(any(FakeDenuncia.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(ofertaRepository.save(any(FakeOferta.class))).thenAnswer(invocation -> invocation.getArgument(0));
        }

        @Override
        protected RepositorioBase<FakeDenuncia, UUID> repositorioDenuncia() {
            return denunciaRepository;
        }

        @Override
        protected RepositorioBase<FakeOferta, UUID> repositorioOferta() {
            return ofertaRepository;
        }

        @Override
        protected FakeOferta ofertaDaDenuncia(FakeDenuncia denuncia) {
            return oferta;
        }

        @Override
        protected StatusDenuncia novoStatus(FakeModeracao dto) {
            return dto.novoStatus();
        }

        @Override
        protected AcaoModeracaoOferta acaoOferta(FakeModeracao dto) {
            return dto.acao();
        }

        @Override
        protected Object statusOferta(FakeOferta oferta) {
            return oferta.status;
        }

        @Override
        protected void aplicarStatusDenuncia(FakeDenuncia denuncia, StatusDenuncia novoStatus, LocalDateTime atualizadoEm) {
            denuncia.status = novoStatus;
            denuncia.atualizadoEm = atualizadoEm;
        }

        @Override
        protected void pausarOferta(FakeOferta oferta) {
            oferta.status = "PAUSADA";
        }

        @Override
        protected void encerrarOferta(FakeOferta oferta) {
            oferta.status = "ENCERRADA";
        }

        @Override
        protected String mapearResposta(
                FakeDenuncia denuncia,
                FakeOferta oferta,
                StatusDenuncia statusAnteriorDenuncia,
                Object statusAnteriorOferta,
                FakeModeracao dto,
                LocalDateTime moderadoEm) {
            return denuncia.status.name() + ":" + oferta.status;
        }

        @Override
        protected RuntimeException erroDenunciaNaoEncontrada(UUID id) {
            return new IllegalStateException(id.toString());
        }

        @Override
        protected RuntimeException erroModeracaoInvalida(String mensagem) {
            return new IllegalStateException(mensagem);
        }
    }
}
