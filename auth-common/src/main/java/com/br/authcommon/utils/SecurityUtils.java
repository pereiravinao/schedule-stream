package com.br.authcommon.utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.br.authcommon.grpc.UserResponse;
import com.br.authcommon.security.UserAuthentication;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {
    
    public static void setUser(UserResponse user) {
        SecurityContextHolder.getContext().setAuthentication(
            new UserAuthentication(user)
        );
    }

    public static UserResponse getUser() {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User context not found. Make sure the user is authenticated.");
        }
        return authentication.getUser();
    }

    public static void clearUser() {
        SecurityContextHolder.clearContext();
    }

    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null 
            && SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }
} 