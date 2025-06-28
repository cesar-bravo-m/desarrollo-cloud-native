package com.example.bff.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/carro")
public class CarroController {

    private final WebClient webClient;

    public CarroController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public Mono<List<JsonNode>> getAllCarros() {
        return webClient.get()
                .uri("/carro")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonNode>>() {});
    }

    @GetMapping("/{id}")
    public Mono<List<JsonNode>> getCarro(@PathVariable String id) {
        return webClient.get()
                .uri("/carro/{id}", id)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonNode>>() {});
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<JsonNode> createCarro(@RequestBody JsonNode carro) {
        return webClient.post()
                .uri("/carro")
                .bodyValue(carro)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @PutMapping("/{id}")
    public Mono<JsonNode> updateCarro(@PathVariable String id, @RequestBody JsonNode carro) {
        return webClient.put()
                .uri("/carro/{id}", id)
                .bodyValue(carro)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCarro(@PathVariable String id) {
        return webClient.delete()
                .uri("/carro/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
} 