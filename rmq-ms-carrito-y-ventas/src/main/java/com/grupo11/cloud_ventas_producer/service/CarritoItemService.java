package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.cloud_ventas_producer.model.CarritoItem;

public interface CarritoItemService {

    List<CarritoItem> getAllCarritoItems();
    Optional<CarritoItem> getCarritoItemById(Long id);
    CarritoItem createCarritoItem(CarritoItem carritoItem);
    CarritoItem updateCarritoItem(Long id, CarritoItem carritoItem);
    void deleteCarritoItem(Long id);
    
    List<CarritoItem> getItemsByCarritoId(Long carritoId);
    Optional<CarritoItem> getItemByCarritoAndProducto(Long carritoId, Long productoId);
    Long calculateTotalByCarritoId(Long carritoId);
    
    CarritoItem addItemToCarrito(Long carritoId, Long productoId, Long cantidad);
    boolean updateItemQuantity(Long carritoId, Long productoId, Long cantidad);
    boolean removeItemFromCarrito(Long carritoId, Long productoId);
    void clearCarrito(Long carritoId);

} 