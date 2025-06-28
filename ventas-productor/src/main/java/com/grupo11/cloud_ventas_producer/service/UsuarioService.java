package com.grupo11.cloud_ventas_producer.service;

import java.util.List;
import java.util.Optional;

import com.grupo11.cloud_ventas_producer.model.Usuario;

public interface UsuarioService {

    public List<Usuario> getAllUsuarios();
    public Optional<Usuario> getUsuarioById(Long id);
    public Usuario createUsuario(Usuario usuario);
    public Usuario updateUsuario(Long id, Usuario usuario);
    void deleteUsuario(Long id);


}
