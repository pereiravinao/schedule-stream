package com.br.authenticator.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.br.authcommon.enums.UserRole;
import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.UserCreateParameter;
import com.br.authenticator.exception.AuthenticationException;
import com.br.authenticator.exception.grpc.GrpcUnauthenticatedException;
import com.br.authenticator.model.User;
import com.br.authenticator.service.AuthService;
import com.br.authenticator.service.RefreshTokenService;
import com.br.authenticator.service.TokenService;
import com.br.authenticator.service.UserService;

import com.br.authcommon.grpc.AuthServiceGrpc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl extends AuthServiceGrpc.AuthServiceImplBase implements AuthService {

    private final TokenService tokenService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserTokenDTO login(String username, String password) {
        var user = userService.findByUsername(username);
        if (isPasswordMatch(password, user.getPassword())) {
            var userToken = new UserTokenDTO(user);

            userToken.setToken(tokenService.generateToken(user));

            String refreshTokenValue = tokenService.generateRefreshToken(user);
            userToken.setRefreshToken(refreshTokenValue);

            return userToken;
        }
        throw AuthenticationException.invalidCredentials();
    }

    @Override
    public UserTokenDTO register(UserCreateParameter userCreateParameter) {
        var user = userCreateParameter.toModel();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<UserRole> defaultRoles = new HashSet<>();
            defaultRoles.add(UserRole.USER);
            user.setRoles(defaultRoles);
        }

        User savedUser = userService.save(user);

        UserTokenDTO userToken = new UserTokenDTO(savedUser);

        userToken.setToken(tokenService.generateToken(savedUser));

        String refreshTokenValue = tokenService.generateRefreshToken(savedUser);
        userToken.setRefreshToken(refreshTokenValue);

        return userToken;
    }

    @Override
    public UserTokenDTO refreshToken(String refreshToken) {
        try {
            if (!((TokenServiceJwtImpl) tokenService).validateRefreshToken(refreshToken)) {
                throw new GrpcUnauthenticatedException("Token de atualização inválido ou expirado");
            }

            User user = ((TokenServiceJwtImpl) tokenService).extractUserFromRefreshToken(refreshToken);
            if (user == null) {
                throw new GrpcUnauthenticatedException("Token de atualização inválido ou expirado");
            }

            refreshTokenService.revokeRefreshToken(refreshToken);

            user = userService.findById(user.getId());

            UserTokenDTO userToken = new UserTokenDTO(user);
            userToken.setToken(tokenService.generateToken(user));

            String newRefreshToken = tokenService.generateRefreshToken(user);
            userToken.setRefreshToken(newRefreshToken);

            return userToken;
        } catch (Exception e) {
            throw new GrpcUnauthenticatedException("Token de atualização inválido ou expirado");
        }
    }

    @Override
    public UserTokenDTO validateToken(String token) {
        try {
            if (tokenService.validateToken(token)) {
                User user = tokenService.extractUser(token);
                if (user != null) {
                    user = userService.findByUsername(user.getUsername());
                    return new UserTokenDTO(user);
                }
            }
            throw new GrpcUnauthenticatedException("Token inválido ou expirado");
        } catch (Exception e) {
            throw new GrpcUnauthenticatedException("Token inválido ou expirado");
        }
    }

    private boolean isPasswordMatch(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }

}
