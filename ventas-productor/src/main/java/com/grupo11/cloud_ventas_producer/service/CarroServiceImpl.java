package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.config.RabbitMQConfig;
import com.grupo11.cloud_ventas_producer.model.Carro;
import com.grupo11.cloud_ventas_producer.repository.CarroRepository;

@Service
public class CarroServiceImpl implements CarroService {

    @Autowired
    private CarroRepository carroRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Carro> getAllCarros() {
        return carroRepository.findAll();
    }

    @Override
    public Optional<Carro> getCarroById(Long id) {
        return carroRepository.findById(id);
    }

    @Override
    public Carro createCarro(Carro carro) {
        return carroRepository.save(carro);
    }
    
    @Override
    public Carro updateCarro(Long id, Carro carro) {
        if (carroRepository.existsById(id)) {
            carro.setCarroId(id);
            return carroRepository.save(carro);
        } else {
            return null;
        }
    }

    @Override
    public void deleteCarro(Long id) {
        carroRepository.deleteById(id);
    }

    @Override
    public List<Carro> getCarroByUsuarioId(String usuarioId) {
        return carroRepository.getCarroByUsuarioId(usuarioId);
    }

    @Override
    public void sendCarroToQueue(Carro carro) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_SALES, carro);
    }

}
