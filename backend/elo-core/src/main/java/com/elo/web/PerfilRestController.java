package com.elo.web;

import com.elo.perfil.Perfil;
import com.elo.perfil.PerfilService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public abstract class PerfilRestController<U, P extends Perfil, A, R> {

    protected abstract PerfilService<U, P, A, R> service();

    @GetMapping("/{id}/perfil")
    public ResponseEntity<R> buscarPerfil(@PathVariable UUID id) {
        return ResponseEntity.ok(service().buscarPerfil(id));
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<R> atualizarPerfil(@PathVariable UUID id, @Valid @RequestBody A dto) {
        return ResponseEntity.ok(service().atualizarPerfil(id, dto));
    }
}
