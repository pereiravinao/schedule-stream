package com.br.authenticator.service;


import com.br.authenticator.model.User;

public interface UserService {

    User findByUsername(String username);

    User save(User user);

}