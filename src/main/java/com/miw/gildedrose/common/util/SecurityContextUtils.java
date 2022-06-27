package com.miw.gildedrose.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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

}
