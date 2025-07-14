package com.example.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class BoletaController {
    
    private final WebClient ventasWebClient;
    
    public BoletaController(@Qualifier("ventasWebClient") WebClient ventasWebClient) {
        this.ventasWebClient = ventasWebClient;
    }
    
    @GetMapping(value = "boleta", produces = MediaType.APPLICATION_PDF_VALUE)
    public Mono<ResponseEntity<byte[]>> getBoleta(@RequestParam Long carritoId) {
        System.out.println("### carritoId: " + carritoId);
        return ventasWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ventas/boleta")
                        .queryParam("carritoId", carritoId)
                        .build())
                .retrieve()
                .bodyToMono(byte[].class)
                .map(pdfBytes -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=boleta_" + carritoId + ".pdf");
                    headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                    headers.add(HttpHeaders.PRAGMA, "no-cache");
                    headers.add(HttpHeaders.EXPIRES, "0");
                    headers.setContentLength(pdfBytes.length);
                    
                    System.out.println("### PDF bytes length: " + pdfBytes.length);
                    
                    return ResponseEntity
                            .ok()
                            .headers(headers)
                            .body(pdfBytes);
                })
                .onErrorResume(error -> {
                    System.out.println("### Error occurred: " + error.getMessage());
                    error.printStackTrace();
                    return Mono.just(ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .<byte[]>build());
                });
    }
} 