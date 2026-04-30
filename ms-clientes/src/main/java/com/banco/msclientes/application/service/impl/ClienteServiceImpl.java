package com.banco.msclientes.application.service.impl;

import com.banco.msclientes.application.dto.ClienteRequestDTO;
import com.banco.msclientes.application.dto.ClienteResponseDTO;
import com.banco.msclientes.application.service.ClienteService;
import com.banco.msclientes.domain.entity.Cliente;
import com.banco.msclientes.domain.repository.ClienteRepository;
import com.banco.msclientes.infrastructure.exception.ResourceNotFoundException;
import com.banco.msclientes.infrastructure.exception.BusinessException;
import com.banco.msclientes.infrastructure.messaging.ClienteEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClienteResponseDTO crear(ClienteRequestDTO dto) {
        if (clienteRepository.existsByClienteId(dto.getClienteId())) {
            throw new BusinessException("Ya existe un cliente con el clienteId: " + dto.getClienteId());
        }
        if (clienteRepository.existsByIdentificacion(dto.getIdentificacion())) {
            throw new BusinessException("Ya existe una persona con la identificación: " + dto.getIdentificacion());
        }
        Cliente cliente = mapToEntity(dto);
        cliente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        Cliente saved = clienteRepository.save(cliente);
        eventPublisher.publicarClienteCreado(saved.getClienteId(), saved.getNombre());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return mapToResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorClienteId(String clienteId) {
        Cliente cliente = clienteRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con clienteId: " + clienteId));
        return mapToResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        actualizarCampos(cliente, dto);
        return mapToResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponseDTO actualizarParcial(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        if (dto.getNombre() != null) cliente.setNombre(dto.getNombre());
        if (dto.getGenero() != null) cliente.setGenero(dto.getGenero());
        if (dto.getEdad() != null) cliente.setEdad(dto.getEdad());
        if (dto.getDireccion() != null) cliente.setDireccion(dto.getDireccion());
        if (dto.getTelefono() != null) cliente.setTelefono(dto.getTelefono());
        if (dto.getContrasena() != null) cliente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        if (dto.getEstado() != null) cliente.setEstado(dto.getEstado());
        return mapToResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        clienteRepository.delete(cliente);
        eventPublisher.publicarClienteEliminado(cliente.getClienteId());
    }

    private void actualizarCampos(Cliente cliente, ClienteRequestDTO dto) {
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        cliente.setEstado(dto.getEstado());
    }

    private Cliente mapToEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setGenero(dto.getGenero());
        cliente.setEdad(dto.getEdad());
        cliente.setIdentificacion(dto.getIdentificacion());
        cliente.setDireccion(dto.getDireccion());
        cliente.setTelefono(dto.getTelefono());
        cliente.setClienteId(dto.getClienteId());
        cliente.setContrasena(dto.getContrasena());
        cliente.setEstado(dto.getEstado());
        return cliente;
    }

    private ClienteResponseDTO mapToResponse(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .estado(cliente.getEstado())
                .build();
    }
}
