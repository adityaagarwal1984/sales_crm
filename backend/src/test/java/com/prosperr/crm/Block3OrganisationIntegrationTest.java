package com.prosperr.crm;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class Block3OrganisationIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate http;
    private String root(){return "http://localhost:"+port+"/api/v1";}
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->"jdbc:h2:mem:prosperr_block3_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");r.add("app.security.jwt-secret-base64",()->"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");r.add("app.bootstrap.admin-email",()->"admin@prosperr.test");r.add("app.bootstrap.admin-password",()->"AdminTemporaryPass123!");}

    @Test void bdrCanManageOrganisationsAndContactsWithSearchDuplicatesPaginationAndRoleChecks() throws Exception {
        JsonNode adminLogin=http.postForEntity(root()+"/auth/login",Map.of("email","admin@prosperr.test","password","AdminTemporaryPass123!"),JsonNode.class).getBody();
        String admin=adminLogin.path("accessToken").asText();
        JsonNode bdrCreated=http.exchange(root()+"/admin/users",HttpMethod.POST,new HttpEntity<>(Map.of("fullName","Discovery BDR","email","discovery@prosperr.test","role","BDR","temporaryPassword","DiscoveryTemporary123!"),headers(admin)),JsonNode.class).getBody();
        JsonNode firstLogin=http.postForEntity(root()+"/auth/login",Map.of("email","discovery@prosperr.test","password","DiscoveryTemporary123!"),JsonNode.class).getBody();
        JsonNode changed=http.exchange(root()+"/auth/change-password",HttpMethod.POST,new HttpEntity<>(Map.of("currentPassword","DiscoveryTemporary123!","newPassword","DiscoveryPermanent123!"),headers(firstLogin.path("accessToken").asText())),JsonNode.class).getBody();
        String bdr=changed.path("accessToken").asText();
        var created=http.exchange(root()+"/organisations",HttpMethod.POST,new HttpEntity<>(Map.of("name"," Acme Analytics ","website","https://acme.example","industry","Technology","companySize","51-200","location","Bengaluru","leadSource","Referral"),headers(bdr)),JsonNode.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.OK);String orgId=created.getBody().path("id").asText();assertThat(created.getBody().path("createdByName").asText()).isEqualTo("Discovery BDR");
        var duplicateOrg=http.exchange(root()+"/organisations",HttpMethod.POST,new HttpEntity<>(Map.of("name","acme analytics"),headers(bdr)),JsonNode.class);assertThat(duplicateOrg.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        var contact=http.exchange(root()+"/organisations/"+orgId+"/contacts",HttpMethod.POST,new HttpEntity<>(Map.of("name","Sam Person","email","SAM@ACME.EXAMPLE","designation","VP","department","Sales","linkedinUrl","https://linkedin.com/in/sam"),headers(bdr)),JsonNode.class);
        assertThat(contact.getStatusCode()).isEqualTo(HttpStatus.OK);String contactId=contact.getBody().path("id").asText();assertThat(contact.getBody().path("organisation").path("id").asText()).isEqualTo(orgId);
        var duplicateContact=http.exchange(root()+"/organisations/"+orgId+"/contacts",HttpMethod.POST,new HttpEntity<>(Map.of("name","Other Sam","email","sam@acme.example"),headers(bdr)),JsonNode.class);assertThat(duplicateContact.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        var detail=http.exchange(root()+"/organisations/"+orgId,HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class);assertThat(detail.getBody().path("contacts").size()).isEqualTo(1);
        var searched=http.exchange(root()+"/contacts?search=Acme&page=0&size=1",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class);assertThat(searched.getBody().path("content").size()).isEqualTo(1);assertThat(searched.getBody().path("totalElements").asInt()).isEqualTo(1);
        var updated=http.exchange(root()+"/contacts/"+contactId,HttpMethod.PUT,new HttpEntity<>(Map.of("name","Samuel Person","email","sam@acme.example","organisationId",orgId),headers(bdr)),JsonNode.class);assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);assertThat(updated.getBody().path("name").asText()).isEqualTo("Samuel Person");
        var invalid=http.exchange(root()+"/organisations?page=-1",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class);assertThat(invalid.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        http.exchange(root()+"/admin/users",HttpMethod.POST,new HttpEntity<>(Map.of("fullName","AE User","email","ae@prosperr.test","role","AE","temporaryPassword","ExecutiveTemporary123!"),headers(admin)),JsonNode.class);
        JsonNode aeLogin=http.postForEntity(root()+"/auth/login",Map.of("email","ae@prosperr.test","password","ExecutiveTemporary123!"),JsonNode.class).getBody();
        var sharedDirectory=http.exchange(root()+"/organisations",HttpMethod.GET,new HttpEntity<>(headers(aeLogin.path("accessToken").asText())),JsonNode.class);assertThat(sharedDirectory.getStatusCode()).isEqualTo(HttpStatus.OK);assertThat(sharedDirectory.getBody().path("totalElements").asInt()).isEqualTo(1);
        var readOnlyWrite=http.exchange(root()+"/organisations",HttpMethod.POST,new HttpEntity<>(Map.of("name","AE Created Org"),headers(aeLogin.path("accessToken").asText())),JsonNode.class);assertThat(readOnlyWrite.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    private HttpHeaders headers(String token){HttpHeaders h=new HttpHeaders();h.setBearerAuth(token);return h;}
}
