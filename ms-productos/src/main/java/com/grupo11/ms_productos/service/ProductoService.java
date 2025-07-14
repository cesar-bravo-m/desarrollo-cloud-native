package com.grupo11.ms_productos.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.ms_productos.model.Producto;

public interface ProductoService {

    public List<Producto> getAllProductos();
    public Optional<Producto> getProductoById(Long id);
}
