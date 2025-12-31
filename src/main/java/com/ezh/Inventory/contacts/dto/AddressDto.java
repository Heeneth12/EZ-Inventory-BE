package com.ezh.Inventory.contacts.dto;

import com.ezh.Inventory.contacts.entiry.AddressType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    private Long id;
    private AddressType type;// BILLING / SHIPPING / OFFICE etc.
    private String addressLine1;
    private String addressLine2;
    private String route;
    private String area;
    private String city;
    private String state;
    private String country;
    private String pinCode;
}
