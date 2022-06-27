package com.miw.gildedrose.model;

import com.miw.gildedrose.exception.GrErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDto {
    private String code;
    private String message;

    public ErrorDto(GrErrorCode errorCode) {
        this(errorCode.name(), errorCode.getMessage());
    }

}
