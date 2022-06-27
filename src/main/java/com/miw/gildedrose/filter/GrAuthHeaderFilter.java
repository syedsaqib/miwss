package com.miw.gildedrose.filter;

import com.miw.gildedrose.auth.SecurityContextService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter class that reads Authorization: Bearer (token) from header
 *
 * @author ssaqib
 * @since v0.1
 */
public class GrAuthHeaderFilter extends AbstractAuthenticationProcessingFilter {
    private static final String BEARER = "bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final SecurityContextService securityContextService;

    public GrAuthHeaderFilter(String defaultFilterProcessesUrl, SecurityContextService securityContextService) {
        super(defaultFilterProcessesUrl);
        this.securityContextService = securityContextService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String authToken = readTokenFromAuthorizationHeader(request);

        if (authToken != null && !authToken.isEmpty()) {
            Authentication authentication = securityContextService.retrieveAuthenticationToken(authToken);
            if (authentication != null) {
                Authentication  authenticatedAuth = getAuthenticationManager().authenticate(authentication);
                // put in spring context
                SecurityContextHolder.getContext().setAuthentication(authenticatedAuth);
            }
        }

        return null;
    }

    private String readTokenFromAuthorizationHeader(HttpServletRequest request) {
        String authToken = request.getHeader(AUTHORIZATION_HEADER);
        if (authToken != null) {
            if (authToken.toLowerCase().startsWith(BEARER)) {
                return authToken.substring(BEARER.length());
            }
            return authToken;
        }

        return null;
    }
}
