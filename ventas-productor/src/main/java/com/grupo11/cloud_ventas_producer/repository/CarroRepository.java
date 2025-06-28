package com.grupo11.cloud_ventas_producer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo11.cloud_ventas_producer.model.Carro;

public interface CarroRepository extends JpaRepository<Carro, Long> {

    public List<Carro> getCarroByUsuarioId(String usuarioId);

}
