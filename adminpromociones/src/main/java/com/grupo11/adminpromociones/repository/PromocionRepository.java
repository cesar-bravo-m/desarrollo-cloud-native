package com.grupo11.adminpromociones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo11.adminpromociones.model.Promocion;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
} 