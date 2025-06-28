package com.example.bff.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    private final WebClient webClient;

    public ProductoController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public Mono<List<JsonNode>> getAllProductos() {
        return webClient.get()
                .uri("/producto")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonNode>>() {});
    }

    @GetMapping("/{id}")
    public Mono<JsonNode> getProducto(@PathVariable String id) {
        return webClient.get()
                .uri("/producto/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<JsonNode> createProducto(@RequestBody JsonNode producto) {
        return webClient.post()
                .uri("/producto")
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @PutMapping("/{id}")
    public Mono<JsonNode> updateProducto(@PathVariable String id, @RequestBody JsonNode producto) {
        return webClient.put()
                .uri("/producto/{id}", id)
                .bodyValue(producto)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProducto(@PathVariable String id) {
        return webClient.delete()
                .uri("/producto/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
} 