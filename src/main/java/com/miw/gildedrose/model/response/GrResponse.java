package com.miw.gildedrose.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic response class for Gilded Rose application
 *
 * @author ssaqib
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(value = {"status", "body"})
public class GrResponse<T> {
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE   = "FAILURE";

    private T body;
    private String status = SUCCESS;

    public static <T> GrResponse<T> success(T body) {
        return GrResponse.<T>builder()
                .status(SUCCESS)
                .body(body)
                .build();
    }

    public static <T> GrResponse<T> failure(T body) {
        return GrResponse.<T>builder()
                .status(FAILURE)
                .body(body)
                .build();
    }

}
