package com.ezh.Inventory.contacts.repository;

import com.ezh.Inventory.contacts.entiry.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
