package com.br.authenticator.service;

import com.br.authenticator.model.RefreshToken;
import com.br.authenticator.model.User;

public interface RefreshTokenService {
    /**
     * Cria um novo refresh token para um usuário
     * 
     * @param user Usuário para o qual o token será criado
     * @return O token recém-criado
     */
    RefreshToken createRefreshToken(User user);
    
    /**
     * Verifica se um refresh token é válido
     * 
     * @param token O token a ser verificado
     * @return true se o token for válido, false caso contrário
     */
    boolean validateRefreshToken(String token);
    
    /**
     * Busca um refresh token pelo token
     * 
     * @param token O token a ser buscado
     * @return O refresh token se encontrado, null caso contrário
     */
    RefreshToken findByToken(String token);
    
    /**
     * Busca um refresh token pelo ID do usuário
     * 
     * @param userId ID do usuário
     * @return O refresh token se encontrado, null caso contrário
     */
    RefreshToken findByUserId(String userId);
    
    /**
     * Revoga um refresh token
     * 
     * @param token O token a ser revogado
     */
    void revokeRefreshToken(String token);
    
    /**
     * Revoga todos os tokens de um usuário
     * 
     * @param userId ID do usuário
     */
    void revokeAllUserTokens(String userId);
} 