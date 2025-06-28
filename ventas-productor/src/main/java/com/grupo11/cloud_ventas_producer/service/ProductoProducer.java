package com.grupo11.cloud_ventas_producer.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.config.RabbitMQConfig;

@Service
public class ProductoProducer {

    private final RabbitTemplate rabbitTemplate;

    public ProductoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PRODUCTS, message);
    }
} 