package com.br.authenticator.dto;

import com.br.authenticator.enums.UserRole;
import com.br.authenticator.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponse {
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

    public UserResponse() {
    }

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.roles = user.getRoles();
        this.email = user.getEmail();
    }
}