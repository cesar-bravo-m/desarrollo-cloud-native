package com.example.adminpromociones.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.adminpromociones.service.KafkaService;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaService kafkaService;

    public KafkaController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<String>> getVentasMessages() {
        List<String> messages = kafkaService.readVentasMessages();
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/ventas")
    public ResponseEntity<String> sendVentaMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }
        
        kafkaService.sendVentaMessage(message);
        return ResponseEntity.ok("Venta message sent successfully: " + message);
    }

    @PostMapping("/stock")
    public ResponseEntity<String> sendStockMessage(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message is required");
        }
        
        kafkaService.sendStockMessage(message);
        return ResponseEntity.ok("Stock message sent successfully: " + message);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Kafka service is running");
    }
} 