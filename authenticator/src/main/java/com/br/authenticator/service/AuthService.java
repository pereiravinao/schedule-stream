package com.br.authenticator.service;

import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.UserCreateParameter;

public interface AuthService {
    UserTokenDTO login(String username, String password);

    UserTokenDTO register(UserCreateParameter userCreateParameter);

    UserTokenDTO refreshToken(String refreshToken);
    
    UserTokenDTO validateToken(String token);
}
