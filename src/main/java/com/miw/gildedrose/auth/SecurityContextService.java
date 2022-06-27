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

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    private final Map<String, GildedRoseAuthenticationToken> tokenCache = new PassiveExpiringMap<>(TimeUnit.HOURS.toMillis(tokenExpireHours));

    /**
     * Creates a spring security context and put context in temp cache
     */
    public GrUser createNewSecurityContext(GrUser user) {
        String token = IdUtils.generateRandomUUIDNoDashes();

        user.setToken(token);
        user.setLastAccessed(LocalDateTime.now());

        tokenCache.put(token, creatAuthenticationContext(user));

        return user;
    }

    /**
     * Returns AuthenticationToken object from authorization token
     */
    public GildedRoseAuthenticationToken retrieveAuthenticationToken(String token) {
        return tokenCache.get(token);
    }

    /**
     * to force log-off by removing token from cache
     *
     * @return true if token existed and removed
     */
    public boolean removeToken(String token) {
       return token != null && tokenCache.remove(token) != null;
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
     * clear all tokens of a specific user
     */
    public void removeAllUserTokens(String username) {
        Set<String> tokens = tokenCache.entrySet().stream()
                .filter(entry -> username.equals(entry.getValue().getName()))
                .map(Map.Entry::getKey).collect(Collectors.toSet());

        tokens.forEach(tokenCache::remove);
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
