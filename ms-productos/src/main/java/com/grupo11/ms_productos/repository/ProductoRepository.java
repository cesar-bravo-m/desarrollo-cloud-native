package com.grupo11.ms_productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grupo11.ms_productos.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    public Optional<Producto> getProductoByProductoId(Long id);
    
    @Query("SELECT p FROM Producto p WHERE p.productoId = :id")
    Optional<Producto> findProductoById(@Param("id") Long id);
    
    @Query(value = "SELECT producto_id, sku, nombre, descripcion, precio, cantidad_stock, creado_en, actualizado_en, imagen_uri FROM eft_producto WHERE producto_id = :id", nativeQuery = true)
    Optional<Producto> findProductoByIdNative(@Param("id") Long id);
    
    @Query(value = "SELECT producto_id, sku, nombre, descripcion, precio, cantidad_stock, creado_en, actualizado_en, imagen_uri FROM eft_producto", nativeQuery = true)
    List<Producto> findAllProductosNative();
}
