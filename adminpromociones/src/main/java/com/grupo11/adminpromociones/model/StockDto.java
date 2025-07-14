package com.grupo11.adminpromociones.model;

public class StockDto {
    private Long productoId;
    private Integer cantidad;

    public StockDto() {}

    public StockDto(Long productoId, Integer cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return "StockDto{" +
                "productoId=" + productoId +
                ", cantidad=" + cantidad +
                '}';
    }
} 