package com.br.authenticator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
public class AuthenticatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticatorApplication.class, args);
    }

}
