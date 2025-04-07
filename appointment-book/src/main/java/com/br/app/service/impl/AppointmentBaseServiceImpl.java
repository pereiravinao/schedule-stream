package com.br.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.br.app.model.Appointment;
import com.br.app.repository.AppointmentRepository;
import com.br.app.service.AppointmentService;

@Service
public class AppointmentBaseServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentBaseServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
}