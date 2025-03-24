package com.br.authenticator.controller;


import com.br.authenticator.dto.UserTokenDTO;
import com.br.authenticator.dto.parameter.AuthParameter;
import com.br.authenticator.dto.parameter.UserCreateParameter;
import com.br.authenticator.dto.presenter.UserCreatePresenter;
import com.br.authenticator.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserTokenDTO> login(@RequestBody AuthParameter authParameter) {
        return ResponseEntity.ok(authService.login(authParameter.getUsername(), authParameter.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<UserCreatePresenter> register(@RequestBody UserCreateParameter parameter) {
        var user = authService.register(parameter);
        return ResponseEntity.ok(new UserCreatePresenter(user));
    }
}