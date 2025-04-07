package com.br.app.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    private String id;
    private String userId;
    private LocalDateTime dateTime;
    private String description;
    private String status;
}