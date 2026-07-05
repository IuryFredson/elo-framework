package com.elo.web;

import com.elo.denuncia.Denuncia;
import com.elo.moderacao.ModeracaoService;
import com.elo.oferta.Oferta;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public abstract class ModeracaoRestController<D extends Denuncia, O extends Oferta, C, R> {

    protected abstract ModeracaoService<D, O, C, R> service();

    @PatchMapping("/{denunciaId}")
    public ResponseEntity<R> moderar(
            @PathVariable UUID denunciaId,
            @Valid @RequestBody C dto) {
        return ResponseEntity.ok(service().moderar(denunciaId, dto));
    }
}
