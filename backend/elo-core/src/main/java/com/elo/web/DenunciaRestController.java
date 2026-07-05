package com.elo.web;

import com.elo.denuncia.Denuncia;
import com.elo.denuncia.DenunciaService;
import com.elo.denuncia.StatusDenuncia;
import com.elo.oferta.Oferta;
import com.elo.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;
public abstract class DenunciaRestController<D extends Denuncia, O extends Oferta, U extends Usuario, C, R> {

    protected abstract DenunciaService<D, O, U, C, R> service();

    @PostMapping
    public ResponseEntity<R> criar(@Valid @RequestBody C dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service().criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<R>> listarTodas() {
        return ResponseEntity.ok(service().listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<R> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service().buscarPorId(id));
    }

    @GetMapping("/oferta/{ofertaId}")
    public ResponseEntity<List<R>> buscarPorOferta(@PathVariable UUID ofertaId) {
        return ResponseEntity.ok(service().buscarPorOfertaId(ofertaId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<R>> buscarPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(service().buscarPorUsuarioId(usuarioId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<R>> buscarPorStatus(@PathVariable StatusDenuncia status) {
        return ResponseEntity.ok(service().buscarPorStatus(status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<R> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody StatusDenuncia novoStatus) {
        return ResponseEntity.ok(service().atualizarStatus(id, novoStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service().deletar(id);
        return ResponseEntity.noContent().build();
    }
}
