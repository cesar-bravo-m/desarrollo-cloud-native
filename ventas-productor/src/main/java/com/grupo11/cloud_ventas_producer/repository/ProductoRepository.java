package com.grupo11.cloud_ventas_producer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo11.cloud_ventas_producer.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    public Optional<Producto> getProductoByProductoId(Long id);

}
