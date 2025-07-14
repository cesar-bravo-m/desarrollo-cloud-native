package com.grupo11.adminpromociones.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo11.adminpromociones.model.Producto;
import com.grupo11.adminpromociones.model.Promocion;
import com.grupo11.adminpromociones.model.StockDto;
import com.grupo11.adminpromociones.repository.ProductoRepository;

@Service
public class KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.stock}")
    private String stockTopic;

    @Value("${app.kafka.topics.ventas}")
    private String ventasTopic;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Autowired
    private ProductoRepository productoRepository;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate, ProductoRepository productoRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.productoRepository = productoRepository;
        this.objectMapper = new ObjectMapper();
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
    }

    public Map<String, Object> readVentasAndStockTopicsAndCreatePromocion() {
        Map<String, Object> result = new HashMap<>();
        List<String> ventasMessages = new ArrayList<>();
        List<StockDto> stockData = new ArrayList<>();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-read-all");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        // Leer el topic de ventas
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(ventasTopic));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            
            logger.info("Found {} messages in ventas topic", records.count());
            for (ConsumerRecord<String, String> record : records) {
                ventasMessages.add(record.value());
                logger.info("Ventas message: {}", record.value());
            }
        } catch (Exception e) {
            logger.error("Error reading from ventas topic: {}", e.getMessage());
        }

        // Leer el topic de stock y parsear como StockDto
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(stockTopic));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            
            logger.info("Found {} messages in stock topic", records.count());
            for (ConsumerRecord<String, String> record : records) {
                try {
                    StockDto stockDto = objectMapper.readValue(record.value(), StockDto.class);
                    stockData.add(stockDto);
                    logger.info("Stock data: {}", stockDto);
                } catch (Exception e) {
                    logger.warn("Could not parse stock message as JSON: {}", record.value());
                    // Intentar crear StockDto desde string si la parseo JSON falla
                    StockDto fallbackStock = parseStockFromString(record.value());
                    if (fallbackStock != null) {
                        stockData.add(fallbackStock);
                        logger.info("Parsed stock data from string: {}", fallbackStock);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error reading from stock topic: {}", e.getMessage());
        }

        result.put("ventas", ventasMessages);
        result.put("stock", stockData);
        
        // Crear heuristica para el item menos vendido
        Promocion promocionCreated = createPromocionForLeastSoldItem(ventasMessages, stockData);
        result.put("promocionCreated", promocionCreated);
        
        return result;
    }

    private Promocion createPromocionForLeastSoldItem(List<String> ventasMessages, List<StockDto> stockData) {
        // Heuristica simple: contar las ventas por ID de producto
        Map<Long, Integer> salesCount = new HashMap<>();
        Map<Long, Integer> stockQuantities = new HashMap<>();
        
        // Parsear los mensajes de ventas para contar las ventas por ID de producto
        for (String ventaMessage : ventasMessages) {
            try {
                // Asumiendo que el formato del mensaje de ventas contiene el ID de producto y la cantidad
                if (ventaMessage.contains("producto") && ventaMessage.contains("cantidad")) {
                    Long productId = extractProductIdFromMessage(ventaMessage);
                    Integer quantity = extractQuantityFromMessage(ventaMessage);
                    
                    if (productId != null && quantity != null) {
                        salesCount.put(productId, salesCount.getOrDefault(productId, 0) + quantity);
                        logger.info("Product {} has total sales: {}", productId, salesCount.get(productId));
                    }
                }
            } catch (Exception e) {
                logger.warn("Could not parse ventas message: {}", ventaMessage);
            }
        }
        
        // Usar los datos de stock para obtener las cantidades disponibles
        for (StockDto stock : stockData) {
            if (stock.getProductoId() != null && stock.getCantidad() != null) {
                stockQuantities.put(stock.getProductoId(), stock.getCantidad());
                logger.info("Product {} has stock quantity: {}", stock.getProductoId(), stock.getCantidad());
            }
        }
        
        // Encontrar el item menos vendido que tiene stock
        Long leastSoldProductId = null;
        int minSales = Integer.MAX_VALUE;
        
        if (salesCount.isEmpty() && !stockQuantities.isEmpty()) {
            // Si no hay datos de ventas, usar el primer producto con stock
            leastSoldProductId = stockQuantities.keySet().iterator().next();
            logger.info("No sales data found, using first product with stock: {}", leastSoldProductId);
        } else if (salesCount.isEmpty()) {
            // Si no hay datos de ventas y no hay datos de stock, usar el producto por defecto
            leastSoldProductId = 1L;
            logger.info("No sales or stock data found, using default product ID: {}", leastSoldProductId);
        } else {
            // Encontrar el producto con el mínimo de ventas que tiene stock
            for (Map.Entry<Long, Integer> entry : salesCount.entrySet()) {
                Long productId = entry.getKey();
                int sales = entry.getValue();
                
                // Preferir productos que tengan datos de stock, o usar cualquier producto si no hay datos de stock disponibles
                if ((stockQuantities.containsKey(productId) && stockQuantities.get(productId) > 0) || stockQuantities.isEmpty()) {
                    if (sales < minSales) {
                        minSales = sales;
                        leastSoldProductId = productId;
                    }
                }
            }
            
            if (leastSoldProductId != null) {
                logger.info("Least sold product: {} with {} sales", leastSoldProductId, minSales);
            } else {
                // Fallback si no se encuentra un producto valido
                leastSoldProductId = salesCount.keySet().iterator().next();
                logger.info("Using fallback product: {}", leastSoldProductId);
            }
        }
        
        // Crear promocion con 10% de descuento
        // Primero obtenemos el producto para leer su valor actual
        Producto producto = productoRepository.findById(leastSoldProductId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        BigDecimal defaultPrice = producto.getPrecio();
        BigDecimal discountedPrice = defaultPrice.multiply(new BigDecimal("0.90")); // descuento del 10%
        
        // Crear promocion
        Promocion promocion = new Promocion();
        promocion.setProductoId(leastSoldProductId);
        promocion.setCantidad(1); // Minimum quantity for promotion
        promocion.setValor(discountedPrice);
        promocion.setFechaInicio(LocalDate.now());
        promocion.setFechaFin(LocalDate.now().plusDays(30)); // 30-day promotion
        
        logger.info("Created promocion for product {} with 10% discount: discounted price={}", 
                   leastSoldProductId, discountedPrice);
        
        return promocion;
    }
    
    private StockDto parseStockFromString(String message) {
        try {
            Long productId = extractProductIdFromMessage(message);
            Integer cantidad = extractQuantityFromMessage(message);
            
            if (productId != null && cantidad != null) {
                return new StockDto(productId, cantidad);
            }
        } catch (Exception e) {
            logger.debug("Could not parse stock from string: {}", message);
        }
        return null;
    }
    
    private Long extractProductIdFromMessage(String message) {
        try {
            // Simple regex to find numbers after "producto"
            String[] parts = message.split("producto");
            if (parts.length > 1) {
                String productPart = parts[1].trim();
                String[] words = productPart.split("\\s+");
                for (String word : words) {
                    if (word.matches("\\d+")) {
                        return Long.parseLong(word);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error extracting product ID from: {}", message);
        }
        return null;
    }
    
    private Integer extractQuantityFromMessage(String message) {
        try {
            String[] parts = message.split("cantidad");
            if (parts.length > 1) {
                String quantityPart = parts[1].trim();
                String[] words = quantityPart.split("\\s+");
                for (String word : words) {
                    if (word.matches("\\d+")) {
                        return Integer.parseInt(word);
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Error extracting quantity from: {}", message);
        }
        return 1; // Default quantity
    }
} 