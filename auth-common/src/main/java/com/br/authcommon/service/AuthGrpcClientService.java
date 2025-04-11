package com.br.authcommon.service;

import org.springframework.stereotype.Service;

import com.br.authcommon.exceptions.UnauthorizedException;
import com.br.authcommon.grpc.AuthServiceGrpc;
import com.br.authcommon.grpc.TokenRequest;
import com.br.authcommon.grpc.UserResponse;

import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;

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
            throw new UnauthorizedException();
        }
    }

    public UserResponse refreshToken(String refreshToken) {
        TokenRequest request = TokenRequest.newBuilder()
                .setToken(refreshToken)
                .build();
        return blockingStub.refreshToken(request);
    }
}