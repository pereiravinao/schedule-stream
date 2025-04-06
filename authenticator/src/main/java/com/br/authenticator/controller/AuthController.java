package com.br.authenticator.controller;

import com.br.authenticator.dto.UserResponse;
import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.AuthParameter;
import com.br.authenticator.dto.parameter.UserCreateParameter;
import com.br.authenticator.service.AuthService;
import com.br.authenticator.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    RefreshTokenService refreshTokenService;

    private static final int COOKIE_MAX_AGE = 7 * 24 * 60 * 60; // 7 dias em segundos
    private static final String TOKEN_COOKIE_NAME = "auth_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody AuthParameter authParameter) {
        UserTokenDTO userToken = authService.login(authParameter.getUsername(), authParameter.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createTokenCookie(userToken.getToken()).toString());
        headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(userToken.getRefreshToken()).toString());

        UserResponse response = userToken.toResponse();

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserCreateParameter parameter) {
        UserTokenDTO userToken = authService.register(parameter);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createTokenCookie(userToken.getToken()).toString());
        headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(userToken.getRefreshToken()).toString());

        UserResponse response = userToken.toResponse();

        return ResponseEntity.ok().headers(headers).body(response);
    }

//    @PostMapping("/validate")
//    public ResponseEntity<UserResponse> validateToken(
//            @CookieValue(value = TOKEN_COOKIE_NAME, required = false) String cookieToken) {
//
//        if (cookieToken == null) {
//            throw new IllegalArgumentException("Token não fornecido");
//        }
//
//        UserTokenDTO userToken = authService.validateToken(cookieToken);
//        UserResponse response = userToken.toResponse();
//        log.debug("Token validado para o usuário {}", response.getUsername());
//
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping("/refresh")
//    public ResponseEntity<UserResponse> refreshToken(
//            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String cookieRefreshToken) {
//
//        if (cookieRefreshToken == null) {
//            throw new IllegalArgumentException("Refresh token não fornecido");
//        }
//
//        UserTokenDTO userToken = authService.refreshToken(cookieRefreshToken);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.SET_COOKIE, createTokenCookie(userToken.getToken()).toString());
//        headers.add(HttpHeaders.SET_COOKIE, createRefreshTokenCookie(userToken.getRefreshToken()).toString());
//
//        UserResponse response = userToken.toResponse();
//
//        return ResponseEntity.ok().headers(headers).body(response);
//    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) String cookieRefreshToken) {

        if (cookieRefreshToken != null) {
            refreshTokenService.revokeRefreshToken(cookieRefreshToken);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createExpiredCookie(TOKEN_COOKIE_NAME).toString());
        headers.add(HttpHeaders.SET_COOKIE, createExpiredCookie(REFRESH_TOKEN_COOKIE_NAME).toString());

        return ResponseEntity.ok().headers(headers).build();
    }

    private ResponseCookie createTokenCookie(String token) {
        ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .sameSite("Strict")
                .build();
        return cookie;
    }

    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(COOKIE_MAX_AGE)
                .sameSite("Strict")
                .build();
        return cookie;
    }

    private ResponseCookie createExpiredCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return cookie;
    }
}