package com.banco.mscuentas.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteEventConsumer {

    private final ClienteCache clienteCache;

    @RabbitListener(queues = "${rabbitmq.queue.clientes}")
    public void onClienteEvent(ClienteEvent event) {
        log.info("Evento recibido: tipo={}, clienteId={}", event.getTipoEvento(), event.getClienteId());
        switch (event.getTipoEvento()) {
            case "CREADO" -> clienteCache.registrar(event.getClienteId(), event.getNombre());
            case "ELIMINADO" -> clienteCache.eliminar(event.getClienteId());
            default -> log.warn("Tipo de evento desconocido: {}", event.getTipoEvento());
        }
    }
}
