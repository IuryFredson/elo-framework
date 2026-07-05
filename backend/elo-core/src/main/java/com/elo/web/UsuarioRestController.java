package com.elo.web;

import com.elo.usuario.Usuario;
import com.elo.usuario.UsuarioService;
import com.elo.web.dto.AlterarAtivoRequestDTO;
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

import java.util.List;
import java.util.UUID;

public abstract class UsuarioRestController<T extends Usuario, C, A, R> {

    protected abstract UsuarioService<T, C, A, R> service();

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
    public ResponseEntity<R> atualizar(@PathVariable UUID id, @Valid @RequestBody A dto) {
        return ResponseEntity.ok(service().atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<R> alterarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AlterarAtivoRequestDTO dto) {
        return ResponseEntity.ok(service().alterarStatus(id, dto.ativo()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service().deletar(id);
        return ResponseEntity.noContent().build();
    }
}
