package com.asesoria.contable.app_ac.service;

import com.asesoria.contable.app_ac.model.dto.*;
import com.asesoria.contable.app_ac.model.dto.auth.AuthResponse;
import com.asesoria.contable.app_ac.model.dto.auth.LoginUsuarioRequest;
import com.asesoria.contable.app_ac.model.dto.auth.RegisterClienteRequest;
import com.asesoria.contable.app_ac.model.entity.Usuario;

public interface AuthService {

    public AuthResponse login(LoginUsuarioRequest request);
    public ClienteResponse devolverCliente(Long usuarioId);
    public AuthResponse registerCliente(RegisterClienteRequest request);
    public Usuario getUsuarioActual();
//    public void registerUsuario(UsuarioRequest request);
}
