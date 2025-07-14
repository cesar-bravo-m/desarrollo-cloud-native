package com.grupo11.cloud_ventas_producer.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo11.cloud_ventas_producer.model.Carrito;
import com.grupo11.cloud_ventas_producer.model.CarritoItem;
import com.grupo11.cloud_ventas_producer.service.CarritoItemService;
import com.grupo11.cloud_ventas_producer.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;
    
    @Autowired
    private CarritoItemService carritoItemService;

    @GetMapping
    public ResponseEntity<List<Carrito>> getAllCarritos() {
        List<Carrito> carritos = carritoService.getAllCarritos();
        return ResponseEntity.ok(carritos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrito> getCarritoById(@PathVariable Long id) {
        Optional<Carrito> carrito = carritoService.getCarritoById(id);
        return carrito.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Carrito>> getCarritosByUsuario(@PathVariable String usuarioId) {
        List<Carrito> carritos = carritoService.getCarritoByUsuarioId(usuarioId);
        return ResponseEntity.ok(carritos);
    }

    @PostMapping
    public ResponseEntity<Carrito> createCarrito(@RequestBody Carrito carrito) {
        carrito.setEstado("A"); // Activo
        carrito.setCreadoEn(OffsetDateTime.now());
        Carrito newCarrito = carritoService.createCarrito(carrito);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCarrito);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Carrito> updateCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        carrito.setActualizadoEn(OffsetDateTime.now());
        Carrito updatedCarrito = carritoService.updateCarrito(id, carrito);
        if (updatedCarrito != null) {
            return ResponseEntity.ok(updatedCarrito);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarrito(@PathVariable Long id) {
        carritoService.deleteCarrito(id);
        return ResponseEntity.noContent().build();
    }

    // Cart Items Management
    @GetMapping("/{carritoId}/items")
    public ResponseEntity<List<CarritoItem>> getCarritoItems(@PathVariable Long carritoId) {
        List<CarritoItem> items = carritoItemService.getItemsByCarritoId(carritoId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/{carritoId}/items")
    public ResponseEntity<CarritoItem> addItemToCarrito(
            @PathVariable Long carritoId,
            @RequestParam Long productoId,
            @RequestParam Long cantidad) {
        
        CarritoItem item = carritoItemService.addItemToCarrito(carritoId, productoId, cantidad);
        if (item != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(item);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{carritoId}/items/{productoId}")
    public ResponseEntity<Void> updateItemQuantity(
            @PathVariable Long carritoId,
            @PathVariable Long productoId,
            @RequestParam Long cantidad) {
        
        boolean updated = carritoItemService.updateItemQuantity(carritoId, productoId, cantidad);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{carritoId}/items/{productoId}")
    public ResponseEntity<Void> removeItemFromCarrito(
            @PathVariable Long carritoId,
            @PathVariable Long productoId) {
        
        boolean removed = carritoItemService.removeItemFromCarrito(carritoId, productoId);
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Transactional
    @DeleteMapping("/{carritoId}/items")
    public ResponseEntity<Void> clearCarrito(@PathVariable Long carritoId) {
        carritoItemService.clearCarrito(carritoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{carritoId}/total")
    public ResponseEntity<Long> getCarritoTotal(@PathVariable Long carritoId) {
        Long total = carritoItemService.calculateTotalByCarritoId(carritoId);
        return ResponseEntity.ok(total != null ? total : 0L);
    }

    @PutMapping("/{carritoId}/abandon")
    public ResponseEntity<Void> abandonCarrito(@PathVariable Long carritoId) {
        Optional<Carrito> carritoOpt = carritoService.getCarritoById(carritoId);
        if (carritoOpt.isPresent()) {
            Carrito carrito = carritoOpt.get();
            carrito.setEstado("X"); // Abandoned
            carrito.setActualizadoEn(OffsetDateTime.now());
            carritoService.updateCarrito(carritoId, carrito);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 