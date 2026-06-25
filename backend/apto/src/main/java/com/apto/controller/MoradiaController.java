package com.apto.controller;

import com.apto.dto.request.AtualizarMoradiaRequestDTO;
import com.apto.dto.request.CriarMoradiaRequestDTO;
import com.apto.dto.response.MoradiaResponseDTO;
import com.apto.service.MoradiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/moradias")
public class MoradiaController {
    private final MoradiaService moradiaService;

    public MoradiaController(MoradiaService moradiaService) {
        this.moradiaService = moradiaService;
    }

    @GetMapping
    public ResponseEntity<List<MoradiaResponseDTO>> listarTodas(){
        return ResponseEntity.ok(moradiaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoradiaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(moradiaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MoradiaResponseDTO> criar(@Valid @RequestBody CriarMoradiaRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(moradiaService.criar(dto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<MoradiaResponseDTO> atualizr(@PathVariable UUID id, @Valid @RequestBody AtualizarMoradiaRequestDTO dto){
        return ResponseEntity.ok(moradiaService.atualizar(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MoradiaResponseDTO> excluir(@PathVariable UUID id){
        moradiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
