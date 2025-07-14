package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.cloud_ventas_producer.model.Venta;

public interface VentaService {

    List<Venta> getAllVentas();
    Optional<Venta> getVentaById(Long id);
    Venta createVenta(Venta venta);
    Venta updateVenta(Long id, Venta venta);
    void deleteVenta(Long id);
    
    List<Venta> getVentasByUsuarioId(String usuarioId);
    Optional<Venta> getVentaByCarritoId(Long carritoId);
    List<Venta> getVentasByUsuarioIdOrderedByDate(String usuarioId);
    Long getTotalVentasByUsuario(String usuarioId);
    
    void processCheckout(Long carritoId, String usuarioId);
    String getSaleStatusByCarritoId(Long carritoId);

} 