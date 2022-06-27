package com.miw.gildedrose.service;

import com.miw.gildedrose.auth.SecurityContextService;
import com.miw.gildedrose.common.util.SecurityContextUtils;
import com.miw.gildedrose.exception.GildedRoseException;
import com.miw.gildedrose.model.GrUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for admin related APIs
 *
 * @author ssaqib
 * @since v0.2
 */
@Service
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminService {

    private final ItemClickBucketProvider itemClickBucketProvider;
    private final SecurityContextService securityContextService;

    public Integer retrieveItemClicks(String itemId) throws GildedRoseException {
        Integer clickCount = itemClickBucketProvider.retrieveItemClicks(itemId);

        log.debug("---> Admin {} retrieving item: {}'s clicks = {}",
                SecurityContextUtils.getLoggedInUsername(), itemId, clickCount);

        return clickCount;
    }

    public List<GrUser> retrieveAuthenticationByUsername(String username) {
        log.debug("--> admin: {} trying to retrieve tokens for user: {}",
                            SecurityContextUtils.getLoggedInUsername(), username);
        return securityContextService.retrieveAllTokensByUsername(username);
    }

    public boolean expireToken(String bearerToken) throws GildedRoseException {
        return securityContextService.removeToken(bearerToken);
    }
}
