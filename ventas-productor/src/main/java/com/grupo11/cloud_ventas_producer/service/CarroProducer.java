package com.grupo11.cloud_ventas_producer.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.config.RabbitMQConfig;

@Service
public class CarroProducer {

    private final RabbitTemplate rabbitTemplate;

    public CarroProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PRODUCTS, message);
    }
} 