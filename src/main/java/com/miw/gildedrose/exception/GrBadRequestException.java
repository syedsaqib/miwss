package com.miw.gildedrose.exception;

/**
 * Exception to throw in case of invalid input
 *
 * @author ssaqib
 * @since v0.1
 */
public class GrBadRequestException extends GildedRoseException {
    public GrBadRequestException(GrErrorCode errorCode) {
        super(errorCode);
    }

    public static GrBadRequestException of(GrErrorCode errorCode) {
        return new GrBadRequestException(errorCode);
    }
}
