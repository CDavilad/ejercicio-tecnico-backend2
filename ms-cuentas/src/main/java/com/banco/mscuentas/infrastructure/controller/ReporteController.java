package com.banco.mscuentas.infrastructure.controller;

import com.banco.mscuentas.application.dto.ReporteDTO;
import com.banco.mscuentas.application.service.MovimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<ReporteDTO>> generarReporte(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(movimientoService.generarReporte(clienteId, fechaInicio, fechaFin));
    }
}
