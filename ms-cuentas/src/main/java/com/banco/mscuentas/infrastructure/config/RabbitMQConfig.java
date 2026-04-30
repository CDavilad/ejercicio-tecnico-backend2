package com.banco.mscuentas.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.clientes}")
    private String clientesQueue;

    @Value("${rabbitmq.routing-key.cliente-creado}")
    private String routingKeyCreado;

    @Value("${rabbitmq.routing-key.cliente-eliminado}")
    private String routingKeyEliminado;

    @Bean
    public TopicExchange bancoExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue clientesQueue() {
        return QueueBuilder.durable(clientesQueue).build();
    }

    @Bean
    public Binding bindingCreado(Queue clientesQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(clientesQueue).to(bancoExchange).with(routingKeyCreado);
    }

    @Bean
    public Binding bindingEliminado(Queue clientesQueue, TopicExchange bancoExchange) {
        return BindingBuilder.bind(clientesQueue).to(bancoExchange).with(routingKeyEliminado);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
