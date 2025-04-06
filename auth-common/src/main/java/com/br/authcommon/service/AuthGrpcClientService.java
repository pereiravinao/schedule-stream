package com.br.authcommon.service;

import com.br.authcommon.grpc.AuthServiceGrpc;
import com.br.authcommon.grpc.TokenRequest;
import com.br.authcommon.grpc.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthGrpcClientService {

    private final AuthServiceGrpc.AuthServiceBlockingStub blockingStub;

    public UserResponse validateToken(String token) {
        TokenRequest request = TokenRequest.newBuilder()
                .setToken(token)
                .build();
        return blockingStub.validateToken(request);
    }

    public UserResponse refreshToken(String refreshToken) {
        TokenRequest request = TokenRequest.newBuilder()
                .setToken(refreshToken)
                .build();
        return blockingStub.refreshToken(request);
    }
} 