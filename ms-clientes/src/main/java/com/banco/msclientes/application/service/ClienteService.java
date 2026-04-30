package com.banco.msclientes.application.service;

import com.banco.msclientes.application.dto.ClienteRequestDTO;
import com.banco.msclientes.application.dto.ClienteResponseDTO;

import java.util.List;

public interface ClienteService {
    ClienteResponseDTO crear(ClienteRequestDTO dto);
    ClienteResponseDTO obtenerPorId(Long id);
    ClienteResponseDTO obtenerPorClienteId(String clienteId);
    List<ClienteResponseDTO> listarTodos();
    ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto);
    ClienteResponseDTO actualizarParcial(Long id, ClienteRequestDTO dto);
    void eliminar(Long id);
}
