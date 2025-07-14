package com.grupo11.adminpromociones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo11.adminpromociones.model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
} 