package ru.tentateursss.employee.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tentateursss.enums.EmployeeRole;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEmployeeDto {

    @NotBlank(message = "ФИО сотрудника не может быть пустым")
    private String fullName;

    @NotBlank(message = "Номер сотрудника не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона введен некорректно")
    private String phone;

    @NotBlank(message = "Электронная почта сотрудника не может быть пустой")
    @Email(message = "Почта введена некорректно")
    private String email;

    @NotNull(message = "Дата устройства на работу должна быть указана")
    @Past(message = "Дата устройства должна быть в прошлом")
    private LocalDate hireDate;

    @NotNull(message = "Роль должна быть указана")
    private EmployeeRole role;

    @NotNull(message = "ID клиники обязателен")
    private Long clinicId;

    private String specialization;

    private String licenseNumber;
}
