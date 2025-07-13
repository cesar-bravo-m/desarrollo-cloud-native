package com.example.ventasconsumidor.service;

import java.util.List;
import java.util.Optional;

import com.example.ventasconsumidor.model.Carrito;

public interface CarritoService {

    public List<Carrito> getAllCarritos();
    public Optional<Carrito> getCarritoById(Long id);
    public Carrito createCarrito(Carrito carrito);
    public Carrito updateCarrito(Long id, Carrito carrito);
    void deleteCarrito(Long id);

    public List<Carrito> getCarritoByUsuarioId(String usuarioId);
}
