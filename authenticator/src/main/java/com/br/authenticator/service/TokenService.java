package com.br.authenticator.service;

import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface TokenService {

    public String generateToken(UserDetails userDetails);

    public String generateRefreshToken(UserDetails userDetails);

    public boolean validateToken(String token);

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    public String extractUsername(String token);

}