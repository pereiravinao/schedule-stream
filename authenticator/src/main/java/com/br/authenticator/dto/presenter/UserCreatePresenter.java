package com.br.authenticator.dto.presenter;

import com.br.authenticator.enums.UserRole;
import com.br.authenticator.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserCreatePresenter {
    private String username;
    private String phoneNumber;
    private String email;
    private String name;
    private Set<UserRole> roles;
    private LocalDateTime createdAt;

    public UserCreatePresenter(User user) {
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.name = user.getName();
        this.roles = user.getRoles();
        this.createdAt = user.getCreatedAt();
    }
}
