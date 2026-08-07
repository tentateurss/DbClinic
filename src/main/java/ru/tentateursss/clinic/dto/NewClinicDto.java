package ru.tentateursss.clinic.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewClinicDto {

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Адрес не может быть пустым")
    private String address;

    @NotBlank(message = "Номер телефона клиники не может быть пустым")
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Номер телефона введен некорректно")
    private String phone;

    @NotBlank(message = "Электронная почта клиники не может быть пустой")
    @Email(message = "Почта введена некорректно")
    private String email;

    @NotBlank(message = "ИНН не может быть пустым")
    @Pattern(regexp = "^\\d{12}$", message = "ИНН должен быть 12 символов")
    private String inn;
}
