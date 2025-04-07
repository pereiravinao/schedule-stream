package com.br.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@ComponentScan(basePackages = {"com.br.app", "com.br.authcommon"})
public class AppointmentBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppointmentBookApplication.class, args);
	}

}
