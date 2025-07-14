package com.grupo11.cloud_ventas_producer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo11.cloud_ventas_producer.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findBySku(String sku);
    
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT p FROM Producto p WHERE p.cantidadStock > 0")
    List<Producto> findProductosInStock();
    
    @Query("SELECT p FROM Producto p WHERE p.cantidadStock > :minStock")
    List<Producto> findProductosWithMinStock(@Param("minStock") Long minStock);

} 