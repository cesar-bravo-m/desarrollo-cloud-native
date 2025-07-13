package com.example.ventasconsumidor.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class SaleEvent {
    
    private Long carritoId;
    private String usuarioId;
    private BigDecimal montoTotal;
    private String urlRecibo;
    private OffsetDateTime fechaVenta;
    private List<SaleItem> items;
    private String estado;
    private String eventType;
    
    // Constructor
    public SaleEvent() {
        this.eventType = "SALE_COMPLETED";
        this.fechaVenta = OffsetDateTime.now();
    }
    
    // Getters and Setters
    public Long getCarritoId() {
        return carritoId;
    }
    
    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
    }
    
    public String getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public BigDecimal getMontoTotal() {
        return montoTotal;
    }
    
    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    public String getUrlRecibo() {
        return urlRecibo;
    }
    
    public void setUrlRecibo(String urlRecibo) {
        this.urlRecibo = urlRecibo;
    }
    
    public OffsetDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(OffsetDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public List<SaleItem> getItems() {
        return items;
    }
    
    public void setItems(List<SaleItem> items) {
        this.items = items;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    // Inner class for sale items
    public static class SaleItem {
        private Long productoId;
        private String productoSku;
        private String productoNombre;
        private Long cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
        
        // Constructor
        public SaleItem() {}
        
        public SaleItem(Long productoId, String productoSku, String productoNombre, 
                       Long cantidad, BigDecimal precioUnitario) {
            this.productoId = productoId;
            this.productoSku = productoSku;
            this.productoNombre = productoNombre;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
        }
        
        // Getters and Setters
        public Long getProductoId() {
            return productoId;
        }
        
        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }
        
        public String getProductoSku() {
            return productoSku;
        }
        
        public void setProductoSku(String productoSku) {
            this.productoSku = productoSku;
        }
        
        public String getProductoNombre() {
            return productoNombre;
        }
        
        public void setProductoNombre(String productoNombre) {
            this.productoNombre = productoNombre;
        }
        
        public Long getCantidad() {
            return cantidad;
        }
        
        public void setCantidad(Long cantidad) {
            this.cantidad = cantidad;
            // Recalculate subtotal when quantity changes
            if (this.precioUnitario != null) {
                this.subtotal = this.precioUnitario.multiply(new BigDecimal(cantidad));
            }
        }
        
        public BigDecimal getPrecioUnitario() {
            return precioUnitario;
        }
        
        public void setPrecioUnitario(BigDecimal precioUnitario) {
            this.precioUnitario = precioUnitario;
            // Recalculate subtotal when price changes
            if (this.cantidad != null) {
                this.subtotal = precioUnitario.multiply(new BigDecimal(this.cantidad));
            }
        }
        
        public BigDecimal getSubtotal() {
            return subtotal;
        }
        
        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
} 