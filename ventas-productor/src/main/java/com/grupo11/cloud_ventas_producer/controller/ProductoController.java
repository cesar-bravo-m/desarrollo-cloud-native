package com.grupo11.cloud_ventas_producer.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.grupo11.cloud_ventas_producer.model.Producto;
import com.grupo11.cloud_ventas_producer.service.ProductoService;

@RestController
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/producto")
    public List<Producto> getProductosAll() {
        return productoService.getAllProductos();
    }
    
    @GetMapping("/producto/{id}")
    public Optional<Producto> getProducto(@PathVariable Long id) {
        return productoService.getProductoById(id);
    }

    @PostMapping("/producto")
    public Producto sendMessageCarro(@RequestBody Producto producto) {
        productoService.sendProductoToQueue(producto);
        return producto;
    }

}
