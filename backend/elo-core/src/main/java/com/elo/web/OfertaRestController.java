package com.elo.web;

import com.elo.oferta.Oferta;
import com.elo.oferta.OfertaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public abstract class OfertaRestController<O extends Oferta, C, A, R, S> {

    protected abstract OfertaService<O, C, A, R, S> service();

    @PostMapping
    public ResponseEntity<R> criar(@Valid @RequestBody C dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service().criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<R>> listarTodos() {
        return ResponseEntity.ok(service().listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<R> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service().buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<R> atualizar(
            @PathVariable UUID id,
            @RequestParam UUID publicadorId,
            @Valid @RequestBody A dto) {
        return ResponseEntity.ok(service().atualizar(id, publicadorId, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<R> atualizarStatus(@PathVariable UUID id, @RequestBody S novoStatus) {
        return ResponseEntity.ok(service().atualizarStatus(id, novoStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service().deletar(id);
        return ResponseEntity.noContent().build();
    }
}
