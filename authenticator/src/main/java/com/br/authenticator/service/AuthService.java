package com.br.authenticator.service;

import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.UserCreateParameter;
import com.br.authenticator.model.User;

public interface AuthService {
    UserTokenDTO login(String username, String password);

    User register(UserCreateParameter userCreateParameter);

    User refreshToken(String refreshToken);
}
