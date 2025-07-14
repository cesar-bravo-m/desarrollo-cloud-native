package com.grupo11.adminpromociones.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.grupo11.adminpromociones.model.Promocion;
import com.grupo11.adminpromociones.service.KafkaService;
import com.grupo11.adminpromociones.service.PromocionService;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    private static final Logger logger = LoggerFactory.getLogger(PromocionController.class);
    private final PromocionService promocionService;
    private final KafkaService kafkaService;
    private final RestTemplate restTemplate;

    public PromocionController(PromocionService promocionService, KafkaService kafkaService) {
        this.promocionService = promocionService;
        this.kafkaService = kafkaService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public ResponseEntity<List<Promocion>> getAllPromociones() {
        List<Promocion> promociones = promocionService.getAllPromociones();
        return ResponseEntity.ok(promociones);
    }

    @PostMapping
    public ResponseEntity<Promocion> createPromocion(@RequestBody Promocion promocion) {
        Promocion savedPromocion = promocionService.savePromocion(promocion);

        kafkaService.manuallyReadStockTopic();
        
        try {
            String externalUrl = "http://localhost:8085/api/promociones";
            logger.info("Llamando: {}", externalUrl);
            
            ResponseEntity<Promocion> externalResponse = restTemplate.postForEntity(
                externalUrl, 
                promocion, 
                Promocion.class
            );
            
            logger.info("Servicio retornó estado: {}", externalResponse.getStatusCode());
            if (externalResponse.getBody() != null) {
                logger.info("Servicio localhost:8085 retornó: {}", externalResponse.getBody());
            }
        } catch (Exception e) {
            logger.error("Fallo al llamar localhost:8085: {}", e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedPromocion);
    }
} 