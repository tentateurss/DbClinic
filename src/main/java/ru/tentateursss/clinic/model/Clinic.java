package ru.tentateursss.clinic.model;

import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.enums.EmployeeRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.tentateursss.medicalservice.model.MedicalService;
import ru.tentateursss.appointment.model.Appointment;
import org.hibernate.annotations.CreationTimestamp;
import ru.tentateursss.patient.model.Patient;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "clinic")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private Long id;

    @NotBlank(message = "Код клиники обязателен")
    @Column(name = "clinic_code", nullable = false, length = 255)
    private String clinicCode;

    @NotBlank(message = "Название не может быть пустым")
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Адрес не может быть пустым")
    @Column(nullable = false, length = 255)
    private String address;

    @NotBlank(message = "Номер телефона клиники не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона введен некорректно")
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Электронная почта клиники не может быть пустой")
    @Email(message = "Почта введена некорректно")
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank(message = "ИНН не может быть пустым")
    @Pattern(regexp = "^\\d{12}$", message = "ИНН должен быть 12 символов")
    @Column(nullable = false, length = 12)
    private String inn;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Patient> patients = new ArrayList<>();

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "clinic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MedicalService> medicalServices = new ArrayList<>();

    public List<Employee> getDoctors() {
        return employees.stream()
                .filter(e -> e.getRole().equals(EmployeeRole.DOCTOR))
                .collect(Collectors.toList());
    }

    public List<Employee> getByRole(EmployeeRole role) {
        return employees.stream()
                .filter(e -> e.getRole().equals(role))
                .collect(Collectors.toList());
    }
}
