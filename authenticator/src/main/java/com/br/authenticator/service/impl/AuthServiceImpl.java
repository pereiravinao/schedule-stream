package com.br.authenticator.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.UserCreateParameter;
import com.br.authenticator.enums.UserRole;
import com.br.authenticator.exception.AuthenticationException;
import com.br.authenticator.model.User;
import com.br.authenticator.service.AuthService;
import com.br.authenticator.service.RefreshTokenService;
import com.br.authenticator.service.TokenService;
import com.br.authenticator.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

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

            log.info("Login bem-sucedido para o usuário: {}", username);
            return userToken;
        }
        log.warn("Tentativa de login com credenciais inválidas para usuário: {}", username);
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

        log.info("Usuário registrado com sucesso: {}", savedUser.getUsername());
        return userToken;
    }

    @Override
    public UserTokenDTO refreshToken(String refreshToken) {
        try {
            if (!((TokenServiceJwtImpl) tokenService).validateRefreshToken(refreshToken)) {
                log.warn("Tentativa de refresh com token inválido ou expirado");
                throw AuthenticationException.invalidRefreshToken();
            }

            User user = ((TokenServiceJwtImpl) tokenService).extractUserFromRefreshToken(refreshToken);
            if (user == null) {
                log.warn("Usuário não encontrado para o refresh token fornecido");
                throw AuthenticationException.invalidRefreshToken();
            }

            refreshTokenService.revokeRefreshToken(refreshToken);
            log.info("Refresh token revogado durante processo de renovação: {}", refreshToken);

            user = userService.findById(user.getId());
            
            UserTokenDTO userToken = new UserTokenDTO(user);
            userToken.setToken(tokenService.generateToken(user));

            String newRefreshToken = tokenService.generateRefreshToken(user);
            userToken.setRefreshToken(newRefreshToken);
            
            log.info("Novos tokens gerados para o usuário: {}", user.getUsername());
            return userToken;
        } catch (Exception e) {
            log.error("Erro ao renovar token: {}", e.getMessage());
            throw AuthenticationException.invalidRefreshToken();
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
            throw AuthenticationException.invalidToken();
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
            throw AuthenticationException.invalidToken();
        }
    }

    private boolean isPasswordMatch(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}