package com.example.adminventas.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.adminventas.dto.VentaMessage;
import com.example.adminventas.service.KafkaProducerService;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentasController {
    
    private static final Logger logger = LoggerFactory.getLogger(VentasController.class);
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @PostMapping("/informarVenta")
    public ResponseEntity<Map<String, Object>> informarVenta(@RequestBody VentaMessage ventaMessage) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (ventaMessage.getCarritoId() == null || ventaMessage.getUserId() == null || 
                ventaMessage.getUserId().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "carritoId and userId are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            logger.info("Received venta message: {}", ventaMessage);
            
            kafkaProducerService.sendVentaMessage(ventaMessage);
            
            response.put("success", true);
            response.put("message", "Venta message sent successfully");
            response.put("data", ventaMessage);
            
            logger.info("Venta message processed successfully for carritoId: {}, userId: {}", 
                ventaMessage.getCarritoId(), ventaMessage.getUserId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing venta message: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "Error processing venta message: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "adminventas");
        return ResponseEntity.ok(response);
    }
} 