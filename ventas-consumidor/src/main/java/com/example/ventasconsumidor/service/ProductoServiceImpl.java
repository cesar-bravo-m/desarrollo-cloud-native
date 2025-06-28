package com.example.ventasconsumidor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ventasconsumidor.config.RabbitMQConfig;
import com.example.ventasconsumidor.model.Producto;
import com.example.ventasconsumidor.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto createProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto updateProducto(Long id, Producto producto) {
        if (productoRepository.existsById(id)) {
            producto.setProductoId(id);
            return productoRepository.save(producto);
        }
        throw new RuntimeException("Producto no encontrado con ID: " + id);
    }

    @Override
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public void sendProductoToQueue(Producto producto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PRODUCTS, producto);
    }
} 