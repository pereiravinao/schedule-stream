package com.br.authenticator.dto.parameter;

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

    public User toModel() {
        var model = new User();
        model.setUsername(username);
        model.setPassword(password);
        model.setEmail(email);
        model.setPhoneNumber(phoneNumber);
        model.setName(name);
        return model;
    }

}
