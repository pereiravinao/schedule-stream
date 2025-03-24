package com.br.authenticator.dto;

import com.br.authenticator.enums.UserRole;
import com.br.authenticator.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserTokenDTO {
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private String phoneNumber;
    private Set<UserRole> roles;

    public User toModel() {
        var model = new User();
        model.setUsername(username);
        model.setPhoneNumber(phoneNumber);
        model.setRoles(roles);
        model.setEmail(email);
        return model;
    }

    public UserTokenDTO() {
    }

    public UserTokenDTO(User user) {
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.roles = user.getRoles();
        this.email = user.getEmail();
    }
    
    public UserResponse toResponse() {
        UserResponse response = new UserResponse();
        response.setUsername(username);
        response.setEmail(email);
        response.setPhoneNumber(phoneNumber);
        response.setRoles(roles);
        return response;
    }
} 