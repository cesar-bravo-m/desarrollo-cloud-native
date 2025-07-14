package com.grupo11.cloud_ventas_producer.model;

public class SaleEvent {
    private Long carritoId;
    private String usuarioId;

    public SaleEvent() {
    }

    public SaleEvent(Long carritoId, String usuarioId) {
        this.carritoId = carritoId;
        this.usuarioId = usuarioId;
    }

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
} 