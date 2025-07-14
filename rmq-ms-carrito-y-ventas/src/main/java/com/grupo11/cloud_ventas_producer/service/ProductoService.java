package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.cloud_ventas_producer.model.Producto;

public interface ProductoService {

    List<Producto> getAllProductos();
    Optional<Producto> getProductoById(Long id);
    Optional<Producto> getProductoBySku(String sku);
    Producto createProducto(Producto producto);
    Producto updateProducto(Long id, Producto producto);
    void deleteProducto(Long id);
    
    List<Producto> searchProductosByNombre(String nombre);
    List<Producto> getProductosInStock();
    List<Producto> getProductosWithMinStock(Long minStock);
    
    boolean updateStock(Long productoId, Long cantidad);
    boolean reduceStock(Long productoId, Long cantidad);

} 