package com.br.authcommon.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.br.authcommon.grpc.AuthServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcClientConfig {

    @Value("${grpc.client.auth-service.host:localhost}")
    private String host;

    @Value("${grpc.client.auth-service.port:9090}")
    private int port;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .enableRetry()
                .maxRetryAttempts(3)
                .retryBufferSize(16777216) // 16MB
                .keepAliveWithoutCalls(true)
                .keepAliveTime(30, TimeUnit.SECONDS)
                .keepAliveTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public AuthServiceGrpc.AuthServiceBlockingStub authServiceBlockingStub(ManagedChannel channel) {
        return AuthServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .withWaitForReady();
    }

    @Bean
    public AuthServiceGrpc.AuthServiceStub authServiceStub(ManagedChannel channel) {
        return AuthServiceGrpc.newStub(channel)
                .withDeadlineAfter(30, TimeUnit.SECONDS)
                .withWaitForReady();
    }
}