package com.br.authenticator.exception.grpc;

import io.grpc.Status;

public class GrpcNotFoundException extends GrpcBaseException {
    public GrpcNotFoundException(String message) {
        super(Status.NOT_FOUND, message, false);
    }

    public GrpcNotFoundException(String message, Throwable cause) {
        super(Status.NOT_FOUND, message, false, cause);
    }
}