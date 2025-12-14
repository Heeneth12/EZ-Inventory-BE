package com.ezh.Inventory.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactMiniDto {
    private Long id;
    private String contactCode;
    private String name;
}
