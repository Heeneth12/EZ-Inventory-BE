package com.ezh.Inventory.employee.controller;

import com.ezh.Inventory.employee.dto.EmployeeDto;
import com.ezh.Inventory.employee.dto.EmployeeFilter;
import com.ezh.Inventory.employee.service.EmployeeService;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.ResponseResource;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/employee")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> createEmployee(@RequestBody EmployeeDto employeeDto) throws CommonException {
        log.info("Creating new Employee: {}", employeeDto);
        CommonResponse response = employeeService.createEmployee(employeeDto);
        return ResponseResource.success(HttpStatus.CREATED, response, "Successfully Created");
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto employeeDto) throws CommonException {
        log.info("Updating Employee {}: {}", id, employeeDto);
        CommonResponse response = employeeService.updateEmployee(id, employeeDto);
        return ResponseResource.success(HttpStatus.OK, response, "Successfully Updated");
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<EmployeeDto> getEmployee(@PathVariable Long id) throws CommonException {
        log.info("Fetching Employee with ID: {}", id);
        EmployeeDto employeeDto = employeeService.getEmployee(id);
        return ResponseResource.success(HttpStatus.OK, employeeDto, "Employee fetched successfully");
    }

    @PostMapping(path = "/all", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<Page<EmployeeDto>> getAllEmployees(@RequestParam Integer page, @RequestParam Integer size,
                                                               @RequestBody EmployeeFilter filter) throws CommonException {
        log.info("Fetching employees with filter: {}", filter);
        Page<EmployeeDto> employees = employeeService.getAllEmployees(filter, page, size);
        return ResponseResource.success(HttpStatus.OK, employees, "Employees fetched successfully");
    }

    @PostMapping(value = "/{id}/status")
    public ResponseResource<CommonResponse> toggleStatus(@PathVariable Long id, @RequestParam Boolean active) throws CommonException {
        log.info("Toggling status of Employee {} to {}", id, active);
        CommonResponse response = employeeService.toggleStatus(id, active);
        return ResponseResource.success(HttpStatus.OK, response, "Status updated successfully");
    }
}
