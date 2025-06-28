package com.example.ventasconsumidor.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TD_CARRO")

public class Carro {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carro_seq")
    @SequenceGenerator(name = "carro_seq", sequenceName = "SEQ_CARRO_ID", allocationSize = 1)
    @Column(name = "CARRO_ID")
    private Long carroId;
    @Column(name = "USUARIO_ID")
    private String usuarioId;
    @Column(name = "PRODUCTO_ID")
    private Long productoId;
    @Column(name = "CANTIDAD")
    private Long cantidad;
    @Column(name = "REGISTRO_FECHA")
    private Date registroFecha;
    @Column(name = "VIGENCiA_FLAG")
    private int vigenciaFlag;
    @Column(name = "TICKET_ID")
    private Long ticketId;

    public Long getCarroId() {
        return carroId;
    }
    public void setCarroId(Long carroId) {
        this.carroId = carroId;
    }
    public String getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
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
    public Date getRegistroFecha() {
        return registroFecha;
    }
    public void setRegistroFecha(Date registroFecha) {
        this.registroFecha = registroFecha;
    }
    public int getVigenciaFlag() {
        return vigenciaFlag;
    }
    public void setVigenciaFlag(int vigenciaFlag) {
        this.vigenciaFlag = vigenciaFlag;
    }
    public Long getTicketId() {
        return ticketId;
    }
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
