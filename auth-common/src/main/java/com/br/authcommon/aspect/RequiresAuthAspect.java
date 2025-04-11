package com.br.authcommon.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.br.authcommon.annotation.RequiresAuth;
import com.br.authcommon.grpc.UserResponse;
import com.br.authcommon.service.AuthGrpcClientService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class RequiresAuthAspect {

    private static final Logger logger = LoggerFactory.getLogger(RequiresAuthAspect.class);
    private final AuthGrpcClientService authGrpcClientService;

    @Around("@annotation(com.br.authcommon.annotation.RequiresAuth)")
    public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RequiresAuth methodAnnotation = signature.getMethod().getAnnotation(RequiresAuth.class);
        RequiresAuth classAnnotation = (RequiresAuth) signature.getDeclaringType().getAnnotation(RequiresAuth.class);

        RequiresAuth requiresAuth = methodAnnotation != null ? methodAnnotation : classAnnotation;

        if (requiresAuth == null || !requiresAuth.authenticated()) {
            return joinPoint.proceed();
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Não foi possível obter o contexto da requisição");
        }

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        if (response == null) {
            throw new IllegalStateException("Não foi possível obter a resposta da requisição");
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token não fornecido");
            return null;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        UserResponse userResponse = authGrpcClientService.validateToken(token);
        if (userResponse == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return null;
        }
        try {
            request.setAttribute("userEmail", userResponse.getEmail());
            request.setAttribute("userRoles", userResponse.getRolesList());
            request.setAttribute("username", userResponse.getUsername());
            request.setAttribute("phoneNumber", userResponse.getPhoneNumber());
            return joinPoint.proceed();
        } catch (Exception e) {
            logger.error("Erro ao validar token para a requisição: " + request.getRequestURI(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Erro ao validar token: " + e.getMessage());
            return null;
        }
    }
}