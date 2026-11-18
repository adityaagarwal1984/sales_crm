package com.prosperr.crm.organisation.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.organisation.api.OrganisationDtos.*;
import com.prosperr.crm.organisation.service.OrganisationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organisations")
public class OrganisationController {
    private final OrganisationService service;
    public OrganisationController(OrganisationService service){this.service=service;}
    @GetMapping public PageResponse<OrganisationSummary> search(@RequestParam(required=false) String search,@RequestParam(required=false) String industry,@RequestParam(required=false) String companySize,@RequestParam(required=false) String leadSource,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.search(search,industry,companySize,leadSource,page,size));}
    @GetMapping("/{id}") public OrganisationView get(@PathVariable UUID id){return service.get(id);}
    @PostMapping public OrganisationView create(@Valid @RequestBody OrganisationInput input,@AuthenticationPrincipal CrmPrincipal principal){return service.create(input,principal);}
    @PutMapping("/{id}") public OrganisationView update(@PathVariable UUID id,@Valid @RequestBody OrganisationInput input){return service.update(id,input);}
    @PostMapping("/{id}/contacts") public ContactView createContact(@PathVariable UUID id,@Valid @RequestBody ContactInput input,@AuthenticationPrincipal CrmPrincipal principal){return service.createContact(id,input,principal);}
}
