package com.example.ventasconsumidor.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesMessage {
    
    @JsonProperty("carritoId")
    private Long carritoId;
    
    @JsonProperty("usuarioId")
    private String usuarioId;
    
    public SalesMessage() {}
    
    public SalesMessage(Long carritoId, String usuarioId) {
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
    
    @Override
    public String toString() {
        return "SalesMessage{" +
                "carritoId=" + carritoId +
                ", usuarioId='" + usuarioId + '\'' +
                '}';
    }
} 