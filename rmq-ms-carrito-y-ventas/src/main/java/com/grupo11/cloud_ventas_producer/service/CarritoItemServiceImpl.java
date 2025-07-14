package com.grupo11.cloud_ventas_producer.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.grupo11.cloud_ventas_producer.model.CarritoItem;
import com.grupo11.cloud_ventas_producer.model.Producto;
import com.grupo11.cloud_ventas_producer.repository.CarritoItemRepository;
import com.grupo11.cloud_ventas_producer.repository.ProductoRepository;

@Service
public class CarritoItemServiceImpl implements CarritoItemService {

    @Autowired
    private CarritoItemRepository carritoItemRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<CarritoItem> getAllCarritoItems() {
        return carritoItemRepository.findAll();
    }

    @Override
    public Optional<CarritoItem> getCarritoItemById(Long id) {
        return carritoItemRepository.findById(id);
    }

    @Override
    public CarritoItem createCarritoItem(CarritoItem carritoItem) {
        carritoItem.setAgregadoEn(OffsetDateTime.now());
        return carritoItemRepository.save(carritoItem);
    }

    @Override
    public CarritoItem updateCarritoItem(Long id, CarritoItem carritoItem) {
        if (carritoItemRepository.existsById(id)) {
            carritoItem.setCarritoItemId(id);
            return carritoItemRepository.save(carritoItem);
        } else {
            return null;
        }
    }

    @Override
    public void deleteCarritoItem(Long id) {
        carritoItemRepository.deleteById(id);
    }

    @Override
    public List<CarritoItem> getItemsByCarritoId(Long carritoId) {
        return carritoItemRepository.findByCarritoId(carritoId);
    }

    @Override
    public Optional<CarritoItem> getItemByCarritoAndProducto(Long carritoId, Long productoId) {
        return carritoItemRepository.findByCarritoIdAndProductoId(carritoId, productoId);
    }

    @Override
    public Long calculateTotalByCarritoId(Long carritoId) {
        return carritoItemRepository.calculateTotalByCarritoId(carritoId);
    }

    @Override
    public CarritoItem addItemToCarrito(Long carritoId, Long productoId, Long cantidad) {
        Optional<Producto> productoOpt = productoRepository.findById(productoId);
        if (productoOpt.isPresent()) {
            Producto producto = productoOpt.get();
            
            // Check if item already exists in cart
            Optional<CarritoItem> existingItem = carritoItemRepository.findByCarritoIdAndProductoId(carritoId, productoId);
            
            if (existingItem.isPresent()) {
                // Update existing item
                CarritoItem item = existingItem.get();
                item.setCantidad(item.getCantidad() + cantidad);
                return carritoItemRepository.save(item);
            } else {
                // Create new item
                CarritoItem newItem = new CarritoItem();
                newItem.setCarritoId(carritoId);
                newItem.setProductoId(productoId);
                newItem.setCantidad(cantidad);
                newItem.setPrecioUnitario(producto.getPrecio());
                newItem.setAgregadoEn(OffsetDateTime.now());
                return carritoItemRepository.save(newItem);
            }
        }
        return null;
    }

    @Override
    public boolean updateItemQuantity(Long carritoId, Long productoId, Long cantidad) {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByCarritoIdAndProductoId(carritoId, productoId);
        if (itemOpt.isPresent()) {
            CarritoItem item = itemOpt.get();
            item.setCantidad(cantidad);
            carritoItemRepository.save(item);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeItemFromCarrito(Long carritoId, Long productoId) {
        Optional<CarritoItem> itemOpt = carritoItemRepository.findByCarritoIdAndProductoId(carritoId, productoId);
        if (itemOpt.isPresent()) {
            carritoItemRepository.delete(itemOpt.get());
            return true;
        }
        return false;
    }

    @Override
    public void clearCarrito(Long carritoId) {
        carritoItemRepository.deleteByCarritoId(carritoId);
    }
} 