package com.elo.web;

import com.elo.manifestacao.ManifestacaoInteresse;
import com.elo.manifestacao.ManifestacaoInteresseService;
import com.elo.oferta.Oferta;
import com.elo.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public abstract class ManifestacaoInteresseRestController<
        M extends ManifestacaoInteresse,
        O extends Oferta,
        U extends Usuario,
        C,
        R,
        D> {

    protected abstract ManifestacaoInteresseService<M, O, U, C, R, D> service();

    @PostMapping
    public ResponseEntity<D> criar(@Valid @RequestBody C dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service().criar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<D> buscarPorId(
            @PathVariable UUID id,
            @RequestParam UUID solicitanteId) {
        return ResponseEntity.ok(service().buscarPorId(id, solicitanteId));
    }

    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<D> aceitar(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId) {
        return ResponseEntity.ok(service().aceitar(id, publicadorId));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<D> recusar(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId) {
        return ResponseEntity.ok(service().recusar(id, publicadorId));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<D> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID interessadoId) {
        return ResponseEntity.ok(service().cancelar(id, interessadoId));
    }

    @GetMapping("/oferta/{ofertaId}")
    public ResponseEntity<List<R>> listarPorOferta(
            @PathVariable UUID ofertaId,
            @RequestParam UUID publicadorId) {
        return ResponseEntity.ok(service().listarPorOferta(ofertaId, publicadorId));
    }

    @GetMapping("/interessado/{interessadoId}")
    public ResponseEntity<List<R>> listarPorInteressado(@PathVariable UUID interessadoId) {
        return ResponseEntity.ok(service().listarPorInteressado(interessadoId));
    }
}
