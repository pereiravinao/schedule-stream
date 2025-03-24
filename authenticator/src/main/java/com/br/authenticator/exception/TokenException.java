package com.br.authenticator.exception;

import org.springframework.http.HttpStatus;

public class TokenException extends CustomException {
    
    public TokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public static TokenException expired() {
        return new TokenException("Token expirado");
    }
    
    public static TokenException invalid() {
        return new TokenException("Token inválido");
    }
    
    public static TokenException missing() {
        return new TokenException("Token não fornecido");
    }
    
    public static TokenException malformed() {
        return new TokenException("Token com formato inválido");
    }
} 