package com.example.promociones.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.promociones.config.RabbitMQConfig;
import com.example.promociones.model.Promocion;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping
    public ResponseEntity<String> crearPromocion(@RequestBody Promocion promocion) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.PROMOCIONES_QUEUE, promocion);
            
            return ResponseEntity.ok("Promoción enviada exitosamente a la cola: " + promocion.toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al enviar la promoción a la cola: " + e.getMessage());
        }
    }
} 