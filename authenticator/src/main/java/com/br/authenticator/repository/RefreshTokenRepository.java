package com.br.authenticator.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.br.authenticator.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
    
    List<RefreshTokenEntity> findByUserId(String userId);
    
    void deleteByUserId(String userId);
} 