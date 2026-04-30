package com.banco.mscuentas.infrastructure.controller;

import com.banco.mscuentas.application.dto.CuentaRequestDTO;
import com.banco.mscuentas.application.dto.CuentaResponseDTO;
import com.banco.mscuentas.application.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(cuentaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuentaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizarParcial(
            @PathVariable Long id,
            @RequestBody CuentaRequestDTO dto) {
        return ResponseEntity.ok(cuentaService.actualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
