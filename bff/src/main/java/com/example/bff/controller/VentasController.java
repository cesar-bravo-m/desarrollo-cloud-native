package com.example.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class VentasController {
    
    private final WebClient ventasWebClient;
    
    public VentasController(@Qualifier("ventasWebClient") WebClient ventasWebClient) {
        this.ventasWebClient = ventasWebClient;
    }
    
    @GetMapping("carritos")
    public Mono<ResponseEntity<Object>> getAllCarritos() {
        return ventasWebClient.get()
                .uri("/api/carritos")
                .retrieve()
                .toEntity(Object.class);
    }
    
    @PostMapping("carritos")
    public Mono<ResponseEntity<Object>> createCarrito(@RequestBody Object carrito) {
        return ventasWebClient.post()
                .uri("/api/carritos")
                .bodyValue(carrito)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("carritos/{id}")
    public Mono<ResponseEntity<Object>> getCarritoById(@PathVariable Long id) {
        return ventasWebClient.get()
                .uri("/api/carritos/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @PutMapping("carritos/{id}")
    public Mono<ResponseEntity<Object>> updateCarrito(@PathVariable Long id, @RequestBody Object carrito) {
        return ventasWebClient.put()
                .uri("/api/carritos/{id}", id)
                .bodyValue(carrito)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @DeleteMapping("carritos/{id}")
    public Mono<ResponseEntity<Object>> deleteCarrito(@PathVariable Long id) {
        return ventasWebClient.delete()
                .uri("/api/carritos/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("carritos/usuario/{usuarioId}")
    public Mono<ResponseEntity<Object>> getCarritosByUsuario(@PathVariable String usuarioId) {
        return ventasWebClient.get()
                .uri("/api/carritos/usuario/{usuarioId}", usuarioId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("carritos/{carritoId}/items")
    public Mono<ResponseEntity<Object>> getCarritoItems(@PathVariable Long carritoId) {
        return ventasWebClient.get()
                .uri("/api/carritos/{carritoId}/items", carritoId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @PostMapping("carritos/{carritoId}/items")
    public Mono<ResponseEntity<Object>> addItemToCarrito(@PathVariable Long carritoId, 
                                                        @RequestParam Long productoId, 
                                                        @RequestParam Long cantidad) {
        return ventasWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/carritos/{carritoId}/items")
                        .queryParam("productoId", productoId)
                        .queryParam("cantidad", cantidad)
                        .build(carritoId))
                .retrieve()
                .toEntity(Object.class);
    }
    
    // Quitar todos los productos del carrito
    @DeleteMapping("carritos/{carritoId}/items")
    public Mono<ResponseEntity<Object>> clearCarrito(@PathVariable Long carritoId) {
        return ventasWebClient.delete()
                .uri("/api/carritos/{carritoId}/items", carritoId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    // Actualizar la cantidad de un producto en el carrito
    @PutMapping("carritos/{carritoId}/items/{productoId}")
    public Mono<ResponseEntity<Object>> updateCarritoItemQuantity(@PathVariable Long carritoId, 
                                                                 @PathVariable Long productoId, 
                                                                 @RequestParam Long cantidad) {
        return ventasWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/carritos/{carritoId}/items/{productoId}")
                        .queryParam("cantidad", cantidad)
                        .build(carritoId, productoId))
                .retrieve()
                .toEntity(Object.class);
    }
    
    @DeleteMapping("carritos/{carritoId}/items/{productoId}")
    public Mono<ResponseEntity<Object>> removeItemFromCarrito(@PathVariable Long carritoId, 
                                                             @PathVariable Long productoId) {
        return ventasWebClient.delete()
                .uri("/api/carritos/{carritoId}/items/{productoId}", carritoId, productoId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("carritos/{carritoId}/total")
    public Mono<ResponseEntity<Object>> getCarritoTotal(@PathVariable Long carritoId) {
        return ventasWebClient.get()
                .uri("/api/carritos/{carritoId}/total", carritoId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @PutMapping("carritos/{carritoId}/abandon")
    public Mono<ResponseEntity<Object>> abandonCarrito(@PathVariable Long carritoId) {
        return ventasWebClient.put()
                .uri("/api/carritos/{carritoId}/abandon", carritoId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    // ===============================================
    // VENTAS ENDPOINTS
    // ===============================================
    
    @GetMapping("ventas")
    public Mono<ResponseEntity<Object>> getAllVentas() {
        return ventasWebClient.get()
                .uri("/api/ventas")
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("ventas/{id}")
    public Mono<ResponseEntity<Object>> getVentaById(@PathVariable Long id) {
        return ventasWebClient.get()
                .uri("/api/ventas/{id}", id)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("ventas/usuario/{usuarioId}")
    public Mono<ResponseEntity<Object>> getVentasByUsuario(@PathVariable String usuarioId) {
        return ventasWebClient.get()
                .uri("/api/ventas/usuario/{usuarioId}", usuarioId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @PostMapping("ventas/checkout")
    public Mono<ResponseEntity<Object>> checkout(@RequestParam Long carritoId, 
                                                @RequestParam String usuarioId) {
        return ventasWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ventas/checkout")
                        .queryParam("carritoId", carritoId)
                        .queryParam("usuarioId", usuarioId)
                        .build())
                .exchange()
                .flatMap(response -> response.toEntity(Object.class));
    }

    @GetMapping("ventas/carrito/{carritoId}/status")
    public Mono<ResponseEntity<Object>> getVentaStatus(@PathVariable Long carritoId) {
        return ventasWebClient.get()
                .uri("/api/ventas/carrito/{carritoId}/status", carritoId)
                .retrieve()
                .toEntity(Object.class);
    }
    
    @GetMapping("/health")
    public Mono<ResponseEntity<Object>> healthCheck() {
        return Mono.just(ResponseEntity.ok("OK"));
    }
} 