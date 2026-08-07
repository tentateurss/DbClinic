package ru.tentateursss.appointment.model;

import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import ru.tentateursss.medicalservice.model.MedicalService;
import org.hibernate.annotations.CreationTimestamp;
import ru.tentateursss.patient.model.Patient;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_service_id")
    private MedicalService medicalService;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AppointmentStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    public int getCost() {
        return medicalService == null ? 0 : medicalService.getCost();
    }
}
