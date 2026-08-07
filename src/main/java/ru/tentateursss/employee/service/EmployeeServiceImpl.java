package ru.tentateursss.employee.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tentateursss.clinic.model.Clinic;
import ru.tentateursss.clinic.repository.ClinicRepository;
import ru.tentateursss.employee.dto.EmployeeDto;
import ru.tentateursss.employee.dto.NewEmployeeDto;
import ru.tentateursss.employee.mapper.EmployeeMapper;
import ru.tentateursss.employee.model.Employee;
import ru.tentateursss.employee.repository.EmployeeRepository;
import ru.tentateursss.enums.EmployeeRole;
import ru.tentateursss.exception.ConflictException;
import ru.tentateursss.exception.NotFoundException;
import ru.tentateursss.utils.Utils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ClinicRepository clinicRepository;

    @Override
    @Transactional
    public EmployeeDto createEmployee(NewEmployeeDto employeeDto) {
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new ConflictException("Работник с почтой " + employeeDto.getEmail() + " уже существует");
        }

        if (employeeRepository.existsByPhone(employeeDto.getPhone())) {
            throw new ConflictException("Работник с телефоном " + employeeDto.getPhone() + " уже существует");
        }

        if (employeeDto.getLicenseNumber() != null &&
                employeeRepository.existsByLicenseNumber(employeeDto.getLicenseNumber())) {
            throw new ConflictException("Работник с номером лицензии " + employeeDto.getLicenseNumber() + " уже существует");
        }

        Clinic clinic = clinicRepository.findById(employeeDto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + employeeDto.getClinicId() + " не найдена"));

        Employee employee = EmployeeMapper.toEntity(employeeDto, clinic);
        employee.setFullName(Utils.splitBio(employeeDto.getFullName()));
        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Создан работник: {} для клиники: {}", savedEmployee.getFullName(), clinic.getName());
        return EmployeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional
    public EmployeeDto updateEmployee(Long id, NewEmployeeDto dto) {
        Employee findEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Работник с ID " + id + " не найден"));

        if (!findEmployee.getPhone().equals(dto.getPhone()) &&
                employeeRepository.existsByPhone(dto.getPhone())) {
            throw new ConflictException("Работник с телефоном " + dto.getPhone() + " уже существует");
        }

        if (!findEmployee.getEmail().equals(dto.getEmail()) &&
                employeeRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Работник с email " + dto.getEmail() + " уже существует");
        }

        if (dto.getLicenseNumber() != null &&
                !findEmployee.getLicenseNumber().equals(dto.getLicenseNumber()) &&
                employeeRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new ConflictException("Работник с номером лицензии " + dto.getLicenseNumber() + " уже существует");
        }

        Clinic clinic = clinicRepository.findById(dto.getClinicId())
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + dto.getClinicId() + " не найдена"));

        EmployeeMapper.updateEmployee(findEmployee, dto, clinic);
        findEmployee.setFullName(Utils.splitBio(dto.getFullName()));
        Employee updatedEmployee = employeeRepository.save(findEmployee);

        log.info("Обновлен работник с ID: {}, FullName: {}", updatedEmployee.getId(), updatedEmployee.getFullName());
        return EmployeeMapper.toDto(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee findEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Работник с ID " + id + " не найден"));

        employeeRepository.delete(findEmployee);
        log.info("Удален работник с ID: {}, FullName: {}", id, findEmployee.getFullName());
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        Employee findEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Работник с ID " + id + " не найден"));

        log.debug("Получен работник с ID: {}", id);
        return EmployeeMapper.toDto(findEmployee);
    }

    @Override
    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);

        log.debug("Получение всех работников (страница {}, размер {})",
                pageable.getPageNumber(), pageable.getPageSize());
        return employees.map(EmployeeMapper::toDto);
    }

    @Override
    public List<EmployeeDto> getEmployeesByClinicId(Long clinicId) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        List<Employee> employees = employeeRepository.findByClinicId(clinicId);

        log.debug("Найдено {} работников для клиники с ID: {}", employees.size(), clinicId);
        return employees.stream()
                .map(EmployeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByRole(EmployeeRole role) {
        List<Employee> employees = employeeRepository.findByRole(role);

        log.debug("Найдено {} работников с ролью: {}", employees.size(), role);
        return employees.stream()
                .map(EmployeeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> getEmployeesByClinicIdAndRole(Long clinicId, EmployeeRole role) {
        clinicRepository.findById(clinicId)
                .orElseThrow(() -> new NotFoundException("Клиника с ID " + clinicId + " не найдена"));

        List<Employee> employees = employeeRepository.findByClinicIdAndRole(clinicId, role);

        log.debug("Найдено {} работников для клиники с ID: {} и ролью: {}", employees.size(), clinicId, role);
        return employees.stream()
                .map(EmployeeMapper::toDto)
                .collect(Collectors.toList());
    }
}