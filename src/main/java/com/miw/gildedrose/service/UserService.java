package com.miw.gildedrose.service;

import com.miw.gildedrose.entity.GrUserEntity;
import com.miw.gildedrose.exception.ApiObjectNotFoundException;
import com.miw.gildedrose.exception.GildedRoseException;
import com.miw.gildedrose.exception.GrErrorCode;
import com.miw.gildedrose.exception.GrUnAuthorizedException;
import com.miw.gildedrose.model.GrUser;
import com.miw.gildedrose.repository.GrUserRepository;
import com.miw.gildedrose.auth.SecurityContextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User service to load user and login etc.
 *
 * @author ssaqib
 * @since v0.1
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final GrUserRepository userRepository;
    private final SecurityContextService securityContextService;

    public GrUser retrieveUserByUserName(String userName) throws GildedRoseException {
        try {
            GrUserEntity entity = userRepository.findByUserName(userName)
                                    .orElseThrow(ApiObjectNotFoundException::new);
            log.debug("--> retrieved user with username = {}", userName);

            return mapEntity(entity);
        } catch (ApiObjectNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("--> error while retrieving user with username = {}", userName, e);
            throw GildedRoseException.of(GrErrorCode.DATABASE_ERROR);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return retrieveUserByUserName(username);
        } catch (ApiObjectNotFoundException anf) {
            throw new UsernameNotFoundException(anf.getErrorCode().getMessage());
        }
    }

    public GrUser loginUser(String userName, String password) throws GildedRoseException {
        GrUserEntity entity = userRepository.findByUserName(userName)
                                .orElseThrow(GrUnAuthorizedException::new);

        log.debug("--> user: {} found. checking password...", userName);
        if (!passwordMatches(entity.getPassword(), password)) {
            throw new GrUnAuthorizedException();
        }

        GrUser user = mapEntity(entity);

        return securityContextService.createNewSecurityContext(user);
    }

    /**
     * Logs off current logged in user in spring security context
     */
    public GrUser logOff() {
        return securityContextService.logOff();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public GrUser createUser(GrUser user) throws GildedRoseException {
        // creates a new user by admin

        return user;
    }

    private boolean passwordMatches(String dbPassword, String inputPassword) {
        // we can use some encoder here later
        return dbPassword.equals(inputPassword);
    }

    private GrUser mapEntity(GrUserEntity entity) {
        return GrUser.builder()
                .id(entity.getId())
                .username(entity.getUserName())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .build();
    }

    private GrUserEntity mapModel(GrUser user) {
        return user == null ? null
                : GrUserEntity.builder()
                   .id(user.getId())
                    .userName(user.getUsername())
                    .password(user.getPassword()) // encrypt it later
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .build();
    }

}
