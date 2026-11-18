package com.prosperr.crm.organisation.service;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.organisation.api.OrganisationDtos.*;
import com.prosperr.crm.organisation.domain.*;
import com.prosperr.crm.organisation.repository.*;
import java.util.Locale;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OrganisationService {
    private final OrganisationRepository organisations; private final ContactRepository contacts; private final UserRepository users;
    public OrganisationService(OrganisationRepository organisations,ContactRepository contacts,UserRepository users){this.organisations=organisations;this.contacts=contacts;this.users=users;}

    @Transactional(readOnly=true)
    public Page<OrganisationSummary> search(String search,String industry,String companySize,String leadSource,int page,int size){Page<OrganisationEntity> result=organisations.search(searchPattern(search),normalizedFilter(industry),normalizedFilter(companySize),normalizedFilter(leadSource),pageable(page,size));Map<UUID,Long> counts=counts(result.getContent().stream().map(OrganisationEntity::getId).toList());return result.map(o->summary(o,counts));}
    @Transactional(readOnly=true)
    public OrganisationView get(UUID id){OrganisationEntity org=organisation(id);return view(org);}
    @Transactional
    public OrganisationView create(OrganisationInput input,CrmPrincipal principal){String name=input.name().trim();if(organisations.existsByNormalizedName(name.toLowerCase(Locale.ROOT)))throw new ResponseStatusException(HttpStatus.CONFLICT,"An organisation with this name already exists");UserEntity creator=users.findById(principal.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));OrganisationEntity saved=organisations.save(entity(input,creator));return view(saved);}
    @Transactional
    public OrganisationView update(UUID id,OrganisationInput input){OrganisationEntity org=organisation(id);String name=input.name().trim();if(organisations.existsByNormalizedNameAndIdNot(name.toLowerCase(Locale.ROOT),id))throw new ResponseStatusException(HttpStatus.CONFLICT,"An organisation with this name already exists");org.update(name,clean(input.website()),clean(input.industry()),clean(input.companySize()),clean(input.location()),clean(input.address()),clean(input.leadSource()),clean(input.description()));return view(org);}

    @Transactional(readOnly=true)
    public Page<ContactView> searchContacts(String search,UUID organisationId,int page,int size){if(organisationId!=null)organisation(organisationId);Page<ContactEntity> result=contacts.search(searchPattern(search),organisationId,pageable(page,size));Map<UUID,Long> counts=counts(result.getContent().stream().map(c->c.getOrganisation().getId()).distinct().toList());return result.map(c->contactView(c,counts));}
    @Transactional(readOnly=true)
    public ContactView getContact(UUID id){ContactEntity c=contact(id);return contactView(c,counts(List.of(c.getOrganisation().getId())));}
    @Transactional
    public ContactView createContact(UUID organisationId,ContactInput input,CrmPrincipal principal){OrganisationEntity org=organisation(organisationId);UserEntity creator=users.findById(principal.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));String email=email(input.email());ensureUniqueEmail(org.getId(),email,null);ContactEntity created=contacts.save(contactEntity(org,creator,input,email));return contactView(created,Map.of(org.getId(),contacts.countByOrganisation_Id(org.getId())));}
    @Transactional
    public ContactView updateContact(UUID id,ContactInput input){ContactEntity contact=contact(id);UUID targetId=input.organisationId()==null?contact.getOrganisation().getId():input.organisationId();OrganisationEntity org=organisation(targetId);String email=email(input.email());ensureUniqueEmail(org.getId(),email,id);contact.update(org,input.name().trim(),email,clean(input.phone()),clean(input.designation()),clean(input.department()),clean(input.linkedinUrl()));return contactView(contact,counts(List.of(org.getId())));}

    private OrganisationEntity entity(OrganisationInput i,UserEntity user){return new OrganisationEntity(i.name().trim(),clean(i.website()),clean(i.industry()),clean(i.companySize()),clean(i.location()),clean(i.address()),clean(i.leadSource()),clean(i.description()),user);}
    private ContactEntity contactEntity(OrganisationEntity org,UserEntity creator,ContactInput i,String email){return new ContactEntity(org,creator,i.name().trim(),email,clean(i.phone()),clean(i.designation()),clean(i.department()),clean(i.linkedinUrl()));}
    private void ensureUniqueEmail(UUID orgId,String email,UUID except){if(email==null)return;boolean exists=except==null?contacts.existsByOrganisationAndNormalizedEmail(orgId,email):contacts.existsByOrganisationAndNormalizedEmailAndIdNot(orgId,email,except);if(exists)throw new ResponseStatusException(HttpStatus.CONFLICT,"A contact with this email already exists in the organisation");}
    private OrganisationEntity organisation(UUID id){return organisations.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Organisation not found"));}
    private ContactEntity contact(UUID id){return contacts.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));}
    private OrganisationSummary summary(OrganisationEntity o,Map<UUID,Long> counts){return new OrganisationSummary(o.getId(),o.getName(),o.getWebsite(),o.getIndustry(),o.getCompanySize(),o.getLocation(),o.getLeadSource(),counts.getOrDefault(o.getId(),0L),o.getCreatedAt());}
    private OrganisationView view(OrganisationEntity o){var listedEntities=contacts.findAllByOrganisation_IdOrderByNameAsc(o.getId());Map<UUID,Long> counts=counts(List.of(o.getId()));var listed=listedEntities.stream().map(c->contactView(c,counts)).toList();return new OrganisationView(o.getId(),o.getName(),o.getWebsite(),o.getIndustry(),o.getCompanySize(),o.getLocation(),o.getAddress(),o.getLeadSource(),o.getDescription(),o.getCreatedBy().getId(),o.getCreatedBy().getFullName(),o.getCreatedAt(),o.getUpdatedAt(),listed);}
    private ContactView contactView(ContactEntity c,Map<UUID,Long> counts){return new ContactView(c.getId(),c.getName(),c.getEmail(),c.getPhone(),c.getDesignation(),c.getDepartment(),c.getLinkedinUrl(),c.getCreatedAt(),c.getUpdatedAt(),c.getCreatedBy().getId(),c.getCreatedBy().getFullName(),summary(c.getOrganisation(),counts));}
    private Map<UUID,Long> counts(Collection<UUID> organisationIds){if(organisationIds.isEmpty())return Map.of();return contacts.countByOrganisationIds(organisationIds).stream().collect(Collectors.toMap(com.prosperr.crm.organisation.repository.OrganisationContactCount::getOrganisationId,com.prosperr.crm.organisation.repository.OrganisationContactCount::getContactCount));}
    private Pageable pageable(int page,int size){if(page<0||size<1||size>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page must be non-negative and size must be between 1 and 100");return PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"));}
    private static String email(String value){String normalized=clean(value);return normalized==null?null:normalized.toLowerCase(Locale.ROOT);}
    private static String normalizedFilter(String value){String normalized=clean(value);return normalized==null?"":normalized.toLowerCase(Locale.ROOT);}
    private static String searchPattern(String value){String normalized=clean(value);return normalized==null?"%%":"%"+normalized.toLowerCase(Locale.ROOT)+"%";}
    private static String clean(String value){if(value==null)return null;String cleaned=value.trim();return cleaned.isEmpty()?null:cleaned;}
}
