package com.br.authenticator.service.impl;

import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.UserCreateParameter;
import com.br.authenticator.exception.AuthenticationException;
import com.br.authenticator.model.User;
import com.br.authenticator.service.AuthService;
import com.br.authenticator.service.TokenService;
import com.br.authenticator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserTokenDTO login(String username, String password) {
        var user = userService.findByUsername(username);
        if (isPasswordMatch(password, user.getPassword())) {
            var userToken = new UserTokenDTO(user);
            userToken.setToken(tokenService.generateToken(user));
            userToken.setRefreshToken(tokenService.generateRefreshToken(user));
            return userToken;
        }
        throw AuthenticationException.invalidCredentials();
    }

    @Override
    public User register(UserCreateParameter userCreateParameter) {
        var user = userCreateParameter.toModel();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.save(user);
    }

    @Override
    public User refreshToken(String refreshToken) {
        String username = tokenService.extractUsername(refreshToken);
        if (username != null && tokenService.validateToken(refreshToken)) {
            return userService.findByUsername(username);
        }
        throw AuthenticationException.invalidRefreshToken();
    }

    private boolean isPasswordMatch(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}