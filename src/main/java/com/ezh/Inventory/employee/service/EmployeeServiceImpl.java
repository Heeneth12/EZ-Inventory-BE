package com.ezh.Inventory.employee.service;

import com.ezh.Inventory.contacts.dto.AddressDto;
import com.ezh.Inventory.contacts.entiry.Address;
import com.ezh.Inventory.contacts.repository.AddressRepository;
import com.ezh.Inventory.employee.dto.EmployeeDto;
import com.ezh.Inventory.employee.dto.EmployeeFilter;
import com.ezh.Inventory.employee.entity.Employee;
import com.ezh.Inventory.employee.repository.EmployeeRepository;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public CommonResponse createEmployee(EmployeeDto employeeDto) throws CommonException {
        log.info("Creating Employee {}", employeeDto);

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDto, employee);

        employeeRepository.save(employee);
        return CommonResponse
                .builder()
                .status(Status.SUCCESS)
                .message("Employee Created Successfully")
                .build();
    }


    @Override
    @Transactional
    public CommonResponse updateEmployee(Long id, EmployeeDto employeeDto) throws CommonException {
        log.info("Updating Employee {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Employee Not Found"));

        // Copy main employee fields except address
        BeanUtils.copyProperties(employeeDto, employee, "address", "id", "uuid");

        // Update address separately
        updateEmployeeAddress(employee, employeeDto.getAddress());

        employeeRepository.save(employee);

        return CommonResponse.builder()
                .status(Status.SUCCESS)
                .message("Employee Updated Successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto getEmployee(Long id) throws CommonException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Employee Not Found"));

        EmployeeDto employeeDto = new EmployeeDto();
        BeanUtils.copyProperties(employee, employeeDto);
        return employeeDto;
    }



    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDto> getAllEmployees(EmployeeFilter filter, Integer page, Integer size) throws CommonException {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Employee> employees = employeeRepository.findAll(pageable);

        return employees.map(employee -> {
            EmployeeDto dto = new EmployeeDto();
            BeanUtils.copyProperties(employee, dto);
            return dto;
        });
    }


    @Override
    @Transactional
    public CommonResponse toggleStatus(Long id, Boolean active) throws CommonException {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Employee Not Found"));

        employee.setActive(active);
        employeeRepository.save(employee);
        return CommonResponse
                .builder()
                .status(Status.SUCCESS)
                .message("Status Updated Successfully")
                .build();
    }


    /**
     * Private helper method to update Address of Employee
     */
    private void updateEmployeeAddress(Employee employee, AddressDto addressDto) {
        if (addressDto == null) return;
        Address address = employee.getAddress();
        BeanUtils.copyProperties(addressDto, address, "id", "uuid", "createdAt", "updatedAt");
        employee.setAddress(address);
    }
}
