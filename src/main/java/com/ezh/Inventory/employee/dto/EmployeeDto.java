package com.ezh.Inventory.employee.dto;

import com.ezh.Inventory.contacts.dto.AddressDto;
import com.ezh.Inventory.employee.entity.EmployeeRole;
import com.ezh.Inventory.employee.entity.Gender;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private Gender gender;
    private EmployeeRole role;
    private String officialEmail;
    private String personalEmail;
    private String contactNumber;
    private Boolean active;
    private AddressDto address;
}
