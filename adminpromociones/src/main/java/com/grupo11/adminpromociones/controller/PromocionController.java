package com.grupo11.adminpromociones.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Value("${app.services.promociones.url}")
    private String promocionesServiceUrl;

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
    public ResponseEntity<Map<String, Object>> createPromocion() {
        logger.info("=== CREAR PROMOCIONES ENDPOINT CALLED ===");
        
        // Leer los topics de ventas y stock y crear una promocion
        Map<String, Object> result = kafkaService.readVentasAndStockTopicsAndCreatePromocion();
        
        // Obtener la promocion creada
        Promocion promocionCreated = (Promocion) result.get("promocionCreated");
        
        if (promocionCreated != null) {
            // Guardar la promocion en la base de datos local
            try {
                Promocion savedPromocion = promocionService.savePromocion(promocionCreated);
                logger.info("Promocion saved to database with ID: {}", savedPromocion.getId());
                
                // Actualizar el resultado con la promocion guardada (ahora tiene ID)
                result.put("promocionCreated", savedPromocion);
                result.put("savedToDatabase", true);
                
            } catch (Exception e) {
                logger.error("Error saving promocion to database: {}", e.getMessage());
                result.put("savedToDatabase", false);
                result.put("databaseError", e.getMessage());
            }
            
            // Llamar al servicio ms-promociones con la promoción creada
            try {
                logger.info("Llamando servicio externo: {}", promocionesServiceUrl);
                
                ResponseEntity<Promocion> externalResponse = restTemplate.postForEntity(
                    promocionesServiceUrl, 
                    promocionCreated, 
                    Promocion.class
                );
                
                logger.info("Servicio externo retornó estado: {}", externalResponse.getStatusCode());
                if (externalResponse.getBody() != null) {
                    logger.info("Servicio externo retornó: {}", externalResponse.getBody());
                }
                
                // Agregar la respuesta del servicio externo al resultado
                result.put("externalServiceResponse", externalResponse.getBody());
                result.put("externalServiceStatus", externalResponse.getStatusCode().toString());
                
            } catch (Exception e) {
                logger.error("Fallo al llamar servicio externo {}: {}", promocionesServiceUrl, e.getMessage());
                result.put("externalServiceError", e.getMessage());
            }
        }
        
        logger.info("Returning result: {}", result);
        
        return ResponseEntity.ok(result);
    }
} 