package com.example.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/producto")
public class ProductosController {
    
    private final WebClient productosWebClient;
    
    public ProductosController(@Qualifier("productosWebClient") WebClient productosWebClient) {
        this.productosWebClient = productosWebClient;
    }
    
    @GetMapping
    public Mono<ResponseEntity<Object>> getAllProductos() {
        return productosWebClient.get()
                .uri("/producto")
                .exchange()
                .flatMap(response -> response.toEntity(Object.class));
    }
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Object>> getProductoById(@PathVariable Long id) {
        return productosWebClient.get()
                .uri("/producto/{id}", id)
                .exchange()
                .flatMap(response -> response.toEntity(Object.class));
    }
} 