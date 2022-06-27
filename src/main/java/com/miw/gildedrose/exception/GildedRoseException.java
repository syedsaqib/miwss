package com.miw.gildedrose.exception;

import lombok.Getter;

public class GildedRoseException extends RuntimeException {
    @Getter
    private GrErrorCode errorCode;

    public GildedRoseException() {
        super();
    }

    public GildedRoseException(String message) {
        super(message);
    }

    public GildedRoseException(GrErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public static GildedRoseException of(GrErrorCode errorCode) {
        return new GildedRoseException(errorCode);
    }

}
