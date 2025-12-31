package com.ezh.Inventory.contacts.service;

import com.ezh.Inventory.contacts.dto.AddressDto;
import com.ezh.Inventory.contacts.dto.ContactDto;
import com.ezh.Inventory.contacts.dto.ContactFilter;
import com.ezh.Inventory.contacts.entiry.Address;
import com.ezh.Inventory.contacts.entiry.Contact;
import com.ezh.Inventory.contacts.repository.ContactRepository;
import com.ezh.Inventory.utils.common.CommonResponse;
import com.ezh.Inventory.utils.common.Status;
import com.ezh.Inventory.utils.exception.BadRequestException;
import com.ezh.Inventory.utils.exception.CommonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ezh.Inventory.utils.UserContextUtil.getTenantIdOrThrow;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository repository;

    @Override
    @Transactional
    public CommonResponse createContact(ContactDto contactDto) throws CommonException {
        log.info("");
        if (repository.existsByContactCode(contactDto.getContactCode())) {
            throw new BadRequestException("Contact code already exists");
        }

        Contact contact = convertToEntity(contactDto);
        repository.save(contact);

        return CommonResponse.builder()
                .message("Contact created successfully")
                .status(Status.SUCCESS)
                .id(String.valueOf(contact.getId()))
                .build();
    }

    @Override
    @Transactional
    public CommonResponse updateContact(Long id, ContactDto contactDto) throws CommonException {
        Contact existing = repository.findByIdAndTenantId(id, getTenantIdOrThrow())
                .orElseThrow(() -> new BadRequestException("Contact not found"));

        existing.setContactCode(contactDto.getContactCode());
        existing.setName(contactDto.getName());
        existing.setEmail(contactDto.getEmail());
        existing.setPhone(contactDto.getPhone());
        existing.setCreditDays(contactDto.getCreditDays());
        existing.setGstNumber(contactDto.getGstNumber());
        existing.setType(contactDto.getType());
        existing.setActive(contactDto.getActive() != null ? contactDto.getActive() : true);

        repository.save(existing);

        return CommonResponse.builder()
                .message("Contact updated successfully")
                .status(Status.SUCCESS)
                .id(String.valueOf(existing.getId()))
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public ContactDto getContact(Long id) throws CommonException {
        Contact contact = repository.findByIdAndTenantId(id, getTenantIdOrThrow())
                .orElseThrow(() -> new BadRequestException("Contact not found"));

        return convertToDTO(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> getAllContacts(ContactFilter contactFilter, Integer page, Integer size) {
        log.info("get all");
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Contact> contacts = repository.searchContacts(
                contactFilter.getSearchQuery(),
                contactFilter.getName(),
                contactFilter.getEmail(),
                contactFilter.getPhone(),
                contactFilter.getGstNumber(),
                contactFilter.getType(),
                contactFilter.getActive(),
                pageable
        );
        //Page<Contact> contacts = repository.findAll(pageable);
        return contacts.map(this::convertToDTO);
    }



    @Override
    @Transactional
    public CommonResponse toggleStatus(Long id, Boolean active) throws CommonException {
        Contact contact = repository.findByIdAndTenantId(id, getTenantIdOrThrow())
                .orElseThrow(() -> new BadRequestException("Contact not found"));

        contact.setActive(active);
        repository.save(contact);

        String statusMsg = active ? "activated" : "deactivated";
        return CommonResponse.builder()
                .message("Contact " + statusMsg + " successfully")
                .status(Status.SUCCESS)
                .id(String.valueOf(contact.getId()))
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<ContactDto> searchContact(ContactFilter contactFilter) throws CommonException {

        log.info("Searching contacts without pagination");

        Pageable pageable = Pageable.unpaged();

        Page<Contact> contacts = repository.searchContacts(
                contactFilter.getSearchQuery(),
                contactFilter.getName(),
                contactFilter.getEmail(),
                contactFilter.getPhone(),
                contactFilter.getGstNumber(),
                contactFilter.getType(),
                contactFilter.getActive(),
                pageable
        );

        return contacts.getContent()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    private Contact convertToEntity(ContactDto dto) {

        Contact contact = Contact.builder()
                .tenantId(getTenantIdOrThrow())
                .contactCode(dto.getContactCode())
                .name(dto.getName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .creditDays(dto.getCreditDays())
                .gstNumber(dto.getGstNumber())
                .type(dto.getType())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            dto.getAddresses().forEach(a -> {
                Address address = Address.builder()
                        .type(a.getType())
                        .addressLine1(a.getAddressLine1())
                        .addressLine2(a.getAddressLine2())
                        .area(a.getArea())
                        .route(a.getRoute())
                        .city(a.getCity())
                        .state(a.getState())
                        .country(a.getCountry())
                        .pinCode(a.getPinCode())
                        .contact(contact)
                        .build();
                contact.getAddresses().add(address);
            });
        }
        return contact;
    }


    private ContactDto convertToDTO(Contact contact) {

        ContactDto.ContactDtoBuilder builder = ContactDto.builder()
                .id(contact.getId())
                .tenantId(contact.getTenantId())
                .contactCode(contact.getContactCode())
                .name(contact.getName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .creditDays(contact.getCreditDays())
                .gstNumber(contact.getGstNumber())
                .type(contact.getType())
                .active(contact.getActive());

        if (contact.getAddresses() != null && !contact.getAddresses().isEmpty()) {
            List<AddressDto> addressDtos = contact.getAddresses()
                    .stream()
                    .map(a -> AddressDto.builder()
                            .id(a.getId())
                            .type(a.getType())
                            .addressLine1(a.getAddressLine1())
                            .addressLine2(a.getAddressLine2())
                            .route(a.getRoute())
                            .area(a.getArea())
                            .city(a.getCity())
                            .state(a.getState())
                            .country(a.getCountry())
                            .pinCode(a.getPinCode())
                            .build()
                    )
                    .toList();

            builder.addresses(addressDtos);
        }
        return builder.build();
    }

}
