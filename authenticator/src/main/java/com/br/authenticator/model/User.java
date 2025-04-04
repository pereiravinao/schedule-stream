package com.br.authenticator.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.br.authenticator.enums.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User  implements UserDetails {

    private String id;
    private String name;
    private String username;
    private String password;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private Set<UserRole> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.toString())).collect(Collectors.toList());
    }
}
