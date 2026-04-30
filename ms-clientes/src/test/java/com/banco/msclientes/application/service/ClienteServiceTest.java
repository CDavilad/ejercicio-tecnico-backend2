package com.banco.msclientes.application.service;

import com.banco.msclientes.application.dto.ClienteRequestDTO;
import com.banco.msclientes.application.dto.ClienteResponseDTO;
import com.banco.msclientes.application.service.impl.ClienteServiceImpl;
import com.banco.msclientes.domain.entity.Cliente;
import com.banco.msclientes.domain.repository.ClienteRepository;
import com.banco.msclientes.infrastructure.exception.BusinessException;
import com.banco.msclientes.infrastructure.exception.ResourceNotFoundException;
import com.banco.msclientes.infrastructure.messaging.ClienteEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteEventPublisher eventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private ClienteRequestDTO requestDTO;
    private Cliente clienteEntity;

    @BeforeEach
    void setUp() {
        requestDTO = new ClienteRequestDTO(
                "Jose Lema", "Masculino", 30,
                "1234567890", "Otavalo sn y principal", "098254785",
                "jose.lema", "1234", true
        );

        clienteEntity = new Cliente();
        clienteEntity.setId(1L);
        clienteEntity.setNombre("Jose Lema");
        clienteEntity.setGenero("Masculino");
        clienteEntity.setEdad(30);
        clienteEntity.setIdentificacion("1234567890");
        clienteEntity.setDireccion("Otavalo sn y principal");
        clienteEntity.setTelefono("098254785");
        clienteEntity.setClienteId("jose.lema");
        clienteEntity.setContrasena("1234");
        clienteEntity.setEstado(true);
    }

    @Test
    void crear_DebeRetornarClienteCreado_CuandoDatosValidos() {
        when(clienteRepository.existsByClienteId(anyString())).thenReturn(false);
        when(clienteRepository.existsByIdentificacion(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteEntity);
        doNothing().when(eventPublisher).publicarClienteCreado(anyString(), anyString());

        ClienteResponseDTO result = clienteService.crear(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Jose Lema");
        assertThat(result.getClienteId()).isEqualTo("jose.lema");
        assertThat(result.getEstado()).isTrue();
        verify(clienteRepository, times(1)).save(any(Cliente.class));
        verify(eventPublisher, times(1)).publicarClienteCreado(anyString(), anyString());
    }

    @Test
    void crear_DebeLanzarExcepcion_CuandoClienteIdYaExiste() {
        when(clienteRepository.existsByClienteId("jose.lema")).thenReturn(true);

        assertThatThrownBy(() -> clienteService.crear(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("jose.lema");

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_DebeRetornarCliente_CuandoExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));

        ClienteResponseDTO result = clienteService.obtenerPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    void obtenerPorId_DebeLanzarExcepcion_CuandoNoExiste() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.obtenerPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void listarTodos_DebeRetornarListaDeClientes() {
        when(clienteRepository.findAll()).thenReturn(List.of(clienteEntity));

        List<ClienteResponseDTO> result = clienteService.listarTodos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    void eliminar_DebeEliminarCliente_CuandoExiste() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteEntity));
        doNothing().when(clienteRepository).delete(clienteEntity);
        doNothing().when(eventPublisher).publicarClienteEliminado(anyString());

        assertThatCode(() -> clienteService.eliminar(1L)).doesNotThrowAnyException();
        verify(clienteRepository, times(1)).delete(clienteEntity);
        verify(eventPublisher, times(1)).publicarClienteEliminado("jose.lema");
    }
}
