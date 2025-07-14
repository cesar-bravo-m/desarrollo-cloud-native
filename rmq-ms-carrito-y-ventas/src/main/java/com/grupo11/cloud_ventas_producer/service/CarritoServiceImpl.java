package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.config.RabbitMQConfig;
import com.grupo11.cloud_ventas_producer.model.Carrito;
import com.grupo11.cloud_ventas_producer.repository.CarritoRepository;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
        System.out.println("carrito.getCarritoId(): " + carrito.getCarritoId());
        System.out.println("carrito.getUsuarioId(): " + carrito.getUsuarioId());
        System.out.println("carrito.getEstado(): " + carrito.getEstado());
        System.out.println("carrito.getCreadoEn(): " + carrito.getCreadoEn());
        System.out.println("carrito.getActualizadoEn(): " + carrito.getActualizadoEn());
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

    @Override
    public void sendCarritoToQueue(Carrito carrito) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_SALES, carrito);
    }

}
