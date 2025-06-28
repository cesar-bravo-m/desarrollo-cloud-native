package com.grupo11.cloud_ventas_producer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TD_USUARIO")

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "SEQ_USUARIO_ID", allocationSize = 1)
    @Column(name = "USUARIO_ID")
    private Long usuarioId;
    @Column(name = "EMAIL")
    private Long email;
    @Column(name = "NOMBRE")
    private Long nombre;
    @Column(name = "LOGIN_ULTIMO")
    private Long loginUltimo;
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    public Long getEmail() {
        return email;
    }
    public void setEmail(Long email) {
        this.email = email;
    }
    public Long getNombre() {
        return nombre;
    }
    public void setNombre(Long nombre) {
        this.nombre = nombre;
    }
    public Long getLoginUltimo() {
        return loginUltimo;
    }
    public void setLoginUltimo(Long loginUltimo) {
        this.loginUltimo = loginUltimo;
    }


}
