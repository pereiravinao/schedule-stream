package com.br.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.br.app.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String>, JpaSpecificationExecutor<Appointment> {
} 