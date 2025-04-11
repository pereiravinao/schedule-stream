package com.br.authcommon.service;

import org.springframework.stereotype.Service;

import com.br.authcommon.exceptions.UnauthorizedException;
import com.br.authcommon.grpc.AuthServiceGrpc;
import com.br.authcommon.grpc.TokenRequest;
import com.br.authcommon.grpc.UserResponse;
import com.br.authcommon.utils.SecurityUtils;

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
            UserResponse userResponse = blockingStub.validateToken(request);
            SecurityUtils.setUser(userResponse);
            return userResponse;
        } catch (StatusRuntimeException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public UserResponse refreshToken(String refreshToken) {
        TokenRequest request = TokenRequest.newBuilder()
                .setToken(refreshToken)
                .build();
        UserResponse userResponse = blockingStub.refreshToken(request);
        SecurityUtils.setUser(userResponse);
        return userResponse;
    }
}