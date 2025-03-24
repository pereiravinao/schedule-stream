package com.br.authenticator.service.impl;

import com.br.authenticator.enums.UserRole;
import com.br.authenticator.exception.TokenException;
import com.br.authenticator.model.RefreshToken;
import com.br.authenticator.model.User;
import com.br.authenticator.service.RefreshTokenService;
import com.br.authenticator.service.TokenService;
import com.br.authenticator.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    @Autowired
    private RefreshTokenService refreshTokenService;

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
        if (userDetails instanceof User user) {
            // Criar e persistir o refresh token no banco
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            
            // Retorna o token gerado
            return refreshToken.getToken();
        }
        
        return null;
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

    public User extractUserFromRefreshToken(String refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken);
        if (token != null && !token.isRevoked()) {
            String userId = token.getUserId();
            try {
                return userService.findById(userId);
            } catch (Exception e) {
                log.error("Erro ao buscar usuário do refresh token: {}", e.getMessage());
                return null;
            }
        }
        return null;
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
        } catch (SignatureException | IllegalArgumentException e) {
            throw TokenException.invalid();
        } catch (MalformedJwtException e) {
            throw TokenException.malformed();
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
        } catch (SignatureException | IllegalArgumentException e) {
            throw TokenException.invalid();
        } catch (MalformedJwtException e) {
            throw TokenException.malformed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Valida um refresh token verificando no banco de dados
     * 
     * @param token O refresh token
     * @return true se válido, false caso contrário
     */
    public boolean validateRefreshToken(String token) {
        return refreshTokenService.validateRefreshToken(token);
    }
}
