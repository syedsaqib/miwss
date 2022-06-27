package com.miw.gildedrose.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miw.gildedrose.auth.SecurityContextService;
import com.miw.gildedrose.common.util.SecurityContextUtils;
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
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter class that reads Authorization: Bearer (token) from header.
 * Note: This filter is positioned before spring security's anonymous filter
 *
 * @author ssaqib
 * @since v0.1
 */
@Slf4j
public class GrAuthHeaderFilter extends AbstractAuthenticationProcessingFilter {
    private static final String BEARER = "bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public GrAuthHeaderFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    public GrAuthHeaderFilter(RequestMatcher requestMatcher) {
        super(requestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String authToken = readTokenFromAuthorizationHeader(request);

        try {
            Authentication authentication = SecurityContextUtils.buildGrAuthenticationToken(authToken);
            if (authentication != null) {
                return getAuthenticationManager().authenticate(authentication);
            }

            SecurityContextHolder.clearContext();
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
