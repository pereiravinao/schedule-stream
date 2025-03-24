package com.br.authenticator.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private String id;
    private String token;
    private String userId;
    private Instant expiryDate;
    private boolean revoked;
} 