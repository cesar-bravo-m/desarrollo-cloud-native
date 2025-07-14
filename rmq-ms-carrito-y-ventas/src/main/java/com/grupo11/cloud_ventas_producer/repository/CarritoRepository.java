package com.grupo11.cloud_ventas_producer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo11.cloud_ventas_producer.model.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    public List<Carrito> getCarritoByUsuarioId(String usuarioId);

}
