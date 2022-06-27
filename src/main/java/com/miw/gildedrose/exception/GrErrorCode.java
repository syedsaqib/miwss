package com.miw.gildedrose.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum GrErrorCode {
    OBJECT_NOT_FOUND("Object not found"),
    UNKNOWN_ERROR("Unknown Error"),
    DATABASE_ERROR("Database Error"),
    INVALID_LOGIN("UserName or password is invalid!"),
    INVALID_AUTH_TOKEN("Authorization token is invalid or expired!"),
    ACCESS_DENIED("Resource is forbidden"),
    INSUFFICIENT_QUANTITY("Item quantity is not available")
    ;

    @Getter
    private final String message;
}
