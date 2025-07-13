package com.example.ventasconsumidor.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ventasconsumidor.model.Producto;
import com.example.ventasconsumidor.repository.ProductoRepository;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Optional<Producto> getProductoBySku(String sku) {
        return productoRepository.findBySku(sku);
    }

    @Override
    public Producto createProducto(Producto producto) {
        producto.setCreadoEn(OffsetDateTime.now());
        return productoRepository.save(producto);
    }

    @Override
    public Producto updateProducto(Long id, Producto producto) {
        if (productoRepository.existsById(id)) {
            producto.setProductoId(id);
            producto.setActualizadoEn(OffsetDateTime.now());
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
    public List<Producto> searchProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Producto> getProductosInStock() {
        return productoRepository.findProductosInStock();
    }

    @Override
    public List<Producto> getProductosWithMinStock(Long minStock) {
        return productoRepository.findProductosWithMinStock(minStock);
    }

    @Override
    public boolean updateStock(Long productoId, Long cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            producto.setCantidadStock(cantidad);
            producto.setActualizadoEn(OffsetDateTime.now());
            productoRepository.save(producto);
            return true;
        }
        return false;
    }

    @Override
    public boolean reduceStock(Long productoId, Long cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            if (producto.getCantidadStock() >= cantidad) {
                producto.setCantidadStock(producto.getCantidadStock() - cantidad);
                producto.setActualizadoEn(OffsetDateTime.now());
                productoRepository.save(producto);
                return true;
            }
        }
        return false;
    }
} 