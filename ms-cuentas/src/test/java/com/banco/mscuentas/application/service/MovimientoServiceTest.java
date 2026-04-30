package com.banco.mscuentas.application.service;

import com.banco.mscuentas.application.dto.MovimientoRequestDTO;
import com.banco.mscuentas.application.dto.MovimientoResponseDTO;
import com.banco.mscuentas.application.service.impl.MovimientoServiceImpl;
import com.banco.mscuentas.domain.entity.Cuenta;
import com.banco.mscuentas.domain.entity.Movimiento;
import com.banco.mscuentas.domain.repository.CuentaRepository;
import com.banco.mscuentas.domain.repository.MovimientoRepository;
import com.banco.mscuentas.infrastructure.exception.ResourceNotFoundException;
import com.banco.mscuentas.infrastructure.exception.SaldoInsuficienteException;
import com.banco.mscuentas.infrastructure.messaging.ClienteCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteCache clienteCache;

    @InjectMocks
    private MovimientoServiceImpl movimientoService;

    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta("Ahorro")
                .saldoInicial(new BigDecimal("2000.00"))
                .saldoDisponible(new BigDecimal("2000.00"))
                .estado(true)
                .clienteId("jose.lema")
                .build();
    }

    @Test
    void registrar_DebeActualizarSaldo_CuandoHaySaldoSuficiente() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO("478758", new BigDecimal("-575.00"));

        Movimiento movimientoGuardado = Movimiento.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento("Retiro")
                .valor(new BigDecimal("-575.00"))
                .saldo(new BigDecimal("1425.00"))
                .cuenta(cuenta)
                .build();

        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);

        MovimientoResponseDTO result = movimientoService.registrar(dto);

        assertThat(result).isNotNull();
        assertThat(result.getTipoMovimiento()).isEqualTo("Retiro");
        assertThat(result.getSaldo()).isEqualByComparingTo(new BigDecimal("1425.00"));
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void registrar_DebeLanzarSaldoInsuficiente_CuandoSaldoEsInsuficiente() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO("478758", new BigDecimal("-9999.00"));

        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> movimientoService.registrar(dto))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessage("Saldo no disponible");
    }

    @Test
    void registrar_DebeLanzarExcepcion_CuandoCuentaNoExiste() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO("000000", new BigDecimal("100.00"));

        when(cuentaRepository.findByNumeroCuenta("000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.registrar(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("000000");
    }

    @Test
    void registrar_DebeRegistrarDeposito_CuandoValorEsPositivo() {
        MovimientoRequestDTO dto = new MovimientoRequestDTO("478758", new BigDecimal("600.00"));

        Movimiento movimientoGuardado = Movimiento.builder()
                .id(2L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento("Deposito")
                .valor(new BigDecimal("600.00"))
                .saldo(new BigDecimal("2600.00"))
                .cuenta(cuenta)
                .build();

        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(movimientoGuardado);

        MovimientoResponseDTO result = movimientoService.registrar(dto);

        assertThat(result.getTipoMovimiento()).isEqualTo("Deposito");
        assertThat(result.getSaldo()).isEqualByComparingTo(new BigDecimal("2600.00"));
    }
}
