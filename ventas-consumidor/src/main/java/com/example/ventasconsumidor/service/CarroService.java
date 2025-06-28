package com.example.ventasconsumidor.service;

import java.util.List;
import java.util.Optional;

import com.example.ventasconsumidor.model.Carro;

public interface CarroService {

    public List<Carro> getAllCarros();
    public Optional<Carro> getCarroById(Long id);
    public Carro createCarro(Carro carro);
    public Carro updateCarro(Long id, Carro carro);
    void deleteCarro(Long id);

    public List<Carro> getCarroByUsuarioId(String usuarioId);
    public void sendCarroToQueue(Carro carro);

}
