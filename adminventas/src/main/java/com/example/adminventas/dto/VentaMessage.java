package com.example.adminventas.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VentaMessage {
    
    @JsonProperty("carritoId")
    private Long carritoId;
    
    @JsonProperty("userId")
    private String userId;
    
    public VentaMessage() {}
    
    public VentaMessage(Long carritoId, String userId) {
        this.carritoId = carritoId;
        this.userId = userId;
    }
    
    public Long getCarritoId() {
        return carritoId;
    }
    
    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "VentaMessage{" +
                "carritoId=" + carritoId +
                ", userId='" + userId + '\'' +
                '}';
    }
} 