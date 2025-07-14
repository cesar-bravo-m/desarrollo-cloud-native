package com.grupo11.adminstock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo11.adminstock.dto.VentaMessage;

@Service
public class VentasKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(VentasKafkaListener.class);

    private final StockService stockService;
    private final ObjectMapper objectMapper;

    public VentasKafkaListener(StockService stockService) {
        this.stockService = stockService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "${app.kafka.topics.ventas}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(@Payload String message,
                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                      @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                      @Header(KafkaHeaders.OFFSET) long offset,
                      Acknowledgment acknowledgment) {
        
        logger.info("Received message from topic '{}', partition {}, offset {}: {}", 
                   topic, partition, offset, message);

        try {
            // Deserialize the VentaMessage
            VentaMessage ventaMessage = objectMapper.readValue(message, VentaMessage.class);
            logger.info("Parsed VentaMessage: carritoId={}, userId={}", 
                       ventaMessage.getCarritoId(), ventaMessage.getUserId());

            // Process the venta to update stock
            stockService.processVenta(ventaMessage.getCarritoId(), ventaMessage.getUserId());

            // Acknowledge the message after successful processing
            acknowledgment.acknowledge();
            logger.info("Successfully processed venta message for carrito: {}", ventaMessage.getCarritoId());

        } catch (JsonProcessingException e) {
            logger.error("Error deserializing venta message: {}", e.getMessage(), e);
            // Don't acknowledge on parsing error - this will retry
        } catch (Exception e) {
            logger.error("Error processing venta message: {}", e.getMessage(), e);
            // Don't acknowledge on processing error - this will retry
        }
    }
} 