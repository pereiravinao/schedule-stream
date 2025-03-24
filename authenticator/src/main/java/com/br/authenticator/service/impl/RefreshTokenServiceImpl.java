package com.br.authenticator.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.br.authenticator.entity.RefreshTokenEntity;
import com.br.authenticator.model.RefreshToken;
import com.br.authenticator.model.User;
import com.br.authenticator.repository.RefreshTokenRepository;
import com.br.authenticator.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    
    @Value("${app.refresh-token.duration-ms}")
    private long refreshTokenDurationMs;
    
    @Override
    public RefreshToken createRefreshToken(User user) {
        List<RefreshTokenEntity> existingTokens = refreshTokenRepository.findByUserId(user.getId());
        if (!existingTokens.isEmpty()) {
            log.info("Encontrados {} tokens antigos para o usuário: {}", existingTokens.size(), user.getUsername());
            existingTokens.forEach(token -> {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
                log.info("Token antigo revogado: {}", token.getToken());
            });
        }
        
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .token(java.util.UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();
        
        RefreshTokenEntity savedEntity = refreshTokenRepository.save(new RefreshTokenEntity(refreshToken));
        log.info("Novo refresh token criado para usuário: {}", user.getUsername());
        
        return savedEntity.toModel();
    }
    
    @Override
    public boolean validateRefreshToken(String token) {
        Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByToken(token);
        
        if (refreshToken.isEmpty()) {
            log.warn("Refresh token não encontrado: {}", token);
            return false;
        }
        
        RefreshTokenEntity tokenEntity = refreshToken.get();
        
        if (tokenEntity.isRevoked()) {
            log.warn("Tentativa de uso de refresh token revogado: {}", token);
            return false;
        }
        
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Tentativa de uso de refresh token expirado: {}", token);
            tokenEntity.setRevoked(true);
            refreshTokenRepository.save(tokenEntity);
            return false;
        }
        
        return true;
    }
    
    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshTokenEntity::toModel)
                .orElse(null);
    }
    
    @Override
    public RefreshToken findByUserId(String userId) {
        List<RefreshTokenEntity> tokens = refreshTokenRepository.findByUserId(userId);
        if (tokens.isEmpty()) {
            return null;
        }
        
        Optional<RefreshTokenEntity> validToken = tokens.stream()
                .filter(token -> !token.isRevoked())
                .max((t1, t2) -> t1.getExpiryDate().compareTo(t2.getExpiryDate()));
        
        return validToken.map(RefreshTokenEntity::toModel).orElse(null);
    }
    
    @Override
    public void revokeRefreshToken(String token) {
        Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByToken(token);
        
        if (refreshToken.isPresent()) {
            RefreshTokenEntity tokenEntity = refreshToken.get();
            tokenEntity.setRevoked(true);
            refreshTokenRepository.save(tokenEntity);
            log.info("Refresh token revogado: {}", token);
        } else {
            log.warn("Tentativa de revogar refresh token inexistente: {}", token);
        }
    }
    
    @Override
    public void revokeAllUserTokens(String userId) {
        List<RefreshTokenEntity> tokens = refreshTokenRepository.findByUserId(userId);
        
        if (!tokens.isEmpty()) {
            for (RefreshTokenEntity token : tokens) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
            log.info("Todos os {} refresh tokens do usuário {} foram revogados", tokens.size(), userId);
        } else {
            log.info("Nenhum refresh token encontrado para o usuário: {}", userId);
        }
    }
} 