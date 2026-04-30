package com.banco.mscuentas.application.service.impl;

import com.banco.mscuentas.application.dto.MovimientoRequestDTO;
import com.banco.mscuentas.application.dto.MovimientoResponseDTO;
import com.banco.mscuentas.application.dto.ReporteDTO;
import com.banco.mscuentas.application.service.MovimientoService;
import com.banco.mscuentas.domain.entity.Cuenta;
import com.banco.mscuentas.domain.entity.Movimiento;
import com.banco.mscuentas.domain.repository.CuentaRepository;
import com.banco.mscuentas.domain.repository.MovimientoRepository;
import com.banco.mscuentas.infrastructure.exception.BusinessException;
import com.banco.mscuentas.infrastructure.exception.ResourceNotFoundException;
import com.banco.mscuentas.infrastructure.exception.SaldoInsuficienteException;
import com.banco.mscuentas.infrastructure.messaging.ClienteCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final ClienteCache clienteCache;

    @Override
    @Transactional
    public MovimientoResponseDTO registrar(MovimientoRequestDTO dto) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(dto.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada con número: " + dto.getNumeroCuenta()));

        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(dto.getValor());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoInsuficienteException("Saldo no disponible");
        }

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        String tipoMovimiento = dto.getValor().compareTo(BigDecimal.ZERO) >= 0 ? "Deposito" : "Retiro";

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(dto.getValor())
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        return mapToResponse(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponseDTO obtenerPorId(Long id) {
        return mapToResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> listarTodos() {
        return movimientoRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        movimientoRepository.delete(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReporteDTO> generarReporte(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        String nombreCliente = clienteCache.obtenerNombre(clienteId);

        return movimientoRepository
                .findByClienteIdAndFechaBetween(clienteId, inicio, fin)
                .stream()
                .map(m -> ReporteDTO.builder()
                        .fecha(m.getFecha())
                        .cliente(nombreCliente)
                        .numeroCuenta(m.getCuenta().getNumeroCuenta())
                        .tipo(m.getCuenta().getTipoCuenta())
                        .saldoInicial(m.getCuenta().getSaldoInicial())
                        .estado(m.getCuenta().getEstado())
                        .movimiento(m.getValor())
                        .saldoDisponible(m.getSaldo())
                        .build())
                .collect(Collectors.toList());
    }

    private Movimiento findById(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado con id: " + id));
    }

    private MovimientoResponseDTO mapToResponse(Movimiento m) {
        return MovimientoResponseDTO.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .tipoMovimiento(m.getTipoMovimiento())
                .valor(m.getValor())
                .saldo(m.getSaldo())
                .numeroCuenta(m.getCuenta().getNumeroCuenta())
                .build();
    }
}
