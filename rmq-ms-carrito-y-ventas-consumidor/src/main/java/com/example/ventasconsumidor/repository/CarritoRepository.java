package com.example.ventasconsumidor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ventasconsumidor.model.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    public List<Carrito> getCarritoByUsuarioId(String usuarioId);

}
