package com.example.ventasconsumidor.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eft_ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venta_id")
    private Long ventaId;

    @Column(name = "carrito_id", nullable = false, unique = true)
    private Long carritoId;

    @Column(name = "usuario_id", length = 255)
    private String usuarioId;

    @Column(name = "monto_total", precision = 18, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "url_recibo", length = 1024, nullable = false)
    private String urlRecibo;

    @Column(name = "creado_en", nullable = false)
    private OffsetDateTime creadoEn;

    @OneToOne
    @JoinColumn(name = "carrito_id", insertable = false, updatable = false)
    private Carrito carrito;

    public Venta() {
    }

    public Long getVentaId() {
        return ventaId;
    }

    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
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

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }
} 