package com.example.ventasconsumidor.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ventasconsumidor.model.Carrito;
import com.example.ventasconsumidor.model.CarritoItem;
import com.example.ventasconsumidor.model.Venta;
import com.example.ventasconsumidor.repository.CarritoRepository;
import com.example.ventasconsumidor.repository.VentaRepository;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private CarritoItemService carritoItemService;
    
    @Autowired
    private ProductoService productoService;

    @Override
    public List<Venta> getAllVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public Optional<Venta> getVentaById(Long id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta createVenta(Venta venta) {
        venta.setCreadoEn(OffsetDateTime.now());
        return ventaRepository.save(venta);
    }

    @Override
    public Venta updateVenta(Long id, Venta venta) {
        if (ventaRepository.existsById(id)) {
            venta.setVentaId(id);
            return ventaRepository.save(venta);
        } else {
            return null;
        }
    }

    @Override
    public void deleteVenta(Long id) {
        ventaRepository.deleteById(id);
    }

    @Override
    public List<Venta> getVentasByUsuarioId(String usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Optional<Venta> getVentaByCarritoId(Long carritoId) {
        return ventaRepository.findByCarritoId(carritoId);
    }

    @Override
    public List<Venta> getVentasByUsuarioIdOrderedByDate(String usuarioId) {
        return ventaRepository.findVentasByUsuarioIdOrderByFecha(usuarioId);
    }

    @Override
    public Long getTotalVentasByUsuario(String usuarioId) {
        return ventaRepository.getTotalVentasByUsuario(usuarioId);
    }

    @Override
    @Transactional
    public Venta processCheckout(Long carritoId, String usuarioId) {
        Optional<Carrito> carritoOpt = carritoRepository.findById(carritoId);
        if (carritoOpt.isPresent()) {
            Carrito carrito = carritoOpt.get();
            
            // Verify cart is active
            if (!"A".equals(carrito.getEstado())) {
                return null;
            }
            
            // Get cart items
            List<CarritoItem> items = carritoItemService.getItemsByCarritoId(carritoId);
            if (items.isEmpty()) {
                return null;
            }
            
            // Calculate total
            BigDecimal total = BigDecimal.ZERO;
            
            for (CarritoItem item : items) {
                total = total.add(item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())));
                
                // Reduce stock
                productoService.reduceStock(item.getProductoId(), item.getCantidad());
            }
            
            // Create and save venta to database
            Venta venta = new Venta();
            venta.setCarritoId(carritoId);
            venta.setUsuarioId(usuarioId);
            venta.setMontoTotal(total);
            venta.setUrlRecibo("https://receipts.store.com/receipt-" + System.currentTimeMillis() + ".pdf");
            venta.setCreadoEn(OffsetDateTime.now());
            
            // Save venta to database
            Venta savedVenta = ventaRepository.save(venta);
            
            // Update carrito status to completed
            carrito.setEstado("C");
            carrito.setActualizadoEn(OffsetDateTime.now());
            carritoRepository.save(carrito);
            
            return savedVenta;
        }
        return null;
    }

    @Override
    public String getSaleStatusByCarritoId(Long carritoId) {
        Optional<Carrito> carritoOpt = carritoRepository.findById(carritoId);
        if (carritoOpt.isPresent()) {
            return carritoOpt.get().getEstado();
        }
        return null;
    }

    @Override
    public void sendVentaToQueue(Venta venta) {
        // This method is deprecated and no longer used
        // Sales are now stored directly in the database
    }
} 