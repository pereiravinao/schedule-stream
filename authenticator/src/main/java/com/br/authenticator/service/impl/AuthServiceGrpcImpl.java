package com.br.authenticator.service.impl;

import com.br.authenticator.grpc.AuthServiceGrpc;
import com.br.authenticator.grpc.TokenRequest;
import com.br.authenticator.grpc.UserResponse;
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
        var userResponse = this.authService.validateToken(token);
        var rolesList = userResponse.getRoles().stream().map(Enum::name).toList();
        UserResponse response = UserResponse.newBuilder()
                .setUsername(userResponse.getUsername())
                .setEmail(userResponse.getEmail())
                .setPhoneNumber(userResponse.getPhoneNumber())
                .addAllRoles(rolesList)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
