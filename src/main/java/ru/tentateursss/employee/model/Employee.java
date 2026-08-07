package ru.tentateursss.employee.model;

import ru.tentateursss.enums.EmployeeRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.tentateursss.clinic.model.Clinic;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long id;

    @NotBlank(message = "ФИО сотрудника не может быть пустым")
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotBlank(message = "Номер сотрудника не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона введен некорректно")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Электронная почта сотрудника не может быть пустой")
    @Email(message = "Почта введена некорректно")
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EmployeeRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "license_number")
    private String licenseNumber;
}
