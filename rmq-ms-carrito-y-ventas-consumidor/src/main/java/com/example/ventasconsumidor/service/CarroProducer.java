package com.example.ventasconsumidor.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class CarroProducer {

    private final RabbitTemplate rabbitTemplate;

    public CarroProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
} 