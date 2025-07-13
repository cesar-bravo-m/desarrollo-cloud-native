package com.example.ventasconsumidor.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ventasconsumidor.model.Carrito;
import com.example.ventasconsumidor.repository.CarritoRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public List<Carrito> getAllCarritos() {
        return carritoRepository.findAll();
    }

    @Override
    public Optional<Carrito> getCarritoById(Long id) {
        return carritoRepository.findById(id);
    }

    @Override
    public Carrito createCarrito(Carrito carrito) {
        return carritoRepository.save(carrito);
    }
    
    @Override
    public Carrito updateCarrito(Long id, Carrito carrito) {
        if (carritoRepository.existsById(id)) {
            carrito.setCarritoId(id);
            return carritoRepository.save(carrito);
        } else {
            return null;
        }
    }

    @Override
    public void deleteCarrito(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public List<Carrito> getCarritoByUsuarioId(String usuarioId) {
        return carritoRepository.getCarritoByUsuarioId(usuarioId);
    }

}
