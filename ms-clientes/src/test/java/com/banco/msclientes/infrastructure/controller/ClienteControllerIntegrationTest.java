package com.banco.msclientes.infrastructure.controller;

import com.banco.msclientes.application.dto.ClienteRequestDTO;
import com.banco.msclientes.infrastructure.messaging.ClienteEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClienteEventPublisher eventPublisher;

    @Test
    void crearCliente_DebeRetornar201_CuandoDatosValidos() throws Exception {
        doNothing().when(eventPublisher).publicarClienteCreado(anyString(), anyString());

        ClienteRequestDTO dto = new ClienteRequestDTO(
                "Marianela Montalvo", "Femenino", 28,
                "0987654321", "Amazonas y NNUU", "097548965",
                "marianela.montalvo", "5678", true
        );

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Marianela Montalvo"))
                .andExpect(jsonPath("$.clienteId").value("marianela.montalvo"))
                .andExpect(jsonPath("$.estado").value(true));
    }

    @Test
    void crearCliente_DebeRetornar400_CuandoCamposObligatoriosFaltan() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO(
                "", null, null,
                "", null, null,
                "", "", null
        );

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarClientes_DebeRetornar200() throws Exception {
        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void obtenerClientePorId_DebeRetornar404_CuandoNoExiste() throws Exception {
        mockMvc.perform(get("/clientes/9999"))
                .andExpect(status().isNotFound());
    }
}
