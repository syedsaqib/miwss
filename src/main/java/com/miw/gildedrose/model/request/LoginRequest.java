package com.miw.gildedrose.model.request;

import lombok.Builder;
import lombok.Data;

/**
 * Request class for login
 *
 * @author ssaqib
 * @since v0.1
 */
@Data
@Builder
public class LoginRequest {

    private String username;
    private String password;
}
