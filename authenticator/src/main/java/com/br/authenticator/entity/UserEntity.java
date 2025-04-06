package com.br.authenticator.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.br.authcommon.enums.UserRole;
import com.br.authenticator.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "users")
public class UserEntity {
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("password")
    private String password;

    @Field("name")
    private String name;

    @Field("roles")
    private Set<UserRole> roles;

    @Field("phone_number")
    private String phoneNumber;

    @Field("email")
    private String email;

    @Field("createdAt")
    private String createdAt;

    @Field("updatedAt")
    private String updatedAt;

    public UserEntity() {
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.phoneNumber = user.getPhoneNumber();
        this.name = user.getName();
        this.email = user.getEmail();
        this.roles = user.getRoles();
        this.createdAt = String.valueOf(LocalDateTime.now());
    }

    public User toModel() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setPhoneNumber(this.phoneNumber);
        user.setEmail(this.email);
        user.setRoles(this.roles);
        user.setCreatedAt(LocalDateTime.parse(this.createdAt));
        return user;
    }
}
