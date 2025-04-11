package com.br.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.app.model.Appointment;
import com.br.app.service.AppointmentService;
import com.br.authcommon.annotation.RequiresAuth;
import com.br.authcommon.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/appointments")
@Slf4j
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    @RequiresAuth(authenticated = true)
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        var user = SecurityUtils.getUser();
        log.info("User: {}", user);
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

}
