package com.banco.mscuentas.application.service;

import com.banco.mscuentas.application.dto.MovimientoRequestDTO;
import com.banco.mscuentas.application.dto.MovimientoResponseDTO;
import com.banco.mscuentas.application.dto.ReporteDTO;

import java.time.LocalDate;
import java.util.List;

public interface MovimientoService {
    MovimientoResponseDTO registrar(MovimientoRequestDTO dto);
    MovimientoResponseDTO obtenerPorId(Long id);
    List<MovimientoResponseDTO> listarTodos();
    void eliminar(Long id);
    List<ReporteDTO> generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
