package com.grupo11.adminstock.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grupo11.adminstock.model.CarritoItem;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    List<CarritoItem> findByCarritoId(Long carritoId);
} 