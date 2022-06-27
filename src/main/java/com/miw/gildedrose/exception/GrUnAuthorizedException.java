package com.miw.gildedrose.exception;

/**
 * Exception for invalid login to throw 401 back
 *
 * @author ssaqib
 * @since v0.1
 */
public class GrUnAuthorizedException extends GildedRoseException {

    public GrUnAuthorizedException() {
        this(GrErrorCode.INVALID_LOGIN);
    }

    public GrUnAuthorizedException(GrErrorCode errorCode) {
        super(errorCode);
    }

    public static GrUnAuthorizedException of(GrErrorCode errorCode) {
        return new GrUnAuthorizedException(errorCode);
    }
}
