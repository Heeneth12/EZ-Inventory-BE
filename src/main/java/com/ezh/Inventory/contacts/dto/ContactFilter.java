package com.ezh.Inventory.contacts.dto;

import com.ezh.Inventory.contacts.entiry.ContactType;

public class ContactFilter {
    private String search;
    private ContactType type;         // VENDOR / CUSTOMER / BOTH / null
    private Boolean active;           // true / false / null
}
