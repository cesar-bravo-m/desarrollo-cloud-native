package com.example.ventasconsumidor.service;

import java.util.List;
import java.util.Optional;

import com.example.ventasconsumidor.model.Producto;

public interface ProductoService {

    public List<Producto> getAllProductos();
    public Optional<Producto> getProductoById(Long id);
    public Producto createProducto(Producto producto);
    public Producto updateProducto(Long id, Producto producto);
    void deleteProducto(Long id);
    public void sendProductoToQueue(Producto producto);

}
