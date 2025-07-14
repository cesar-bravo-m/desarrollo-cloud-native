package com.grupo11.adminstock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo11.adminstock.dto.StockDto;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.stock}")
    private String stockTopic;

    @Value("${app.kafka.topics.ventas}")
    private String ventasTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void sendStockUpdate(StockDto stockDto) {
        try {
            String message = objectMapper.writeValueAsString(stockDto);
            kafkaTemplate.send(stockTopic, String.valueOf(stockDto.getProductoId()), message);
            logger.info("Sent stock update message to topic '{}' for product {}: {}", 
                       stockTopic, stockDto.getProductoId(), message);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing stock update message: {}", e.getMessage(), e);
        }
    }
} 