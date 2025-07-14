package com.grupo11.cloud_ventas_producer.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grupo11.cloud_ventas_producer.model.Carrito;
import com.grupo11.cloud_ventas_producer.model.SaleEvent;
import com.grupo11.cloud_ventas_producer.model.Venta;
import com.grupo11.cloud_ventas_producer.repository.CarritoRepository;
import com.grupo11.cloud_ventas_producer.repository.VentaRepository;

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
    
    @Autowired
    private CarroProducer carroProducer;

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
    public void processCheckout(Long carritoId, String usuarioId) {
        SaleEvent saleEvent = new SaleEvent(carritoId, usuarioId);
        carroProducer.sendMessage(saleEvent);
    }

    @Override
    public String getSaleStatusByCarritoId(Long carritoId) {
        Optional<Carrito> carritoOpt = carritoRepository.findById(carritoId);
        if (carritoOpt.isPresent()) {
            return carritoOpt.get().getEstado();
        }
        return null;
    }
} 