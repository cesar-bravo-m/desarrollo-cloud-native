package com.grupo11.cloud_ventas_producer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo11.cloud_ventas_producer.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

}