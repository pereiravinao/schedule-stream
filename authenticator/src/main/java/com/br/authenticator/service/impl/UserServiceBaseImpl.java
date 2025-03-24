package com.br.authenticator.service.impl;


import com.br.authenticator.entity.UserEntity;
import com.br.authenticator.exception.CustomException;
import com.br.authenticator.model.User;
import com.br.authenticator.repository.UserRepository;
import com.br.authenticator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceBaseImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserEntity::toModel)
                .orElseThrow(() -> new RuntimeException("Username or Password is incorrect"));
    }

    @Override
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new CustomException("E-mail já cadastrado", 400);
        }
        return userRepository.save(new UserEntity(user)).toModel();
    }

}