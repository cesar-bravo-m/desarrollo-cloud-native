package com.grupo11.adminpromociones.service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.stock}")
    private String stockTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStockMessage(String message) {
        logger.info("Sending stock message: {}", message);
        kafkaTemplate.send(stockTopic, message);
    }

    @KafkaListener(topics = "${app.kafka.topics.stock}", groupId = "${spring.kafka.consumer.group-id}")
    public void readStockMessage(String message) {
        logger.info("=== STOCK TOPIC MESSAGE (Auto Listener) ===");
        logger.info("Received from stock topic: {}", message);
        logger.info("==========================================");
    }

    public void manuallyReadStockTopic() {
        logger.info("=== MANUALLY READING STOCK TOPIC ===");
        
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-manual");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(stockTopic));
            
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            
            if (records.isEmpty()) {
                logger.info("No messages found in stock topic");
            } else {
                logger.info("Found {} messages in stock topic:", records.count());
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Stock message: {} (offset: {}, partition: {})", 
                              record.value(), record.offset(), record.partition());
                }
            }
        } catch (Exception e) {
            logger.error("Error reading from stock topic: {}", e.getMessage(), e);
        }
        
        logger.info("=== FINISHED READING STOCK TOPIC ===");
    }
} 