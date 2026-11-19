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
class Block8LeadershipAnalyticsIntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate http;
    private String root(){return "http://localhost:"+port+"/api/v1";}
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",()->"jdbc:h2:mem:prosperr_block8_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        r.add("app.security.jwt-secret-base64",()->"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");
        r.add("app.bootstrap.admin-email",()->"admin@prosperr.test");
        r.add("app.bootstrap.admin-password",()->"AdminTemporaryPass123!");
    }

    @Test void leadershipAnalyticsUsesRealAggregatesAndAppliesFiltersAndRoleChecks(){
        String admin=login("admin@prosperr.test","AdminTemporaryPass123!");
        JsonNode managerUser=createUser(admin,"Analytics Manager","manager8@prosperr.test","SALES_MANAGER");
        JsonNode bdrUser=createUser(admin,"Analytics BDR","bdr8@prosperr.test","BDR");
        JsonNode aeUser=createUser(admin,"Analytics AE","ae8@prosperr.test","AE");
        JsonNode leaderUser=createUser(admin,"Analytics Leader","leader8@prosperr.test","LEADERSHIP");
        String manager=login("manager8@prosperr.test","TemporaryPassword123!");
        String bdr=login("bdr8@prosperr.test","TemporaryPassword123!");
        String ae=login("ae8@prosperr.test","TemporaryPassword123!");
        String leadership=login("leader8@prosperr.test","TemporaryPassword123!");
        JsonNode org=post(bdr,"/organisations",Map.of("name","Analytics Org","leadSource","Referral"));
        JsonNode contact=post(bdr,"/organisations/"+org.path("id").asText()+"/contacts",Map.of("name","Jamie Analytics","email","jamie@analytics.test"));
        JsonNode lead=post(bdr,"/bdr/leads",Map.of("title","Analytics Opportunity","organisationId",org.path("id").asText(),"primaryContactId",contact.path("id").asText()));
        post(bdr,"/bdr/leads/"+lead.path("id").asText()+"/calls",Map.of("contactId",contact.path("id").asText(),"calledAt",Instant.now().toString(),"outcome","INTERESTED","notes","Requested demo"));
        Map<String,Object> qualification=new HashMap<>();qualification.put("leadId",lead.path("id").asText());qualification.put("organisationId",org.path("id").asText());qualification.put("primaryContactId",contact.path("id").asText());qualification.put("businessRequirement","Reduce manual processing");qualification.put("decisionMakerIdentified",true);qualification.put("decisionMakerDetails","Jamie, COO");qualification.put("estimatedDealValue",50000);qualification.put("expectedTimeline","This quarter");
        String sqlId=post(bdr,"/bdr/sql-qualifications",qualification).path("qualification").path("id").asText();
        post(bdr,"/bdr/sql-qualifications/"+sqlId+"/submit",Map.of());
        post(manager,"/sales-manager/sql-qualifications/"+sqlId+"/approve",Map.of());
        JsonNode opportunity=post(manager,"/sales-manager/sql-qualifications/"+sqlId+"/assign",Map.of("aeId",aeUser.path("id").asText()));
        String id=opportunity.path("id").asText();
        post(ae,"/ae/opportunities/"+id+"/meetings",meeting("L1","L1"));
        post(ae,"/ae/opportunities/"+id+"/meetings",meeting("L2","L2"));
        patch(ae,"/ae/opportunities/"+id+"/close-won",Map.of("finalDealValue",55000,"closedAt",Instant.now().toString(),"commercialDetails","Annual SaaS contract"));

        JsonNode overview=get(leadership,"/leadership/analytics/overview");
        assertThat(overview.path("totalOrganisations").asInt()).isEqualTo(1);
        assertThat(overview.path("totalContacts").asInt()).isEqualTo(1);
        assertThat(overview.path("activeLeads").asInt()).isEqualTo(1);
        assertThat(overview.path("totalSqls").asInt()).isEqualTo(1);
        assertThat(overview.path("totalOpportunities").asInt()).isEqualTo(1);
        assertThat(overview.path("totalWonCustomers").asInt()).isEqualTo(1);
        assertThat(overview.path("totalRevenue").asDouble()).isEqualTo(55000.0);
        JsonNode bdrs=get(leadership,"/leadership/analytics/bdrs");
        assertThat(bdrs.get(0).path("interestedProspects").asInt()).isEqualTo(1);
        assertThat(bdrs.get(0).path("sqlConversionPercent").asDouble()).isEqualTo(100.0);
        JsonNode managers=get(leadership,"/leadership/analytics/sales-managers");
        assertThat(managers.get(0).path("sqlApprovalRate").asDouble()).isEqualTo(100.0);
        assertThat(managers.get(0).path("opportunitiesAllocated").asInt()).isEqualTo(1);
        JsonNode aes=get(leadership,"/leadership/analytics/aes");
        assertThat(aes.get(0).path("won").asInt()).isEqualTo(1);
        assertThat(aes.get(0).path("revenue").asDouble()).isEqualTo(55000.0);
        JsonNode funnel=get(leadership,"/leadership/analytics/funnel");
        assertThat(funnel.findValue("stage")).isNotNull();
        assertThat(funnel.size()).isEqualTo(11);
        JsonNode revenue=get(leadership,"/leadership/analytics/revenue");
        assertThat(revenue.path("monthly").size()).isEqualTo(1);
        assertThat(revenue.path("bySource").get(0).path("source").asText()).isEqualTo("Referral");
        JsonNode filtered=get(leadership,"/leadership/analytics/overview?department=AE&employeeId="+aeUser.path("id").asText()+"&stage=WON");
        assertThat(filtered.path("totalOpportunities").asInt()).isEqualTo(1);
        assertThat(filtered.path("totalRevenue").asDouble()).isEqualTo(55000.0);
        JsonNode filterOptions=get(leadership,"/leadership/analytics/filters");
        assertThat(filterOptions.path("employees").size()).isEqualTo(3);
        var forbidden=http.exchange(root()+"/leadership/analytics/overview",HttpMethod.GET,new HttpEntity<>(headers(ae)),JsonNode.class);
        assertThat(forbidden.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private Map<String,Object> meeting(String title,String level){Map<String,Object> m=new HashMap<>();m.put("title",title);m.put("meetingType","DISCOVERY");m.put("meetingLevel",level);m.put("completedAt",Instant.now().minusSeconds(30).toString());m.put("outcome","Customer accepted next step");return m;}
    private JsonNode createUser(String admin,String name,String email,String role){Map<String,Object> m=new HashMap<>();m.put("fullName",name);m.put("email",email);m.put("role",role);m.put("temporaryPassword","TemporaryPassword123!");return post(admin,"/admin/users",m);}
    private String login(String email,String password){return http.postForEntity(root()+"/auth/login",Map.of("email",email,"password",password),JsonNode.class).getBody().path("accessToken").asText();}
    private JsonNode get(String token,String path){return http.exchange(root()+path,HttpMethod.GET,new HttpEntity<>(headers(token)),JsonNode.class).getBody();}
    private JsonNode post(String token,String path,Object body){return http.exchange(root()+path,HttpMethod.POST,new HttpEntity<>(body,headers(token)),JsonNode.class).getBody();}
    private JsonNode patch(String token,String path,Object body){return http.exchange(root()+path,HttpMethod.PATCH,new HttpEntity<>(body,headers(token)),JsonNode.class).getBody();}
    private HttpHeaders headers(String token){HttpHeaders h=new HttpHeaders();h.setBearerAuth(token);return h;}
}
