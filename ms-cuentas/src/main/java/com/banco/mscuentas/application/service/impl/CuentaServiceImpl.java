package com.banco.mscuentas.application.service.impl;

import com.banco.mscuentas.application.dto.CuentaRequestDTO;
import com.banco.mscuentas.application.dto.CuentaResponseDTO;
import com.banco.mscuentas.application.service.CuentaService;
import com.banco.mscuentas.domain.entity.Cuenta;
import com.banco.mscuentas.domain.repository.CuentaRepository;
import com.banco.mscuentas.infrastructure.exception.BusinessException;
import com.banco.mscuentas.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;

    @Override
    @Transactional
    public CuentaResponseDTO crear(CuentaRequestDTO dto) {
        if (cuentaRepository.existsByNumeroCuenta(dto.getNumeroCuenta())) {
            throw new BusinessException("Ya existe una cuenta con el número: " + dto.getNumeroCuenta());
        }
        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(dto.getNumeroCuenta())
                .tipoCuenta(dto.getTipoCuenta())
                .saldoInicial(dto.getSaldoInicial())
                .saldoDisponible(dto.getSaldoInicial())
                .estado(dto.getEstado())
                .clienteId(dto.getClienteId())
                .build();
        return mapToResponse(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponseDTO obtenerPorId(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponseDTO> listarTodas() {
        return cuentaRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CuentaResponseDTO actualizar(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = findById(id);
        cuenta.setNumeroCuenta(dto.getNumeroCuenta());
        cuenta.setTipoCuenta(dto.getTipoCuenta());
        cuenta.setSaldoInicial(dto.getSaldoInicial());
        cuenta.setEstado(dto.getEstado());
        cuenta.setClienteId(dto.getClienteId());
        return mapToResponse(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaResponseDTO actualizarParcial(Long id, CuentaRequestDTO dto) {
        Cuenta cuenta = findById(id);
        if (dto.getTipoCuenta() != null) cuenta.setTipoCuenta(dto.getTipoCuenta());
        if (dto.getEstado() != null) cuenta.setEstado(dto.getEstado());
        if (dto.getClienteId() != null) cuenta.setClienteId(dto.getClienteId());
        return mapToResponse(cuentaRepository.save(cuenta));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        cuentaRepository.delete(findById(id));
    }

    private Cuenta findById(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada con id: " + id));
    }

    private CuentaResponseDTO mapToResponse(Cuenta cuenta) {
        return CuentaResponseDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .build();
    }
}
