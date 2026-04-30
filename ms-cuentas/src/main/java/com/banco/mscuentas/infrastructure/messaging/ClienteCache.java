package com.banco.mscuentas.infrastructure.messaging;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache en memoria para almacenar datos de clientes recibidos via eventos RabbitMQ.
 * Permite al ms-cuentas conocer el nombre del cliente sin llamadas síncronas.
 */
@Component
public class ClienteCache {

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public void registrar(String clienteId, String nombre) {
        cache.put(clienteId, nombre);
    }

    public void eliminar(String clienteId) {
        cache.remove(clienteId);
    }

    public String obtenerNombre(String clienteId) {
        return cache.getOrDefault(clienteId, clienteId);
    }
}
