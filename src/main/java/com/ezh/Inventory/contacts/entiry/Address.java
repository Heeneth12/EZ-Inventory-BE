package com.ezh.Inventory.contacts.entiry;

import com.ezh.Inventory.utils.common.CommonSerializable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends CommonSerializable {

    private String addressLine1;
    private String addressLine2;
    private String route;
    private String area;
    private String city;
    private String state;
    private String country;
    private String pinCode;
    @Enumerated(EnumType.STRING)
    private AddressType  type;  // BILLING, SHIPPING, OFFICE, etc.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;
}