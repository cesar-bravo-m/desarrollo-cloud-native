package com.grupo11.adminstock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo11.adminstock.model.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
} 