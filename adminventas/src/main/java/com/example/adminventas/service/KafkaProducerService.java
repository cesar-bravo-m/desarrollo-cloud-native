package com.example.adminventas.service;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.adminventas.dto.VentaMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String VENTAS_TOPIC = "ventas";
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private KafkaAdmin kafkaAdmin;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public void sendVentaMessage(VentaMessage ventaMessage) {
        try {
            // Ensure topic exists
            createTopicIfNotExists();
            
            // Convert message to JSON
            String messageJson = objectMapper.writeValueAsString(ventaMessage);
            
            // Send message
            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(VENTAS_TOPIC, messageJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("Sent message=[{}] with offset=[{}]", 
                        messageJson, result.getRecordMetadata().offset());
                } else {
                    logger.error("Unable to send message=[{}] due to : {}", 
                        messageJson, ex.getMessage());
                }
            });
            
        } catch (JsonProcessingException e) {
            logger.error("Error converting message to JSON: {}", e.getMessage());
            throw new RuntimeException("Error processing venta message", e);
        }
    }
    
    private void createTopicIfNotExists() {
        try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            NewTopic newTopic = new NewTopic(VENTAS_TOPIC, 3, (short) 3);
            CreateTopicsResult result = adminClient.createTopics(Collections.singletonList(newTopic));
            result.all().get();
            logger.info("Topic '{}' created successfully", VENTAS_TOPIC);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                logger.debug("Topic '{}' already exists", VENTAS_TOPIC);
            } else {
                logger.error("Error creating topic '{}': {}", VENTAS_TOPIC, e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while creating topic '{}': {}", VENTAS_TOPIC, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error creating topic '{}': {}", VENTAS_TOPIC, e.getMessage());
        }
    }
} 