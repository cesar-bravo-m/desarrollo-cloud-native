package com.grupo11.cloud_ventas_producer.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eft_carrito_item")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrito_item_id")
    private Long carritoItemId;

    @Column(name = "carrito_id", nullable = false)
    private Long carritoId;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Column(name = "cantidad", nullable = false)
    private Long cantidad;

    @Column(name = "precio_unitario", precision = 18, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "agregado_en", nullable = false)
    private OffsetDateTime agregadoEn;

    @ManyToOne
    @JoinColumn(name = "carrito_id", insertable = false, updatable = false)
    private Carrito carrito;

    @ManyToOne
    @JoinColumn(name = "producto_id", insertable = false, updatable = false)
    private Producto producto;

    public CarritoItem() {
    }

    public Long getCarritoItemId() {
        return carritoItemId;
    }

    public void setCarritoItemId(Long carritoItemId) {
        this.carritoItemId = carritoItemId;
    }

    public Long getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public OffsetDateTime getAgregadoEn() {
        return agregadoEn;
    }

    public void setAgregadoEn(OffsetDateTime agregadoEn) {
        this.agregadoEn = agregadoEn;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
} 