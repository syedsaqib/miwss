package com.miw.gildedrose.auth;

import com.miw.gildedrose.common.util.IdUtils;
import com.miw.gildedrose.model.GrUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.miw.gildedrose.common.util.IdUtils.doWithSyncMap;

/**
 * Class to hold user's tokens in an expiring map i.e. in-memory.
 * Not practical in production so this could be in future backed by hazelcast or redis.
 * OR
 * Better implement a JWT so all context remain on client end with jwt tokens
 *
 * @author ssaqib
 * @since v0.1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityContextService {

    /** auth token. default is 1 day */
    @Value("${gildedRose.authentication.tokenExpiryHours:24}")
    private long tokenExpireHours;

    /**
     * A temporary authentication token cache. Used common collections expiring map that expires after #tokenExpireHours
     */
    private Map<String, GildedRoseAuthenticationToken> tokenCache;

    @PostConstruct
    public void init() {
        // do it on bean initialization so property is available by then
        tokenCache = new PassiveExpiringMap<>(TimeUnit.HOURS.toMillis(tokenExpireHours));
    }

    /**
     * Creates a spring security context and put context in temp cache
     */
    public GrUser createNewSecurityContext(GrUser user) {
        String token = IdUtils.generateRandomUUIDNoDashes();

        user.setToken(token);
        user.setTokenCreatedAt(LocalDateTime.now());

        doWithSyncMap(tokenCache).put(token, creatAuthenticationContext(user));

        log.debug("--> creating spring context for user: {}", user.getUsername());
        log.debug("<-- auth token cache has now: {} tokens...", tokenCache.size());

        return user;
    }

    /**
     * Returns AuthenticationToken object from authorization token
     */
    public GildedRoseAuthenticationToken retrieveAuthenticationToken(String token) {
        return doWithSyncMap(tokenCache).get(token);
    }

    /**
     * to force log-off by removing token from cache
     *
     * @return true if token existed and removed
     */
    public boolean removeToken(String token) {
       return token != null && doWithSyncMap(tokenCache).remove(token) != null;
    }

    public GrUser logOff() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof GildedRoseAuthenticationToken) {
            String token = (String) authentication.getCredentials();

            removeToken(token);
            GrUser user = (GrUser) authentication.getPrincipal();
            return GrUser.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .build();

        }

        return null;
    }

    /**
     * retrieves all tokens of a specific user
     */
    public List<GrUser> retrieveAllTokensByUsername(String username) {
        return doWithSyncMap(tokenCache).values().stream()
                .filter(token -> (token.getPrincipal() instanceof GrUser) && username.equals(token.getName()))
                .map(token -> (GrUser) token.getPrincipal())
                .sorted(Comparator.comparing(GrUser::getTokenCreatedAt))
                .collect(Collectors.toList());
    }

    /**
     * clear all tokens of a specific user
     */
    public void removeAllUserTokens(String username) {
        Set<String> tokens = tokenCache.entrySet().stream()
                .filter(entry -> username.equals(entry.getValue().getName()))
                .map(Map.Entry::getKey).collect(Collectors.toSet());

        tokens.forEach(token -> doWithSyncMap(tokenCache).remove(token));
    }

    public GildedRoseAuthenticationToken creatAuthenticationContext(GrUser user) {
        GildedRoseAuthenticationToken auth = new GildedRoseAuthenticationToken(user);

        // set GildedRose authentication in spring context
        SecurityContextHolder.getContext().setAuthentication(auth);

        return auth;
    }

    // just a quick static class from Authentication
    public static class GildedRoseAuthenticationToken extends AbstractAuthenticationToken {
        private GrUser user;

        public GildedRoseAuthenticationToken(GrUser user) {
            this(user.getAuthorities());
            this.user = user;

            this.user.setPassword(null);
            // set isAuthenticated only true when token is set
            setAuthenticated(user.getToken() != null);
        }

        /**
         * Create authentication token with just bearer token only
         */
        public static GildedRoseAuthenticationToken fromBearerToken(String token) {
            GrUser user = GrUser.builder()
                            .token(token)
                            .role("ROLE_TEMPORARY") // just to fulfill role requirements
                            .build();

            return new GildedRoseAuthenticationToken(user);
        }

        /**
         * {@inheritDoc}
         */
        public GildedRoseAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
            super(authorities);
        }

        @Override
        public Object getCredentials() {
            // return token as credential
            return user.getToken();
        }

        /**
         * Return GrUser as authentication principal
         */
        @Override
        public Object getPrincipal() {
            return user;
        }
    }

}
