package com.example.ventasconsumidor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ventasconsumidor.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
} 