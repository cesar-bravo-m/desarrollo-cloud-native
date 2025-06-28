package com.grupo11.cloud_ventas_producer.model;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TD_PRODUCTO")

public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_seq")
    @SequenceGenerator(name = "producto_seq", sequenceName = "SEQ_PRODUCTO_ID", allocationSize = 1)
    @Column(name = "PRODUCTO_ID")
    private Long productoId;
    @Column(name = "NOMBRE")
    private String nombre;
    @Column(name = "DESCRIPCION")
    private String descripcion;
    @Column(name = "STOCK_ACTUAL")
    private Long StockActual;
    @Column(name = "REGISTRO_FECHA")
    private Date registroFecha;
    @Column(name = "VALOR_COSTO")
    private Long valorCosto;
    @Column(name = "VALOR_VENTA")
    private Long valorVenta;
    public String getImagenUri() {
        return imagenUri;
    }
    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }
    @Column(name = "IMAGEN_URI")
    private String imagenUri;

    public Long getProductoId() {
        return productoId;
    }
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Long getStockActual() {
        return StockActual;
    }
    public void setStockActual(Long stockActual) {
        StockActual = stockActual;
    }
    public Date getRegistroFecha() {
        return registroFecha;
    }
    public void setRegistroFecha(Date registroFecha) {
        this.registroFecha = registroFecha;
    }
    public Long getValorCosto() {
        return valorCosto;
    }
    public void setValorCosto(Long valorCosto) {
        this.valorCosto = valorCosto;
    }
    public Long getValorVenta() {
        return valorVenta;
    }
    public void setValorVenta(Long valorVenta) {
        this.valorVenta = valorVenta;
    }

}
