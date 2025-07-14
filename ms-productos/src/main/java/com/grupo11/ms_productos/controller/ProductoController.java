package com.grupo11.ms_productos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.grupo11.ms_productos.config.ExternalServicesConfig;
import com.grupo11.ms_productos.config.HostConfig;
import com.grupo11.ms_productos.model.Producto;
import com.grupo11.ms_productos.service.ProductoService;

@RestController
public class ProductoController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private HostConfig hostConfig;
    
    @Autowired
    private ExternalServicesConfig externalServicesConfig;

    @GetMapping("/producto")
    public List<Producto> getProductosAll() {
        return productoService.getAllProductos();
    }
    
    @GetMapping("/producto/{id}")
    public Optional<Producto> getProducto(@PathVariable Long id) {
        return productoService.getProductoById(id);
    }
    
    @GetMapping("/producto/debug/{id}")
    public String debugProducto(@PathVariable Long id) {
        Optional<Producto> producto = productoService.getProductoById(id);
        if (producto.isPresent()) {
            Producto p = producto.get();
            return String.format("Producto ID: %d, SKU: %s, Nombre: %s, Imagen URI: '%s'", 
                p.getProductoId(), p.getSku(), p.getNombre(), p.getImagenUri());
        }
        return "Producto no encontrado";
    }
    
    @GetMapping("/config/hosts")
    public Map<String, Object> getHostConfiguration() {
        Map<String, Object> config = new HashMap<>();
        
        Map<String, String> appHosts = new HashMap<>();
        appHosts.put("baseUrl", hostConfig.getBaseUrl());
        appHosts.put("apiGateway", hostConfig.getApiGateway());
        appHosts.put("frontend", hostConfig.getFrontend());
        
        Map<String, String> externalServices = new HashMap<>();
        externalServices.put("userService", externalServicesConfig.getUserService());
        externalServices.put("orderService", externalServicesConfig.getOrderService());
        externalServices.put("paymentService", externalServicesConfig.getPaymentService());
        
        config.put("applicationHosts", appHosts);
        config.put("externalServices", externalServices);
        
        return config;
    }
}
