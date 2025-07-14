package com.example.ventasconsumidor.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.ventasconsumidor.config.RabbitMQConfig;
import com.example.ventasconsumidor.model.Carrito;
import com.example.ventasconsumidor.model.CarritoItem;
import com.example.ventasconsumidor.model.SalesMessage;
import com.example.ventasconsumidor.model.Venta;
import com.example.ventasconsumidor.repository.CarritoRepository;
import com.example.ventasconsumidor.repository.VentaRepository;

@Service
public class SalesConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(SalesConsumer.class);

    @Value("${app.base-uri}")
    private String baseUri;

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private CarritoItemService carritoItemService;

    @Autowired
    private ProductoService productoService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SALES)
    public void consumeSalesMessage(SalesMessage salesMessage) {
        logger.info("Received sales message: {}", salesMessage);
        System.out.println("Received sales message: " + salesMessage);
        Long carritoId = salesMessage.getCarritoId();
        String usuarioId = salesMessage.getUsuarioId();

        Optional<Carrito> carritoOpt = carritoRepository.findById(carritoId);
        if (carritoOpt.isPresent()) {
            Carrito carrito = carritoOpt.get();
            
            if (!"A".equals(carrito.getEstado())) {
                return;
            }
            
            List<CarritoItem> items = carritoItemService.getItemsByCarritoId(carritoId);
            if (items.isEmpty()) {
                return;
            }
            
            BigDecimal total = BigDecimal.ZERO;
            
            // Reducir stock. ESTO SE DEBE HACER EN KAFKA DESPUÉS, pero por ahora se hace aquí.
            // for (CarritoItem item : items) {
            //     total = total.add(item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())));
            //     
            //     productoService.reduceStock(item.getProductoId(), item.getCantidad());
            // }
            
            Venta venta = new Venta();
            venta.setCarritoId(carritoId);
            venta.setUsuarioId(usuarioId);
            venta.setMontoTotal(total);
            venta.setUrlRecibo(baseUri + "/boleta?carritoId=" + carritoId);
            venta.setCreadoEn(OffsetDateTime.now());
            
            Venta savedVenta = ventaRepository.save(venta);
            
            carrito.setEstado("C");
            carrito.setActualizadoEn(OffsetDateTime.now());
            carritoRepository.save(carrito);
            
            logger.info("Venta procesada exitosamente: {}", savedVenta);
        } else {
            logger.warn("Carrito no encontrado con ID: {}", carritoId);
        }
    }
} 