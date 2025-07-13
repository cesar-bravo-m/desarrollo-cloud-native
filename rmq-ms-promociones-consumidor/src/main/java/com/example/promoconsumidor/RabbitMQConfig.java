package com.example.promoconsumidor;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue promocionesQueue() {
        return new Queue("promociones", true);
    }
} 