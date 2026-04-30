package com.banco.mscuentas.application.service;

import com.banco.mscuentas.application.dto.CuentaRequestDTO;
import com.banco.mscuentas.application.dto.CuentaResponseDTO;

import java.util.List;

public interface CuentaService {
    CuentaResponseDTO crear(CuentaRequestDTO dto);
    CuentaResponseDTO obtenerPorId(Long id);
    List<CuentaResponseDTO> listarTodas();
    CuentaResponseDTO actualizar(Long id, CuentaRequestDTO dto);
    CuentaResponseDTO actualizarParcial(Long id, CuentaRequestDTO dto);
    void eliminar(Long id);
}
