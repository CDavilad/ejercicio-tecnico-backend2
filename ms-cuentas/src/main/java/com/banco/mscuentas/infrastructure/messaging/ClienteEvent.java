package com.banco.mscuentas.infrastructure.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEvent implements Serializable {
    private String clienteId;
    private String nombre;
    private String tipoEvento;
}
