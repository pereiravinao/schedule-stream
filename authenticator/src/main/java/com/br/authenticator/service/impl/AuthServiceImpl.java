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
        userToken.setRefreshToken(tokenService.generateRefreshToken(savedUser));
        
        return userToken;
    }

    @Override
    public UserTokenDTO refreshToken(String refreshToken) {
        try {
            // Primeiro validamos o token
            if (!tokenService.validateToken(refreshToken)) {
                throw AuthenticationException.invalidRefreshToken();
            }
            
            // Extraímos o usuário do token
            User userFromToken = tokenService.extractUser(refreshToken);
            if (userFromToken == null || userFromToken.getUsername() == null) {
                log.error("Não foi possível extrair o usuário do refresh token");
                throw AuthenticationException.invalidRefreshToken();
            }
            
            // Buscamos o usuário atualizado no banco
            User user = userService.findByUsername(userFromToken.getUsername());
            if (user == null) {
                log.error("Usuário {} não encontrado ao atualizar token", userFromToken.getUsername());
                throw AuthenticationException.invalidRefreshToken();
            }
            
            // Geramos novos tokens
            UserTokenDTO userToken = new UserTokenDTO(user);
            userToken.setToken(tokenService.generateToken(user));
            userToken.setRefreshToken(tokenService.generateRefreshToken(user));
            
            return userToken;
        } catch (Exception e) {
            log.error("Erro ao renovar token: {}", e.getMessage());
            throw AuthenticationException.invalidRefreshToken();
        }
    }

    @Override
    public UserTokenDTO validateToken(String token) {
        if (tokenService.validateToken(token)) {
            User user = tokenService.extractUser(token);
            if (user != null) {
                user = userService.findByUsername(user.getUsername());
                return new UserTokenDTO(user);
            }
        }
        throw AuthenticationException.invalidToken();
    }

    private boolean isPasswordMatch(String password, String encodedPassword) {
        return passwordEncoder.matches(password, encodedPassword);
    }
}