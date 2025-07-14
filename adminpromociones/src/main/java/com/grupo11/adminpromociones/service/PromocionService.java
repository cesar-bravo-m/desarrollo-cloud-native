package com.grupo11.adminpromociones.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo11.adminpromociones.model.Promocion;
import com.grupo11.adminpromociones.repository.PromocionRepository;

@Service
public class PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    public List<Promocion> getAllPromociones() {
        return promocionRepository.findAll();
    }

    public Promocion savePromocion(Promocion promocion) {
        return promocionRepository.save(promocion);
    }
} 