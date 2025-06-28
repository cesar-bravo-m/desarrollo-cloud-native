package com.example.bff.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    private final WebClient webClient;

    public UsuarioController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping
    public Mono<List<JsonNode>> getAllUsuarios() {
        return webClient.get()
                .uri("/usuario")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<JsonNode>>() {});
    }

    @GetMapping("/{id}")
    public Mono<JsonNode> getUsuario(@PathVariable String id) {
        return webClient.get()
                .uri("/usuario/{id}", id)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<JsonNode> createUsuario(@RequestBody JsonNode usuario) {
        return webClient.post()
                .uri("/usuario")
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @PutMapping("/{id}")
    public Mono<JsonNode> updateUsuario(@PathVariable String id, @RequestBody JsonNode usuario) {
        return webClient.put()
                .uri("/usuario/{id}", id)
                .bodyValue(usuario)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUsuario(@PathVariable String id) {
        return webClient.delete()
                .uri("/usuario/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
} 