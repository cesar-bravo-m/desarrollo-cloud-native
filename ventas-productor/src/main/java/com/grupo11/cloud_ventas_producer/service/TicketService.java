package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.cloud_ventas_producer.model.Ticket;

public interface TicketService {

    public List<Ticket> getAllTickets();
    public Optional<Ticket> getTicketById(Long id);
    public Ticket createTicket(Ticket ticket);
    public Ticket updateTicket(Long id, Ticket ticket);
    void deleteTicket(Long id);

} 
