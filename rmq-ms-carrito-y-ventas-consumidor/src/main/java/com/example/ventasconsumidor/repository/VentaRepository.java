package com.example.ventasconsumidor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ventasconsumidor.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUsuarioId(String usuarioId);
    
    Optional<Venta> findByCarritoId(Long carritoId);
    
    @Query("SELECT v FROM Venta v WHERE v.usuarioId = :usuarioId ORDER BY v.creadoEn DESC")
    List<Venta> findVentasByUsuarioIdOrderByFecha(@Param("usuarioId") String usuarioId);
    
    @Query("SELECT SUM(v.montoTotal) FROM Venta v WHERE v.usuarioId = :usuarioId")
    Long getTotalVentasByUsuario(@Param("usuarioId") String usuarioId);

} 