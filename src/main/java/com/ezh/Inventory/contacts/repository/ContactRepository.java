package com.ezh.Inventory.contacts.repository;

import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.entiry.ContactType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Boolean existsByContactCode(String contactCode);

    @Query("""
                SELECT c FROM Contact c
                WHERE (
                        :search IS NULL OR
                        LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                        LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                        LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR
                        LOWER(c.gstNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
                        LOWER(c.contactCode) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                  AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
                  AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
                  AND (:phone IS NULL OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :phone, '%')))
                  AND (:gst IS NULL OR LOWER(c.gstNumber) LIKE LOWER(CONCAT('%', :gst, '%')))
                  AND (:type IS NULL OR c.type = :type)
                  AND (:active IS NULL OR c.active = :active)
            """)
    Page<Contact> searchContacts(
            @Param("search") String search,
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("gst") String gstNumber,
            @Param("type") ContactType type,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
