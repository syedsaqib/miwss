package com.miw.gildedrose.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miw.gildedrose.auth.SecurityContextService;
import com.miw.gildedrose.exception.GrErrorCode;
import com.miw.gildedrose.model.ErrorDto;
import com.miw.gildedrose.model.response.GrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
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
@Slf4j
public class GrAuthHeaderFilter extends AbstractAuthenticationProcessingFilter {
    private static final String BEARER = "bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final SecurityContextService securityContextService;

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public GrAuthHeaderFilter(String defaultFilterProcessesUrl, SecurityContextService securityContextService) {
        super(defaultFilterProcessesUrl);
        this.securityContextService = securityContextService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String authToken = readTokenFromAuthorizationHeader(request);

        try {
            if (authToken != null && !authToken.isEmpty()) {
                Authentication authentication = securityContextService.retrieveAuthenticationToken(authToken);
                if (authentication != null) {
                    return getAuthenticationManager().authenticate(authentication);
                }
                SecurityContextHolder.clearContext();
            }
            throw new BadCredentialsException(GrErrorCode.INVALID_AUTH_TOKEN.name());
        } catch (AuthenticationException ae) {
            log.error("--> invalid authentication token in header: {}", ae.getMessage());
            sendUnAuthorizedErrorAsJson(response);
        }

        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // put Authentication result in spring security context
        SecurityContextHolder.getContext().setAuthentication(authResult);

        chain.doFilter(request, response);
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

    /**
     * We are not in controller that will use controller advice to throw proper json
     */
    private static void sendUnAuthorizedErrorAsJson(HttpServletResponse response) throws IOException {
        GrResponse<ErrorDto> responseObject = GrResponse.failure(new ErrorDto(GrErrorCode.INVALID_AUTH_TOKEN));
        String jsonText = jsonMapper.writeValueAsString(responseObject);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonText);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
