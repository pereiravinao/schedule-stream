package com.br.authenticator.exception;

import org.springframework.http.HttpStatus;

public class UserException extends CustomException {
    
    public UserException(String message, int statusCode) {
        super(message, statusCode);
    }
    
    public static UserException userNotFound(String username) {
        return new UserException(
            String.format("Usuário '%s' não encontrado", username),
            HttpStatus.NOT_FOUND.value()
        );
    }
    
    public static UserException userNotFoundById(String id) {
        return new UserException(
            String.format("Usuário com ID '%s' não encontrado", id),
            HttpStatus.NOT_FOUND.value()
        );
    }
    
    public static UserException duplicateUsername(String username) {
        return new UserException(
            String.format("Usuário com nome '%s' já existe", username),
            HttpStatus.CONFLICT.value()
        );
    }
    
    public static UserException invalidData(String details) {
        return new UserException(
            String.format("Dados de usuário inválidos: %s", details),
            HttpStatus.BAD_REQUEST.value()
        );
    }
} 