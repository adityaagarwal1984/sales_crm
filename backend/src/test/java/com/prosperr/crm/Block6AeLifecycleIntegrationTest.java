package com.prosperr.crm;

import static org.assertj.core.api.Assertions.assertThat;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class Block6AeLifecycleIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate http;
    private String root(){return "http://localhost:"+port+"/api/v1";}
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",()->"jdbc:h2:mem:prosperr_block6_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        r.add("app.security.jwt-secret-base64",()->"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");
        r.add("app.bootstrap.admin-email",()->"admin@prosperr.test");
        r.add("app.bootstrap.admin-password",()->"AdminTemporaryPass123!");
    }

    @Test void aeCanManageAssignedOpportunityWithHistoryAndManagerApprovedReopening(){
        String admin=login("admin@prosperr.test","AdminTemporaryPass123!");
        JsonNode managerUser=createUser(admin,"Lifecycle Manager","manager6@prosperr.test","SALES_MANAGER");
        JsonNode bdrUser=createUser(admin,"Lifecycle BDR","bdr6@prosperr.test","BDR");
        JsonNode aeUser=createUser(admin,"Lifecycle AE","ae6@prosperr.test","AE");
        String manager=login("manager6@prosperr.test","TemporaryPassword123!");
        String bdr=login("bdr6@prosperr.test","TemporaryPassword123!");
        String ae=login("ae6@prosperr.test","TemporaryPassword123!");
        JsonNode org=post(bdr,"/organisations",Map.of("name","Lifecycle Prospect"));
        JsonNode contact=post(bdr,"/organisations/"+org.path("id").asText()+"/contacts",Map.of("name","Casey Contact","email","casey@lifecycle.test"));
        String lead=createInterestedLead(bdr,org.path("id").asText(),contact.path("id").asText());
        String sql=createAndSubmitSql(bdr,lead,org.path("id").asText(),contact.path("id").asText());
        post(manager,"/sales-manager/sql-qualifications/"+sql+"/approve",Map.of());
        JsonNode opportunity=post(manager,"/sales-manager/sql-qualifications/"+sql+"/assign",Map.of("aeId",aeUser.path("id").asText()));
        String id=opportunity.path("id").asText();
        assertThat(opportunity.path("status").asText()).isEqualTo("L1");

        var invalidSkip=http.exchange(root()+"/ae/opportunities/"+id+"/stage",HttpMethod.PATCH,new HttpEntity<>(Map.of("stage","L3"),headers(ae)),JsonNode.class);
        assertThat(invalidSkip.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode added=post(ae,"/ae/opportunities/"+id+"/stakeholders",Map.of("contactId",contact.path("id").asText(),"role","DECISION_MAKER"));
        assertThat(added.path("role").asText()).isEqualTo("DECISION_MAKER");
        var duplicateStakeholder=http.exchange(root()+"/ae/opportunities/"+id+"/stakeholders",HttpMethod.POST,new HttpEntity<>(Map.of("contactId",contact.path("id").asText(),"role","INFLUENCER"),headers(ae)),JsonNode.class);
        assertThat(duplicateStakeholder.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        JsonNode firstMeeting=post(ae,"/ae/opportunities/"+id+"/meetings",meeting("L1 discovery","DISCOVERY","L1",contact.path("id").asText(),true));
        assertThat(firstMeeting.path("participants").get(0).path("name").asText()).isEqualTo("Casey Contact");
        assertThat(get(ae,"/ae/opportunities/"+id).path("opportunity").path("status").asText()).isEqualTo("L2");
        JsonNode dashboard=get(ae,"/ae/opportunities/dashboard");
        assertThat(dashboard.path("l1Meetings").asInt()).isEqualTo(1);
        assertThat(dashboard.path("pendingFollowUps").asInt()).isEqualTo(1);
        post(ae,"/ae/opportunities/"+id+"/meetings/"+firstMeeting.path("id").asText()+"/follow-up/complete",Map.of());
        post(ae,"/ae/opportunities/"+id+"/meetings",meeting("L2 review","REVIEW","L2",contact.path("id").asText(),false));
        assertThat(get(ae,"/ae/opportunities/"+id).path("opportunity").path("status").asText()).isEqualTo("L3");

        JsonNode won=patch(ae,"/ae/opportunities/"+id+"/close-won",Map.of("finalDealValue",44000,"closedAt",Instant.now().toString(),"commercialDetails","Annual subscription, 12 months"));
        assertThat(won.path("opportunity").path("status").asText()).isEqualTo("WON");
        assertThat(won.path("handovers").get(0).path("status").asText()).isEqualTo("PENDING");
        assertThat(won.path("closures").size()).isEqualTo(1);
        var duplicateClose=http.exchange(root()+"/ae/opportunities/"+id+"/close-won",HttpMethod.PATCH,new HttpEntity<>(Map.of("finalDealValue",45000,"closedAt",Instant.now().toString(),"commercialDetails","retry"),headers(ae)),JsonNode.class);
        assertThat(duplicateClose.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        JsonNode reopen=post(ae,"/ae/opportunities/"+id+"/reopen-request",Map.of("reason","Customer reopened budget approval"));
        JsonNode requests=get(manager,"/sales-manager/opportunity-reopen-requests");
        assertThat(requests.size()).isEqualTo(1);
        post(manager,"/sales-manager/opportunity-reopen-requests/"+reopen.path("id").asText()+"/decision",Map.of("approved",true,"reason","Manager approved"));
        JsonNode reopened=get(ae,"/ae/opportunities/"+id);
        assertThat(reopened.path("opportunity").path("status").asText()).isEqualTo("L3");
        JsonNode lost=patch(ae,"/ae/opportunities/"+id+"/close-lost",Map.of("closedAt",Instant.now().toString(),"lostReason","Customer selected another vendor","closureNotes","Procurement confirmed"));
        assertThat(lost.path("closures").size()).isEqualTo(2);
        assertThat(lost.path("handovers").size()).isEqualTo(1);
        JsonNode secondReopen=post(ae,"/ae/opportunities/"+id+"/reopen-request",Map.of("reason","New commercial proposal accepted"));
        post(manager,"/sales-manager/opportunity-reopen-requests/"+secondReopen.path("id").asText()+"/decision",Map.of("approved",true));
        JsonNode wonAgain=patch(ae,"/ae/opportunities/"+id+"/close-won",Map.of("finalDealValue",48000,"closedAt",Instant.now().toString(),"commercialDetails","Revised annual subscription"));
        assertThat(wonAgain.path("closures").size()).isEqualTo(3);
        assertThat(wonAgain.path("handovers").size()).isEqualTo(2);
        assertThat(wonAgain.path("stageHistory").size()).isGreaterThanOrEqualTo(8);

        var forbidden=http.exchange(root()+"/ae/opportunities/"+id,HttpMethod.GET,new HttpEntity<>(headers(manager)),JsonNode.class);
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Map<String,Object> meeting(String title,String type,String level,String contactId,boolean followUp){
        Map<String,Object> m=new HashMap<>();m.put("title",title);m.put("meetingType",type);m.put("meetingLevel",level);m.put("completedAt",Instant.now().minusSeconds(60).toString());m.put("outcome","Customer confirmed requirements");m.put("notes","Detailed needs recorded");m.put("nextAction","Send materials");m.put("participantIds",java.util.List.of(contactId));if(followUp)m.put("followUpAt",Instant.now().plusSeconds(3600).toString());return m;
    }
    private String createInterestedLead(String bdr,String org,String contact){JsonNode l=post(bdr,"/bdr/leads",Map.of("title","Lifecycle lead","organisationId",org,"primaryContactId",contact));post(bdr,"/bdr/leads/"+l.path("id").asText()+"/calls",Map.of("contactId",contact,"calledAt",Instant.now().toString(),"outcome","INTERESTED","notes","Asked for evaluation"));return l.path("id").asText();}
    private String createAndSubmitSql(String bdr,String lead,String org,String contact){Map<String,Object> m=new HashMap<>();m.put("leadId",lead);m.put("organisationId",org);m.put("primaryContactId",contact);m.put("businessRequirement","Automate operations");m.put("decisionMakerIdentified",true);m.put("decisionMakerDetails","Casey, VP");m.put("estimatedDealValue",40000);m.put("expectedTimeline","This quarter");String id=post(bdr,"/bdr/sql-qualifications",m).path("qualification").path("id").asText();post(bdr,"/bdr/sql-qualifications/"+id+"/submit",Map.of());return id;}
    private JsonNode createUser(String admin,String name,String email,String role){Map<String,Object> m=new HashMap<>();m.put("fullName",name);m.put("email",email);m.put("role",role);m.put("temporaryPassword","TemporaryPassword123!");return post(admin,"/admin/users",m);}
    private String login(String email,String password){return http.postForEntity(root()+"/auth/login",Map.of("email",email,"password",password),JsonNode.class).getBody().path("accessToken").asText();}
    private JsonNode get(String token,String path){return http.exchange(root()+path,HttpMethod.GET,new HttpEntity<>(headers(token)),JsonNode.class).getBody();}
    private JsonNode post(String token,String path,Object body){return http.exchange(root()+path,HttpMethod.POST,new HttpEntity<>(body,headers(token)),JsonNode.class).getBody();}
    private JsonNode patch(String token,String path,Object body){return http.exchange(root()+path,HttpMethod.PATCH,new HttpEntity<>(body,headers(token)),JsonNode.class).getBody();}
    private HttpHeaders headers(String token){HttpHeaders h=new HttpHeaders();h.setBearerAuth(token);return h;}
}
