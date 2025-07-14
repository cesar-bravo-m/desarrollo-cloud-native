package com.example.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class PromocionController {
    
    private final WebClient promocionesWebClient;
    
    public PromocionController(@Qualifier("promocionesWebClient") WebClient promocionesWebClient) {
        this.promocionesWebClient = promocionesWebClient;
    }
    
    @GetMapping("promociones")
    public Mono<ResponseEntity<Object>> getPromociones() {
        return promocionesWebClient.get()
                .uri("/api/promociones")
                .retrieve()
                .toEntity(Object.class);
    }
    
    @PostMapping("promociones")
    public Mono<ResponseEntity<Object>> createPromocion() {
        return promocionesWebClient.post()
                .uri("/api/promociones")
                .retrieve()
                .toEntity(Object.class);
    }
} 