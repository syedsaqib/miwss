package com.miw.gildedrose.common.util;

import com.miw.gildedrose.auth.SecurityContextService.GildedRoseAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Map;

/**
 * Common spring security context utils
 *
 * @author ssaqib
 * @since v0.1
 */
public final class SecurityContextUtils {

    private SecurityContextUtils() {

    }

    /**
     * current logged in username
     */
    public static String getLoggedInUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        return null;
    }

    /**
     * Builds and return a new {@link GildedRoseAuthenticationToken} object.
     * This is used from spring security context's filter
     */
    public static GildedRoseAuthenticationToken buildGrAuthenticationToken(String token) {
        return token == null ? null : GildedRoseAuthenticationToken.fromBearerToken(token);
    }

}
