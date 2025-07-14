package com.grupo11.cloud_ventas_producer.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.grupo11.cloud_ventas_producer.model.Producto;
import com.grupo11.cloud_ventas_producer.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        List<Producto> productos = productoService.getAllProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.getProductoById(id);
        return producto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Producto> getProductoBySku(@PathVariable String sku) {
        Optional<Producto> producto = productoService.getProductoBySku(sku);
        return producto.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        Producto newProducto = productoService.createProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProducto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto updatedProducto = productoService.updateProducto(id, producto);
        if (updatedProducto != null) {
            return ResponseEntity.ok(updatedProducto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Producto>> searchProductos(@RequestParam String nombre) {
        List<Producto> productos = productoService.searchProductosByNombre(nombre);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<List<Producto>> getProductosInStock() {
        List<Producto> productos = productoService.getProductosInStock();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/min-stock")
    public ResponseEntity<List<Producto>> getProductosWithMinStock(@RequestParam Long minStock) {
        List<Producto> productos = productoService.getProductosWithMinStock(minStock);
        return ResponseEntity.ok(productos);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestParam Long cantidad) {
        boolean updated = productoService.updateStock(id, cantidad);
        if (updated) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<Void> reduceStock(@PathVariable Long id, @RequestParam Long cantidad) {
        boolean reduced = productoService.reduceStock(id, cantidad);
        if (reduced) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
} 