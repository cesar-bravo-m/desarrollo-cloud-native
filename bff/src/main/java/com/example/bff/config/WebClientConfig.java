package com.example.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean("productosWebClient")
    public WebClient productosWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8083") // Microservicio de Productos
                .build();
    }
    
    @Bean("ventasWebClient")
    public WebClient ventasWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084") // Microservicio de Carrito y Ventas
                .build();
    }
    
    @Bean("notificationWebClient")
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8088") // Servicio de Notificaciones
                .build();
    }
} 