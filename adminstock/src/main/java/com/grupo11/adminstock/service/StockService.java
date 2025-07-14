package com.grupo11.adminstock.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo11.adminstock.dto.StockDto;
import com.grupo11.adminstock.model.Carrito;
import com.grupo11.adminstock.model.CarritoItem;
import com.grupo11.adminstock.model.Producto;
import com.grupo11.adminstock.repository.CarritoItemRepository;
import com.grupo11.adminstock.repository.CarritoRepository;
import com.grupo11.adminstock.repository.ProductoRepository;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final KafkaService kafkaService;

    public StockService(CarritoRepository carritoRepository, 
                       CarritoItemRepository carritoItemRepository,
                       ProductoRepository productoRepository,
                       KafkaService kafkaService) {
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
        this.kafkaService = kafkaService;
    }

    @Transactional
    public void processVenta(Long carritoId, String userId) {
        logger.info("Processing venta for carrito: {} and user: {}", carritoId, userId);

        // Fetch carrito
        Optional<Carrito> carritoOpt = carritoRepository.findById(carritoId);
        if (carritoOpt.isEmpty()) {
            logger.error("Carrito not found with id: {}", carritoId);
            return;
        }

        Carrito carrito = carritoOpt.get();
        logger.info("Found carrito: {} for user: {}", carrito.getCarritoId(), carrito.getUsuarioId());

        // Fetch carrito items
        List<CarritoItem> carritoItems = carritoItemRepository.findByCarritoId(carritoId);
        logger.info("Found {} items in carrito {}", carritoItems.size(), carritoId);

        // Update stock for each item
        for (CarritoItem item : carritoItems) {
            updateProductStock(item);
        }
    }

    private void updateProductStock(CarritoItem item) {
        logger.info("Updating stock for product: {} with quantity: {}", item.getProductoId(), item.getCantidad());

        Optional<Producto> productoOpt = productoRepository.findById(item.getProductoId());
        if (productoOpt.isEmpty()) {
            logger.error("Producto not found with id: {}", item.getProductoId());
            return;
        }

        Producto producto = productoOpt.get();
        Long currentStock = producto.getCantidadStock();
        Long quantitySold = item.getCantidad();

        if (currentStock < quantitySold) {
            logger.warn("Insufficient stock for product: {}. Current: {}, Requested: {}", 
                       item.getProductoId(), currentStock, quantitySold);
            return;
        }

        // Update stock
        Long newStock = currentStock - quantitySold;
        producto.setCantidadStock(newStock);
        productoRepository.save(producto);

        logger.info("Updated stock for product: {} from {} to {}", 
                   item.getProductoId(), currentStock, newStock);

        // Send stock update message to Kafka
        StockDto stockDto = new StockDto(item.getProductoId(), newStock.intValue());
        kafkaService.sendStockUpdate(stockDto);
    }
} 