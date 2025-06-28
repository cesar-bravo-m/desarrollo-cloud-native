package com.grupo11.cloud_ventas_producer.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.grupo11.cloud_ventas_producer.model.Carro;
import com.grupo11.cloud_ventas_producer.model.Ticket;
import com.grupo11.cloud_ventas_producer.service.CarroService;
import com.grupo11.cloud_ventas_producer.service.TicketService;

@RestController
public class CarroController {

    @Autowired
    private CarroService carroService;
    @Autowired
    private TicketService ticketService;

    @GetMapping("/carro")
    public List<Carro> getCarroAll() {
        return carroService.getAllCarros();
    }

    @GetMapping("/carro/{id}")
    public List<Carro> getCarro(@PathVariable String id) {
        return carroService.getCarroByUsuarioId(id);
    }

    @PostMapping("/carro")
    public Carro sendMessageProducto(@RequestBody Carro carro) {
        System.out.println("Carro Ticket Id: " + carro.getTicketId());
        carroService.sendCarroToQueue(carro);
        return carro;
    }

    @PostMapping("/comprar/{idCarro}")
    public Carro comprar(@PathVariable Long idCarro) {
        Optional<Carro> carroOpt = carroService.getCarroById(idCarro);
        if (carroOpt.isPresent()) {
            Carro carro = carroOpt.get();
            
            // Generar ticket - let JPA handle the ID generation
            Ticket ticket = new Ticket();
            ticket.setRegistroFecha(new java.sql.Date(System.currentTimeMillis()));
            ticket.setTotal(0L); // Set a default total or calculate it
            Ticket savedTicket = ticketService.createTicket(ticket);
            
            // Update carro with the generated ticket ID
            carro.setTicketId(savedTicket.getTicketId());
            carro.setVigenciaFlag(0);
            
            // Save the updated carro
            Carro updatedCarro = carroService.updateCarro(idCarro, carro);
            
            // Send to queue
            carroService.sendCarroToQueue(updatedCarro);
            
            return updatedCarro;
        } else {
            return null;
        }
    }
}
