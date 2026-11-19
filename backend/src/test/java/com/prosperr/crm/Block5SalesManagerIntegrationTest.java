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
class Block5SalesManagerIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate http;
    private String root(){return "http://localhost:"+port+"/api/v1";}

    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",()->"jdbc:h2:mem:prosperr_block5_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        r.add("app.security.jwt-secret-base64",()->"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");
        r.add("app.bootstrap.admin-email",()->"admin@prosperr.test");
        r.add("app.bootstrap.admin-password",()->"AdminTemporaryPass123!");
    }

    @Test void approvedSqlsAndAllAdminCreatedBdrsAndAesAreVisibleToEveryManager(){
        String admin=login("admin@prosperr.test","AdminTemporaryPass123!");
        JsonNode managerUser=createUser(admin,"Team Manager","manager5@prosperr.test","SALES_MANAGER",null);
        createUser(admin,"Other Manager","manager-other@prosperr.test","SALES_MANAGER",null);
        String manager=login("manager5@prosperr.test","TemporaryPassword123!");
        String otherManager=login("manager-other@prosperr.test","TemporaryPassword123!");
        String managerId=managerUser.path("id").asText();
        JsonNode bdrUser=createUser(admin,"Team BDR","team-bdr@prosperr.test","BDR",null);
        JsonNode aeOne=createUser(admin,"AE One","ae-one@prosperr.test","AE",null);
        JsonNode aeTwo=createUser(admin,"AE Two","ae-two@prosperr.test","AE",null);
        JsonNode inactiveAe=createUser(admin,"Inactive AE","ae-inactive@prosperr.test","AE",null);
        http.exchange(root()+"/admin/users/"+inactiveAe.path("id").asText()+"/status",HttpMethod.PATCH,new HttpEntity<>(Map.of("active",false),headers(admin)),JsonNode.class);
        String bdr=login("team-bdr@prosperr.test","TemporaryPassword123!");
        String aeOneToken=login("ae-one@prosperr.test","TemporaryPassword123!");
        String aeTwoToken=login("ae-two@prosperr.test","TemporaryPassword123!");

        JsonNode org=post(bdr,"/organisations",Map.of("name","Manager Team Prospect"));
        String orgId=org.path("id").asText();
        JsonNode contact=post(bdr,"/organisations/"+orgId+"/contacts",Map.of("name","Morgan Buyer"));
        String contactId=contact.path("id").asText();
        String firstLead=createInterestedLead(bdr,orgId,contactId,"HR platform evaluation");
        String firstSql=createAndSubmitSql(bdr,firstLead,orgId,contactId,24000);
        assertThat(post(manager,"/sales-manager/sql-qualifications/"+firstSql+"/approve",Map.of()).path("qualification").path("status").asText()).isEqualTo("APPROVED");
        assertThat(get(manager,"/sales-manager/sql-qualifications/approved-for-assignment").path("totalElements").asInt()).isEqualTo(1);
        assertThat(get(otherManager,"/sales-manager/sql-qualifications?status=APPROVED").path("totalElements").asInt()).isEqualTo(1);

        var inactive=http.exchange(root()+"/sales-manager/sql-qualifications/"+firstSql+"/assign",HttpMethod.POST,new HttpEntity<>(Map.of("aeId",inactiveAe.path("id").asText()),headers(manager)),JsonNode.class);
        assertThat(inactive.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode opportunity=post(manager,"/sales-manager/sql-qualifications/"+firstSql+"/assign",Map.of("aeId",aeOne.path("id").asText()));
        String opportunityId=opportunity.path("id").asText();
        assertThat(get(manager,"/sales-manager/sql-qualifications/approved-for-assignment").path("totalElements").asInt()).isZero();
        var duplicate=http.exchange(root()+"/sales-manager/sql-qualifications/"+firstSql+"/assign",HttpMethod.POST,new HttpEntity<>(Map.of("aeId",aeOne.path("id").asText()),headers(manager)),JsonNode.class);
        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        put(manager,"/sales-manager/opportunities/"+opportunityId+"/assignment",Map.of("aeId",aeTwo.path("id").asText()));
        assertThat(get(manager,"/sales-manager/opportunities/"+opportunityId+"/assignment-history").size()).isEqualTo(2);
        assertThat(get(aeOneToken,"/ae/opportunities").path("totalElements").asInt()).isZero();
        assertThat(get(aeTwoToken,"/ae/opportunities").path("totalElements").asInt()).isEqualTo(1);

        JsonNode notifications=get(aeTwoToken,"/notifications");
        assertThat(notifications.path("unreadCount").asInt()).isEqualTo(1);
        String notificationId=notifications.path("items").get(0).path("id").asText();
        post(aeTwoToken,"/notifications/"+notificationId+"/read",Map.of());
        assertThat(get(aeTwoToken,"/notifications").path("unreadCount").asInt()).isZero();
        post(aeTwoToken,"/ae/opportunities/"+opportunityId+"/meetings",Map.of("title","L1 discovery","meetingType","DISCOVERY","meetingLevel","L1","outcome","Needs confirmed","completedAt",Instant.now().minusSeconds(120).toString()));
        post(aeTwoToken,"/ae/opportunities/"+opportunityId+"/meetings",Map.of("title","L2 demo","meetingType","DEMO","meetingLevel","L2","outcome","Demo successful","completedAt",Instant.now().minusSeconds(60).toString()));
        http.exchange(root()+"/ae/opportunities/"+opportunityId+"/close-won",HttpMethod.PATCH,new HttpEntity<>(Map.of("finalDealValue",24000,"closedAt",Instant.now().toString(),"commercialDetails","Annual subscription"),headers(aeTwoToken)),JsonNode.class);
        var closedReassignment=http.exchange(root()+"/sales-manager/opportunities/"+opportunityId+"/assignment",HttpMethod.PUT,new HttpEntity<>(Map.of("aeId",aeOne.path("id").asText()),headers(manager)),JsonNode.class);
        assertThat(closedReassignment.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        String secondLead=createInterestedLead(bdr,orgId,contactId,"Payroll evaluation");
        String secondSql=createAndSubmitSql(bdr,secondLead,orgId,contactId,12000);
        var unapproved=http.exchange(root()+"/sales-manager/sql-qualifications/"+secondSql+"/assign",HttpMethod.POST,new HttpEntity<>(Map.of("aeId",aeTwo.path("id").asText()),headers(manager)),JsonNode.class);
        assertThat(unapproved.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        JsonNode dashboard=get(manager,"/sales-manager/dashboard");
        assertThat(dashboard.path("totalBdrs").asInt()).isEqualTo(1);
        assertThat(dashboard.path("totalAes").asInt()).isEqualTo(3);
        assertThat(dashboard.path("sqlsPendingReview").asInt()).isEqualTo(1);
        assertThat(dashboard.path("opportunitiesAssigned").asInt()).isEqualTo(1);
        assertThat(dashboard.path("teamConversionRate").asDouble()).isEqualTo(100.0);
        assertThat(dashboard.path("expiredCustomerAccounts").isNull()).isTrue();
        JsonNode metrics=get(manager,"/sales-manager/team-performance");
        assertThat(metrics.path("bdrs").get(0).path("sqlsGenerated").asInt()).isEqualTo(2);
        assertThat(metrics.path("aes").get(1).path("meetingsCompleted").asInt()).isEqualTo(2);
        assertThat(metrics.path("aes").get(1).path("dealsWon").asInt()).isEqualTo(1);
        assertThat(metrics.path("aes").get(1).path("revenueGenerated").asDouble()).isEqualTo(24000.0);
        var crossTeam=http.exchange(root()+"/sales-manager/opportunities/"+opportunityId+"/assignment-history",HttpMethod.GET,new HttpEntity<>(headers(otherManager)),JsonNode.class);
        assertThat(crossTeam.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(crossTeam.getBody().size()).isEqualTo(2);
        var forbidden=http.exchange(root()+"/sales-manager/dashboard",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class);
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private String createInterestedLead(String bdr,String orgId,String contactId,String title){
        JsonNode lead=post(bdr,"/bdr/leads",Map.of("title",title,"organisationId",orgId,"primaryContactId",contactId));
        post(bdr,"/bdr/leads/"+lead.path("id").asText()+"/calls",Map.of("contactId",contactId,"calledAt",Instant.now().toString(),"outcome","INTERESTED","notes","Interested"));
        return lead.path("id").asText();
    }
    private String createAndSubmitSql(String bdr,String leadId,String orgId,String contactId,int value){
        Map<String,Object> input=new HashMap<>();input.put("leadId",leadId);input.put("organisationId",orgId);input.put("primaryContactId",contactId);
        input.put("businessRequirement","Automate core operations");input.put("decisionMakerIdentified",true);
        input.put("decisionMakerDetails","Morgan, VP Operations");input.put("estimatedDealValue",value);input.put("expectedTimeline","This quarter");
        JsonNode draft=post(bdr,"/bdr/sql-qualifications",input);String id=draft.path("qualification").path("id").asText();
        post(bdr,"/bdr/sql-qualifications/"+id+"/submit",Map.of());return id;
    }
    private JsonNode createUser(String admin,String name,String email,String role,String managerId){
        Map<String,Object> input=new HashMap<>();input.put("fullName",name);input.put("email",email);input.put("role",role);
        input.put("temporaryPassword","TemporaryPassword123!");input.put("managerId",managerId);
        return post(admin,"/admin/users",input);
    }
    private String login(String email,String password){return http.postForEntity(root()+"/auth/login",Map.of("email",email,"password",password),JsonNode.class).getBody().path("accessToken").asText();}
    private JsonNode get(String token,String path){return http.exchange(root()+path,HttpMethod.GET,new HttpEntity<>(headers(token)),JsonNode.class).getBody();}
    private JsonNode post(String token,String path,Object body){return http.exchange(root()+path,HttpMethod.POST,new HttpEntity<>(body,headers(token)),JsonNode.class).getBody();}
    private JsonNode put(String token,String path,Object body){return http.exchange(root()+path,HttpMethod.PUT,new HttpEntity<>(body,headers(token)),JsonNode.class).getBody();}
    private HttpHeaders headers(String token){HttpHeaders h=new HttpHeaders();h.setBearerAuth(token);return h;}
}
