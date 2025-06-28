package com.example.ventasconsumidor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ventasconsumidor.model.Carro;

public interface CarroRepository extends JpaRepository<Carro, Long> {

    public List<Carro> getCarroByUsuarioId(String usuarioId);

}
