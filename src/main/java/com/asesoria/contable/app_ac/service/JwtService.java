package com.asesoria.contable.app_ac.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    public String getToken(UserDetails usuario);
    String getUsernameFromToken(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}
