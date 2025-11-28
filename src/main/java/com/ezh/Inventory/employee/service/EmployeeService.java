package com.ezh.Inventory.employee.service;

import com.ezh.Inventory.employee.dto.EmployeeDto;
import com.ezh.Inventory.employee.dto.EmployeeFilter;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.exception.CommonException;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    CommonResponse createEmployee(EmployeeDto employeeDto) throws CommonException;
    CommonResponse updateEmployee(Long id, EmployeeDto employeeDto) throws CommonException;
    EmployeeDto getEmployee(Long id) throws CommonException;
    Page<EmployeeDto> getAllEmployees(EmployeeFilter filter, Integer page, Integer size) throws CommonException;
    CommonResponse toggleStatus(Long id, Boolean active) throws CommonException;

}
