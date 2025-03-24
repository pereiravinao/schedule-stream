package com.br.authenticator.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.br.authenticator.model.User;

public interface TokenService {

    public String generateToken(UserDetails userDetails);

    public String generateRefreshToken(UserDetails userDetails);

    public boolean validateToken(String token);

    public User extractUser(String token);

}