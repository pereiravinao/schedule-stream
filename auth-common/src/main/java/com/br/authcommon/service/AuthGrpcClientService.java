package com.br.authcommon.service;

import com.br.authcommon.grpc.AuthServiceGrpc;
import com.br.authcommon.grpc.TokenRequest;
import com.br.authcommon.grpc.UserResponse;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthGrpcClientService {

    private final AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    public UserResponse validateToken(String token) {
        try {
            TokenRequest request = TokenRequest.newBuilder()
                    .setToken(token)
                    .build();
            return blockingStub.validateToken(request);
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Erro ao validar token: " + e.getMessage());
        }
    }

    public UserResponse refreshToken(String refreshToken) {
        TokenRequest request = TokenRequest.newBuilder()
                .setToken(refreshToken)
                .build();
        return blockingStub.refreshToken(request);
    }
}