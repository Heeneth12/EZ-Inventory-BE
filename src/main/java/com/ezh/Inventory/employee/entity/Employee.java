package com.ezh.Inventory.employee.entity;

import com.ezh.Inventory.contacts.entiry.Address;
import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends CommonSerializable {

    @Column(name = "employee_code", nullable = false, unique = true, length = 30)
    private String employeeCode; // EMP001 / HR-1002

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "gender", length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 50)
    private EmployeeRole role;

    @Column(name = "official_email", unique = true, length = 120)
    private String officialEmail;

    @Column(name = "personal_email", length = 120)
    private String personalEmail;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    private Boolean active;
}
