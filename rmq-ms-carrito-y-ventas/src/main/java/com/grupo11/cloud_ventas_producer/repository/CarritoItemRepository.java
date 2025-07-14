package com.grupo11.cloud_ventas_producer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo11.cloud_ventas_producer.model.CarritoItem;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByCarritoId(Long carritoId);
    
    Optional<CarritoItem> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
    
    @Query("SELECT ci FROM CarritoItem ci WHERE ci.carritoId = :carritoId")
    List<CarritoItem> findItemsByCarritoId(@Param("carritoId") Long carritoId);
    
    @Query("SELECT SUM(ci.cantidad * ci.precioUnitario) FROM CarritoItem ci WHERE ci.carritoId = :carritoId")
    Long calculateTotalByCarritoId(@Param("carritoId") Long carritoId);
    
    void deleteByCarritoId(Long carritoId);

} 