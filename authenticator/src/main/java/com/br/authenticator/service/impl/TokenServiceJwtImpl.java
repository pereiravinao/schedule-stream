package com.br.authenticator.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.br.authenticator.enums.UserRole;
import com.br.authenticator.exception.TokenException;
import com.br.authenticator.model.User;
import com.br.authenticator.service.TokenService;
import com.br.authenticator.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenServiceJwtImpl implements TokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Autowired
    private UserService userService;

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            if (user.getRoles() != null) {
                List<String> roleNames = user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList());
                claims.put("roles", roleNames);
            }

            claims.put("email", user.getEmail());
            claims.put("phoneNumber", user.getPhoneNumber());
            claims.put("userId", user.getId());
        }
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User user) {
            claims.put("userId", user.getId());
        }
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Override
    public User extractUser(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String username = claims.getSubject();

            if (username != null && claims.get("userId") == null) {
                return userService.findByUsername(username);
            }

            User user = new User();
            user.setUsername(username);

            getClaimAsString(claims, "email").ifPresent(user::setEmail);
            getClaimAsString(claims, "phoneNumber").ifPresent(user::setPhoneNumber);
            getClaimAsString(claims, "userId").ifPresent(user::setId);

            extractRoleNamesFromClaims(claims).map(this::convertToUserRoles).ifPresent(user::setRoles);

            return user;
        } catch (Exception e) {
            log.warn("Erro ao extrair usuário do token: {}", e.getMessage());
            return null;
        }
    }

    private Optional<String> getClaimAsString(Claims claims, String claimName) {
        return Optional.ofNullable(claims.get(claimName))
                .map(Object::toString);
    }

    private Optional<List<String>> extractRoleNamesFromClaims(Claims claims) {
        try {
            if (claims.get("roles") == null) {
                return Optional.empty();
            }

            List<String> roleNames = (List<String>) claims.get("roles");

            return Optional.of(roleNames);
        } catch (ClassCastException e) {
            log.warn("Formato inválido para roles no token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Set<UserRole> convertToUserRoles(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Collections.emptySet();
        }

        return roleNames.stream()
                .map(this::safeRoleConversion)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    private Optional<UserRole> safeRoleConversion(String roleName) {
        try {
            return Optional.of(UserRole.valueOf(roleName));
        } catch (IllegalArgumentException e) {
            log.warn("Role inválida encontrada no token: {}", roleName);
            return Optional.empty();
        }
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw TokenException.expired();
        } catch (SignatureException e) {
            throw TokenException.invalid();
        } catch (MalformedJwtException e) {
            throw TokenException.malformed();
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            throw TokenException.invalid();
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw TokenException.expired();
        } catch (SignatureException e) {
            throw TokenException.invalid();
        } catch (MalformedJwtException e) {
            throw TokenException.malformed();
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            throw TokenException.invalid();
        } catch (Exception e) {
            return false;
        }
    }
}
