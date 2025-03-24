package com.br.authenticator.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends CustomException {
    
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED.value());
    }
    
    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Nome de usuário ou senha incorretos");
    }
    
    public static AuthenticationException invalidRefreshToken() {
        return new AuthenticationException("Token de atualização inválido ou expirado");
    }
    
    public static AuthenticationException invalidToken() {
        return new AuthenticationException("Token inválido ou expirado");
    }
} 