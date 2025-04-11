package com.br.authenticator.exception.grpc;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionAdvice {

    @GrpcExceptionHandler(GrpcBaseException.class)
    public StatusRuntimeException handleGrpcBaseException(GrpcBaseException e) {
        return e.getStatus().asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleUnauthenticated(GrpcUnauthenticatedException e) {
        return e.getStatus().asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleNotFound(GrpcNotFoundException e) {
        return e.getStatus().asRuntimeException();
    }
}