package com.example.ventasconsumidor.service;

import java.util.List;
import java.util.Optional;

import com.example.ventasconsumidor.model.Venta;

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
    
    Venta processCheckout(Long carritoId, String usuarioId);
    void sendVentaToQueue(Venta venta);
    String getSaleStatusByCarritoId(Long carritoId);

} 