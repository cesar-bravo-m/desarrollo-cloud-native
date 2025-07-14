package com.example.adminpromociones.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.ventas}")
    private String ventasTopic;

    @Value("${app.kafka.topics.stock}")
    private String stockTopic;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Producer methods
    public void sendVentaMessage(String message) {
        logger.info("Sending venta message: {}", message);
        kafkaTemplate.send(ventasTopic, message);
    }

    public void sendStockMessage(String message) {
        logger.info("Sending stock message: {}", message);
        kafkaTemplate.send(stockTopic, message);
    }

    // Consumer method to read messages from ventas topic
    public List<String> readVentasMessages() {
        List<String> messages = new ArrayList<>();
        
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "ventas-reader-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "60000");
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, "1000");
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, "10000");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(ventasTopic));
            
            // Poll for messages with a timeout
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
            
            for (ConsumerRecord<String, String> record : records) {
                messages.add(record.value());
                logger.info("Read venta message: {}", record.value());
            }
        } catch (Exception e) {
            logger.error("Error reading messages from ventas topic: {}", e.getMessage());
        }
        
        return messages;
    }
} 