package com.br.authenticator.exception.grpc;

import io.grpc.Status;

public class GrpcBaseException extends RuntimeException {
    private final Status status;
    private final boolean retryable;

    public GrpcBaseException(Status status, String message, boolean retryable) {
        super(message);
        this.status = status.withDescription(message);
        this.retryable = retryable;
    }

    public GrpcBaseException(Status status, String message, boolean retryable, Throwable cause) {
        super(message, cause);
        this.status = status.withDescription(message);
        this.retryable = retryable;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isRetryable() {
        return retryable;
    }
}