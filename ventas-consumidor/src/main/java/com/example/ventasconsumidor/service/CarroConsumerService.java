package com.example.ventasconsumidor.service;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.ventasconsumidor.config.RabbitMQConfig;
import com.example.ventasconsumidor.model.Carro;
import com.example.ventasconsumidor.model.Producto;

@Component
public class CarroConsumerService {

    @Autowired
    private CarroService carroService;
    @Autowired
    private ProductoService productoService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SALES)
    public void receiveMessage(Carro carro) {
        System.out.println("Recibiendo mensaje: ");
        System.out.println("Carro ID: " + carro.getCarroId());
        System.out.println("Ticket ID: " + carro.getTicketId());
        System.out.println("Producto ID: " + carro.getProductoId());
        System.out.println("Cantidad: " + carro.getCantidad());
        System.out.println("Usuario ID: " + carro.getUsuarioId());
        System.out.println("Registro Fecha: " + carro.getRegistroFecha());
        System.out.println("Vigencia Flag: " + carro.getVigenciaFlag());
        System.out.println("Ticket ID: " + carro.getTicketId());

        if (carro.getCarroId() == null
            || carro.getCarroId() == 0) {
            // Crear carro; No actualiza stock
            System.out.println("Creando carro: " + carro.toString());
            carroService.createCarro(carro);
        } else if (carro.getCarroId() != null
            && carro.getCarroId() > 0
            && carro.getTicketId() != null
            && carro.getTicketId() > 0) {
            // Actualizar carrito por venta
            // Primero actualizamos el TicketId del carro para demostrar que fue vendido
            System.out.println("Actualizando carro: " + carro.toString());
            Optional<Carro> existente = carroService.getCarroById(carro.getCarroId());
            if (existente.isPresent()) {
                Carro carroActualizado = existente.get();
                carroActualizado.setTicketId(carro.getTicketId());
                carroService.updateCarro(carro.getCarroId(), carroActualizado);
                Optional<Producto> productoItem = productoService.getProductoById(existente.get().getProductoId());
                if (productoItem.isPresent()) {
                    Producto producto = productoItem.get();
                    Long stock = producto.getStockActual() - existente.get().getCantidad();
                    producto.setStockActual(stock);
                    productoService.updateProducto(existente.get().getProductoId(), producto);
                } else {
                    System.out.println("Producto no encontrado para el ID: " + existente.get().getProductoId());
                }
            } else {
                System.out.println("Carro no encontrado para el ID: " + carro.getCarroId());
            }
        }
        System.out.println("Mensaje guardado: " + carro.toString() );
    }

}
