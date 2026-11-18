package com.prosperr.crm;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
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
class Block4ProspectingIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate http;
    private String root(){return "http://localhost:"+port+"/api/v1";}
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->"jdbc:h2:mem:prosperr_block4_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");r.add("app.security.jwt-secret-base64",()->"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");r.add("app.bootstrap.admin-email",()->"admin@prosperr.test");r.add("app.bootstrap.admin-password",()->"AdminTemporaryPass123!");}

    @Test void bdrLeadCallFollowUpAndSqlWorkflowPersistsAndKeepsCallHistoryAppendOnly() throws Exception {
        JsonNode adminLogin=http.postForEntity(root()+"/auth/login",Map.of("email","admin@prosperr.test","password","AdminTemporaryPass123!"),JsonNode.class).getBody();String admin=adminLogin.path("accessToken").asText();
        JsonNode bdrUser=http.exchange(root()+"/admin/users",HttpMethod.POST,new HttpEntity<>(Map.of("fullName","Prospecting BDR","email","prospecting@prosperr.test","role","BDR","temporaryPassword","ProspectingTemporary123!"),headers(admin)),JsonNode.class).getBody();
        JsonNode first=http.postForEntity(root()+"/auth/login",Map.of("email","prospecting@prosperr.test","password","ProspectingTemporary123!"),JsonNode.class).getBody();
        JsonNode changed=http.exchange(root()+"/auth/change-password",HttpMethod.POST,new HttpEntity<>(Map.of("currentPassword","ProspectingTemporary123!","newPassword","ProspectingPermanent123!"),headers(first.path("accessToken").asText())),JsonNode.class).getBody();String bdr=changed.path("accessToken").asText();
        JsonNode org=http.exchange(root()+"/organisations",HttpMethod.POST,new HttpEntity<>(Map.of("name","Prospecting Works","website","https://prospecting.example"),headers(bdr)),JsonNode.class).getBody();String orgId=org.path("id").asText();
        JsonNode contact=http.exchange(root()+"/organisations/"+orgId+"/contacts",HttpMethod.POST,new HttpEntity<>(Map.of("name","Priya Prospect","email","priya@prospecting.example"),headers(bdr)),JsonNode.class).getBody();String contactId=contact.path("id").asText();
        JsonNode lead=http.exchange(root()+"/bdr/leads",HttpMethod.POST,new HttpEntity<>(Map.of("title","Benefits platform evaluation","description","Initial discovery","organisationId",orgId,"primaryContactId",contactId),headers(bdr)),JsonNode.class).getBody();String leadId=lead.path("id").asText();assertThat(lead.path("ownerId").asText()).isEqualTo(bdrUser.path("id").asText());
        Instant calledAt=Instant.now().minusSeconds(120);Instant due=Instant.now().plusSeconds(3600);
        JsonNode call=http.exchange(root()+"/bdr/leads/"+leadId+"/calls",HttpMethod.POST,new HttpEntity<>(Map.of("contactId",contactId,"calledAt",calledAt.toString(),"outcome","INTERESTED","notes","Asked for a follow-up","nextFollowUpAt",due.toString()),headers(bdr)),JsonNode.class).getBody();String followUpId=call.path("followUp").path("id").asText();assertThat(call.path("call").path("outcome").asText()).isEqualTo("INTERESTED");
        JsonNode dashboard=http.exchange(root()+"/bdr/dashboard",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getBody();assertThat(dashboard.path("myLeads").asLong()).isEqualTo(1);assertThat(dashboard.path("organisationsAdded").asLong()).isEqualTo(1);assertThat(dashboard.path("contactsAdded").asLong()).isEqualTo(1);assertThat(dashboard.path("callsCompleted").asLong()).isEqualTo(1);assertThat(dashboard.path("interestedProspects").asLong()).isEqualTo(1);assertThat(dashboard.path("pendingFollowUps").asLong()).isEqualTo(1);
        JsonNode complete=http.exchange(root()+"/bdr/follow-ups/"+followUpId+"/complete",HttpMethod.POST,new HttpEntity<>(Map.of("calledAt",Instant.now().toString(),"outcome","CONNECTED","notes","Completed scheduled call"),headers(bdr)),JsonNode.class).getBody();assertThat(complete.path("call").path("id").asText()).isNotBlank();assertThat(complete.path("followUp").isNull()).isTrue();
        JsonNode history=http.exchange(root()+"/bdr/leads/"+leadId+"/calls?page=0&size=10",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getBody();assertThat(history.path("totalElements").asInt()).isEqualTo(2);
        JsonNode completed=http.exchange(root()+"/bdr/follow-ups?filter=COMPLETED",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getBody();assertThat(completed.path("totalElements").asInt()).isEqualTo(1);
        http.exchange(root()+"/bdr/leads/"+leadId+"/calls",HttpMethod.POST,new HttpEntity<>(Map.of("contactId",contactId,"calledAt",Instant.now().toString(),"outcome","INTERESTED","notes","Confirmed buying interest"),headers(bdr)),JsonNode.class);
        Map<String,Object> qualificationInput=Map.ofEntries(Map.entry("leadId",leadId),Map.entry("organisationId",orgId),Map.entry("primaryContactId",contactId),Map.entry("companySize","51-200"),Map.entry("industry","SaaS"),Map.entry("businessRequirement","Automate benefits administration"),Map.entry("decisionMakerIdentified",true),Map.entry("decisionMakerDetails","Priya, VP People"),Map.entry("estimatedDealValue",25000.00),Map.entry("expectedTimeline","This quarter"),Map.entry("qualificationNotes","Budget confirmed"),Map.entry("supportingInformation","Discovery call summary"));
        JsonNode draft=http.exchange(root()+"/bdr/sql-qualifications",HttpMethod.POST,new HttpEntity<>(qualificationInput,headers(bdr)),JsonNode.class).getBody();String qualificationId=draft.path("qualification").path("id").asText();assertThat(draft.path("qualification").path("status").asText()).isEqualTo("DRAFT");assertThat(draft.path("history").size()).isEqualTo(1);
        var duplicateQualification=http.exchange(root()+"/bdr/sql-qualifications",HttpMethod.POST,new HttpEntity<>(qualificationInput,headers(bdr)),JsonNode.class);assertThat(duplicateQualification.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        var wrongLeadLink=http.exchange(root()+"/bdr/sql-qualifications/"+qualificationId,HttpMethod.PUT,new HttpEntity<>(Map.of("leadId",leadId,"organisationId",orgId,"primaryContactId",UUID.randomUUID()),headers(bdr)),JsonNode.class);assertThat(wrongLeadLink.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        http.exchange(root()+"/admin/users",HttpMethod.POST,new HttpEntity<>(Map.of("fullName","Sales Manager","email","manager@prosperr.test","role","SALES_MANAGER","temporaryPassword","ManagerTemporary123!"),headers(admin)),JsonNode.class);
        JsonNode managerLogin=http.postForEntity(root()+"/auth/login",Map.of("email","manager@prosperr.test","password","ManagerTemporary123!"),JsonNode.class).getBody();String manager=managerLogin.path("accessToken").asText();
        var managerCannotViewDrafts=http.exchange(root()+"/sales-manager/sql-qualifications?status=DRAFT",HttpMethod.GET,new HttpEntity<>(headers(manager)),JsonNode.class);assertThat(managerCannotViewDrafts.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        http.exchange(root()+"/bdr/sql-qualifications/"+qualificationId+"/submit",HttpMethod.POST,new HttpEntity<>(headers(bdr)),JsonNode.class);
        JsonNode managerQueue=http.exchange(root()+"/sales-manager/sql-qualifications?status=SUBMITTED",HttpMethod.GET,new HttpEntity<>(headers(manager)),JsonNode.class).getBody();assertThat(managerQueue.path("totalElements").asInt()).isEqualTo(1);assertThat(managerQueue.path("content").get(0).path("primaryContactName").asText()).isEqualTo("Priya Prospect");
        JsonNode managerDetail=http.exchange(root()+"/sales-manager/sql-qualifications/"+qualificationId,HttpMethod.GET,new HttpEntity<>(headers(manager)),JsonNode.class).getBody();assertThat(managerDetail.path("qualification").path("businessRequirement").asText()).isEqualTo("Automate benefits administration");
        JsonNode rejected=http.exchange(root()+"/sales-manager/sql-qualifications/"+qualificationId+"/reject",HttpMethod.POST,new HttpEntity<>(Map.of("reason","Please add the implementation team and clarify timing"),headers(manager)),JsonNode.class).getBody();assertThat(rejected.path("qualification").path("status").asText()).isEqualTo("REJECTED");
        JsonNode bdrRejected=http.exchange(root()+"/bdr/sql-qualifications/"+qualificationId,HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getBody();assertThat(bdrRejected.path("qualification").path("rejectionReason").asText()).contains("implementation team");
        Map<String,Object> revised=new java.util.HashMap<>(qualificationInput);revised.put("qualificationNotes","Implementation team identified; budget confirmed");
        JsonNode resaved=http.exchange(root()+"/bdr/sql-qualifications/"+qualificationId,HttpMethod.PUT,new HttpEntity<>(revised,headers(bdr)),JsonNode.class).getBody();assertThat(resaved.path("qualification").path("status").asText()).isEqualTo("DRAFT");assertThat(resaved.path("history").get(1).path("reason").asText()).contains("implementation team");
        http.exchange(root()+"/bdr/sql-qualifications/"+qualificationId+"/submit",HttpMethod.POST,new HttpEntity<>(headers(bdr)),JsonNode.class);
        JsonNode approved=http.exchange(root()+"/sales-manager/sql-qualifications/"+qualificationId+"/approve",HttpMethod.POST,new HttpEntity<>(headers(manager)),JsonNode.class).getBody();assertThat(approved.path("qualification").path("status").asText()).isEqualTo("APPROVED");assertThat(approved.path("qualification").path("reviewedById").asText()).isEqualTo(managerLogin.path("user").path("id").asText());assertThat(approved.path("qualification").path("eligibleForAeAssignment").asBoolean()).isTrue();assertThat(approved.path("history").size()).isEqualTo(6);
        var bdrCannotApprove=http.exchange(root()+"/sales-manager/sql-qualifications/"+qualificationId+"/approve",HttpMethod.POST,new HttpEntity<>(headers(bdr)),JsonNode.class);assertThat(bdrCannotApprove.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        JsonNode managerOrgs=http.exchange(root()+"/organisations",HttpMethod.GET,new HttpEntity<>(headers(manager)),JsonNode.class).getBody();assertThat(managerOrgs.path("totalElements").asInt()).isEqualTo(1);
        var managerCannotCreateLead=http.exchange(root()+"/bdr/leads",HttpMethod.POST,new HttpEntity<>(Map.of("title","No access","organisationId",orgId,"primaryContactId",contactId),headers(manager)),JsonNode.class);assertThat(managerCannotCreateLead.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        JsonNode updatedDashboard=http.exchange(root()+"/bdr/dashboard",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getBody();assertThat(updatedDashboard.path("callsCompleted").asLong()).isEqualTo(3);assertThat(updatedDashboard.path("interestedProspects").asLong()).isEqualTo(1);assertThat(updatedDashboard.path("sqlsGenerated").asLong()).isEqualTo(1);assertThat(updatedDashboard.path("pendingFollowUps").asLong()).isZero();
        var forbidden=http.exchange(root()+"/bdr/leads",HttpMethod.GET,new HttpEntity<>(headers(admin)),JsonNode.class);assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    private HttpHeaders headers(String token){HttpHeaders h=new HttpHeaders();h.setBearerAuth(token);return h;}
}
