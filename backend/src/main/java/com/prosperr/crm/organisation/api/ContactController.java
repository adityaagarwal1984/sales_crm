package com.prosperr.crm.organisation.api;

import com.prosperr.crm.organisation.api.OrganisationDtos.ContactInput;
import com.prosperr.crm.organisation.api.OrganisationDtos.ContactView;
import com.prosperr.crm.organisation.api.OrganisationDtos.PageResponse;
import com.prosperr.crm.organisation.service.OrganisationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {
    private final OrganisationService service;
    public ContactController(OrganisationService service){this.service=service;}
    @GetMapping public PageResponse<ContactView> search(@RequestParam(required=false) String search,@RequestParam(required=false) UUID organisationId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.searchContacts(search,organisationId,page,size));}
    @GetMapping("/{id}") public ContactView get(@PathVariable UUID id){return service.getContact(id);}
    @PutMapping("/{id}") public ContactView update(@PathVariable UUID id,@Valid @RequestBody ContactInput input){return service.updateContact(id,input);}
}
