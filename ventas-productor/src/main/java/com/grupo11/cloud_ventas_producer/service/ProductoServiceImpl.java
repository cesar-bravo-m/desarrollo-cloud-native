package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.config.RabbitMQConfig;
import com.grupo11.cloud_ventas_producer.model.Producto;
import com.grupo11.cloud_ventas_producer.repository.ProductoRepository;

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
        return productoRepository.getProductoByProductoId(id);
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
        } else {
            return null;
        }
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