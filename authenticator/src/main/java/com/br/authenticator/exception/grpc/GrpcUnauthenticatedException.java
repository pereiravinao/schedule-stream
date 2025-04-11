package com.br.authenticator.exception.grpc;

import io.grpc.Status;

public class GrpcUnauthenticatedException extends GrpcBaseException {
    public GrpcUnauthenticatedException(String message) {
        super(Status.UNAUTHENTICATED, message, false);
    }

    public GrpcUnauthenticatedException(String message, Throwable cause) {
        super(Status.UNAUTHENTICATED, message, false, cause);
    }
}