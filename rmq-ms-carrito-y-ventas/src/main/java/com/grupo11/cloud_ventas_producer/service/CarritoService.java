package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.cloud_ventas_producer.model.Carrito;

public interface CarritoService {

    public List<Carrito> getAllCarritos();
    public Optional<Carrito> getCarritoById(Long id);
    public Carrito createCarrito(Carrito carrito);
    public Carrito updateCarrito(Long id, Carrito carrito);
    void deleteCarrito(Long id);

    public List<Carrito> getCarritoByUsuarioId(String usuarioId);
    public void sendCarritoToQueue(Carrito carrito);

}
