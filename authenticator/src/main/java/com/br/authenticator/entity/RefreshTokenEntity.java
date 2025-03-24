package com.br.authenticator.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.br.authenticator.model.RefreshToken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    private String id;
    
    @Indexed(unique = true)
    private String token;
    
    @Indexed
    private String userId;
    
    private Instant expiryDate;
    
    private boolean revoked;
    
    public RefreshTokenEntity(RefreshToken refreshToken) {
        this.id = refreshToken.getId();
        this.token = refreshToken.getToken();
        this.userId = refreshToken.getUserId();
        this.expiryDate = refreshToken.getExpiryDate();
        this.revoked = refreshToken.isRevoked();
    }
    
    public RefreshToken toModel() {
        return RefreshToken.builder()
                .id(this.id)
                .token(this.token)
                .userId(this.userId)
                .expiryDate(this.expiryDate)
                .revoked(this.revoked)
                .build();
    }
}
