package com.banco.msclientes.infrastructure.controller;

import com.banco.msclientes.application.dto.ClienteRequestDTO;
import com.banco.msclientes.application.dto.ClienteResponseDTO;
import com.banco.msclientes.application.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ClienteResponseDTO> crear(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(clienteService.actualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
