package com.miw.gildedrose.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
@Slf4j
public class GilderRosesAuthenticationProvider implements AuthenticationProvider {
    private final SecurityContextService securityContextService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication != null && supports((authentication.getClass()))) {
            // our custom authentication token has authentication token in credentials
            String token = (String) authentication.getCredentials();
            // just to make sure it is not still expired from cache
            return securityContextService.retrieveAuthenticationToken(token);
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SecurityContextService.GildedRoseAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
