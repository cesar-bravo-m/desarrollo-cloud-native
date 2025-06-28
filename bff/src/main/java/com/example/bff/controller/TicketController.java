package com.example.bff.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final WebClient webClient;

    public TicketController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/gen")
    public Mono<JsonNode> generateTicket() {
        return webClient.get()
                .uri("/ticket/gen")
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
} 