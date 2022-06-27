package com.miw.gildedrose.exception;

public class ApiObjectNotFoundException extends GildedRoseException {

    public ApiObjectNotFoundException() {
        super(GrErrorCode.OBJECT_NOT_FOUND);
    }
}
