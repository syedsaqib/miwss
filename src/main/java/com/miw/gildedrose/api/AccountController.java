package com.miw.gildedrose.api;

import com.miw.gildedrose.exception.GildedRoseException;
import com.miw.gildedrose.model.GrUser;
import com.miw.gildedrose.model.request.LoginRequest;
import com.miw.gildedrose.model.response.GrResponse;
import com.miw.gildedrose.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AccountController {
    private final UserService userService;

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GrResponse<GrUser> login(@RequestBody LoginRequest request) throws GildedRoseException {
        GrUser user = userService.loginUser(request.getUsername(), request.getPassword());

        return GrResponse.success(user);
    }

    @RequestMapping(path = "/logoff", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE})
    public GrResponse<GrUser> logOff() throws GildedRoseException {
        GrUser user = userService.logOff();

        return user != null ? GrResponse.success(user) : GrResponse.failure(null);
    }
}
