package ru.tentateursss.clinic.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClinicDto {
    private Long id;
    private String clinicCode;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String inn;
    private LocalDateTime createdAt;
}
