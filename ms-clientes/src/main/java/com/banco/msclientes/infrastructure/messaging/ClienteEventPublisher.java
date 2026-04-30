package com.banco.msclientes.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.cliente-creado}")
    private String routingKeyCreado;

    @Value("${rabbitmq.routing-key.cliente-eliminado}")
    private String routingKeyEliminado;

    public void publicarClienteCreado(String clienteId, String nombre) {
        ClienteEvent event = new ClienteEvent(clienteId, nombre, "CREADO");
        rabbitTemplate.convertAndSend(exchange, routingKeyCreado, event);
    }

    public void publicarClienteEliminado(String clienteId) {
        ClienteEvent event = new ClienteEvent(clienteId, null, "ELIMINADO");
        rabbitTemplate.convertAndSend(exchange, routingKeyEliminado, event);
    }
}
