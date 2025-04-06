package com.br.authenticator.service.impl;

import com.br.authcommon.grpc.AuthServiceGrpc;
import com.br.authcommon.grpc.TokenRequest;
import com.br.authcommon.grpc.UserResponse;

import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.service.AuthService;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AuthServiceGrpcImpl extends AuthServiceGrpc.AuthServiceImplBase {

    private final AuthService authService;

    public AuthServiceGrpcImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void validateToken(TokenRequest request, StreamObserver<UserResponse> responseObserver) {
        String token = request.getToken();
        UserTokenDTO userResponse = this.authService.validateToken(token);
        UserResponse response = buildUserResponse(userResponse);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void refreshToken(TokenRequest request, StreamObserver<UserResponse> responseObserver) {
        String refreshToken = request.getToken();
        UserTokenDTO userResponse = this.authService.refreshToken(refreshToken);
        UserResponse response = buildUserResponse(userResponse);

        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    private UserResponse buildUserResponse(UserTokenDTO userResponse) {
        var rolesList = userResponse.getRoles().stream().map(Enum::name).toList();
        return UserResponse.newBuilder()
                .setUsername(userResponse.getUsername())
                .setEmail(userResponse.getEmail())
                .setPhoneNumber(userResponse.getPhoneNumber())
                .addAllRoles(rolesList)
                .build();
    }
}
