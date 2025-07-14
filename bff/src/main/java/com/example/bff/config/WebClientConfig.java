package com.example.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${microservices.productos.base-url}")
    private String productosBaseUrl;
    
    @Value("${microservices.ventas.base-url}")
    private String ventasBaseUrl;
    
    @Value("${microservices.notifications.base-url}")
    private String notificationsBaseUrl;
    
    @Value("${microservices.promociones.base-url}")
    private String promocionesBaseUrl;
    
    @Bean("productosWebClient")
    public WebClient productosWebClient() {
        return WebClient.builder()
                .baseUrl(productosBaseUrl) // Microservicio de Productos
                .build();
    }
    
    @Bean("ventasWebClient")
    public WebClient ventasWebClient() {
        return WebClient.builder()
                .baseUrl(ventasBaseUrl) // Microservicio de Carrito y Ventas
                .build();
    }
    
    @Bean("notificationWebClient")
    public WebClient notificationWebClient() {
        return WebClient.builder()
                .baseUrl(notificationsBaseUrl) // Servicio de Notificaciones
                .build();
    }
    
    @Bean("promocionesWebClient")
    public WebClient promocionesWebClient() {
        return WebClient.builder()
                .baseUrl(promocionesBaseUrl) // Servicio de Promociones
                .build();
    }
} 