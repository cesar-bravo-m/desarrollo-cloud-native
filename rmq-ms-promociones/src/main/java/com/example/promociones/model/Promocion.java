package com.example.promociones.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Promocion {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal valor;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Promocion() {}

    public Promocion(Long id, Long productoId, Integer cantidad, BigDecimal valor, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.valor = valor;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return "Promocion{" +
                "id=" + id +
                ", productoId='" + productoId + '\'' +
                ", cantidad=" + cantidad +
                ", valor=" + valor +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                '}';
    }
} 