package com.br.authenticator.dto.parameter;

import java.util.HashSet;
import java.util.Set;

import com.br.authenticator.enums.UserRole;
import com.br.authenticator.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateParameter {
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private String name;
    private Set<UserRole> roles;

    public User toModel() {
        var model = new User();
        model.setUsername(username);
        model.setPassword(password);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);
        model.setName(name);
        
        // Definir roles padrão se não fornecidas
        if (roles == null || roles.isEmpty()) {
            Set<UserRole> defaultRoles = new HashSet<>();
            defaultRoles.add(UserRole.USER);
            model.setRoles(defaultRoles);
        } else {
            model.setRoles(roles);
        }
        
        return model;
    }

}
