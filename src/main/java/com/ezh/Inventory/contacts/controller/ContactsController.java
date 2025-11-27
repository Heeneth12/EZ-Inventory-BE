package com.ezh.Inventory.contacts.controller;


import com.ezh.Inventory.contacts.dto.ContactDto;
import com.ezh.Inventory.contacts.dto.ContactFilter;
import com.ezh.Inventory.contacts.service.ContactService;
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
@RequestMapping("/v1/contact")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ContactsController {

    private final ContactService contactService;

    /**
     * @Method : createContact
     * @Discriptim :
     *
     * @param contactDto
     * @return
     * @throws CommonException
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> createContact(@RequestBody ContactDto contactDto) throws CommonException{
        log.info("Creating new Contact with {}", contactDto);
        CommonResponse response = contactService.createContact(contactDto);
        return ResponseResource.success(HttpStatus.CREATED, response, " SUCCESSFULLY Created ");
    }

    /**
     *
     * @Method : updateContact
     * @Discriptim :
     *
     * @param id
     * @param contactDto
     * @return
     * @throws CommonException
     */
    @PostMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<CommonResponse> updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto) throws CommonException {
        log.info("Updating Contact {}: {}", id, contactDto);
        CommonResponse response = contactService.updateContact(id, contactDto);
        return ResponseResource.success(HttpStatus.OK, response, "Successfully Updated");
    }

    /**
     * @Method : getContact
     * @Discriptim :
     *
     * @param id
     * @return
     * @throws CommonException
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<ContactDto> getContact(@PathVariable Long id) throws CommonException  {
        log.info("Fetching Contact with ID: {}", id);
        ContactDto contactDto = contactService.getContact(id);
        return ResponseResource.success(HttpStatus.OK, contactDto, "Contact fetched successfully");
    }

    /**
     * @Method : getAllContacts
     * @Discriptim :
     *
     *
     * @param page
     * @param size
     * @param contactFilter
     * @return
     * @throws CommonException
     */
    @PostMapping(path = "/all", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseResource<Page<ContactDto>> getAllContacts(@RequestParam Integer page, @RequestParam Integer size,
                                                             @RequestBody ContactFilter contactFilter) throws CommonException{
        log.info("Fetching all contacts with filter: {}", contactFilter);
        Page<ContactDto> contacts = contactService.getAllContacts(contactFilter, page, size);
        return ResponseResource.success(HttpStatus.OK, contacts, "Contacts fetched successfully");
    }

    /**
     * @Method : toggleStatus
     * @Discriptim :
     *
     * @param id
     * @param active
     * @return
     * @throws CommonException
     */
    @PostMapping(value = "/{id}/status")
    public ResponseResource<CommonResponse> toggleStatus(@PathVariable Long id, @RequestParam Boolean active) throws CommonException {
        log.info("Toggling status of Contact {} to {}", id, active);
        CommonResponse response = contactService.toggleStatus(id, active);
        return ResponseResource.success(HttpStatus.OK, response, "Status updated successfully");
    }
}
