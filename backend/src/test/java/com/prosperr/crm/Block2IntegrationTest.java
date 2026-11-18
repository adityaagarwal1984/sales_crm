package com.prosperr.crm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.*;
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
class Block2IntegrationTest {
    @LocalServerPort int port;
    @Autowired TestRestTemplate http;
    private String root(){return "http://localhost:"+port+"/api/v1";}
    @DynamicPropertySource static void properties(DynamicPropertyRegistry r){
        r.add("app.security.jwt-secret-base64",()->"MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");
        r.add("app.bootstrap.admin-email",()->"admin@prosperr.test");r.add("app.bootstrap.admin-password",()->"AdminTemporaryPass123!");
    }
    @Test void loginPermissionsDeactivationDuplicateAndJwtExpiry() throws Exception {
        var good=http.postForEntity(root()+"/auth/login",Map.of("email","ADMIN@PROSPERR.TEST ","password","AdminTemporaryPass123!"),JsonNode.class);
        assertThat(good.getStatusCode()).isEqualTo(HttpStatus.OK);String admin=good.getBody().path("accessToken").asText();
        assertThat(good.getBody().path("user").has("passwordHash")).isFalse();
        var wrong=http.postForEntity(root()+"/auth/login",Map.of("email","admin@prosperr.test","password","wrong"),JsonNode.class);assertThat(wrong.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        var create=http.exchange(root()+"/admin/users",HttpMethod.POST,new HttpEntity<>(Map.of("fullName","BDR User","email","bdr@prosperr.test","role","BDR","temporaryPassword","BdrTemporaryPass123!"),headers(admin)),JsonNode.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.OK);String id=create.getBody().path("id").asText();
        assertThat(create.getBody().path("mustChangePassword").asBoolean()).isTrue();
        assertThat(http.exchange(root()+"/admin/users",HttpMethod.GET,new HttpEntity<>(headers(admin)),JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.OK);
        var duplicate=http.exchange(root()+"/admin/users",HttpMethod.POST,new HttpEntity<>(Map.of("fullName","Duplicate","email"," BDR@PROSPERR.TEST ","role","AE","temporaryPassword","BdrTemporaryPass123!"),headers(admin)),JsonNode.class);assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        var bdrLogin=http.postForEntity(root()+"/auth/login",Map.of("email","bdr@prosperr.test","password","BdrTemporaryPass123!"),JsonNode.class);String initialBdr=bdrLogin.getBody().path("accessToken").asText();
        var changed=http.exchange(root()+"/auth/change-password",HttpMethod.POST,new HttpEntity<>(Map.of("currentPassword","BdrTemporaryPass123!","newPassword","BdrPermanentPassword456!"),headers(initialBdr)),JsonNode.class);
        assertThat(changed.getStatusCode()).isEqualTo(HttpStatus.OK);String bdr=changed.getBody().path("accessToken").asText();assertThat(changed.getBody().path("user").path("mustChangePassword").asBoolean()).isFalse();
        assertThat(http.exchange(root()+"/admin/users",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(http.exchange(root()+"/admin/users/"+id+"/status",HttpMethod.PATCH,new HttpEntity<>(Map.of("active",false),headers(admin)),JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(http.exchange(root()+"/auth/me",HttpMethod.GET,new HttpEntity<>(headers(bdr)),JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(http.postForEntity(root()+"/auth/login",Map.of("email","bdr@prosperr.test","password","BdrPermanentPassword456!"),JsonNode.class).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        String expired=expiredToken();var expiredResponse=http.exchange(root()+"/auth/me",HttpMethod.GET,new HttpEntity<>(headers(expired)),JsonNode.class);assertThat(expiredResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    private HttpHeaders headers(String token){HttpHeaders h=new HttpHeaders();h.setBearerAuth(token);return h;}
    private String expiredToken() throws Exception {
        byte[] secret=java.util.Base64.getDecoder().decode("MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=");
        var claims=new com.nimbusds.jwt.JWTClaimsSet.Builder().issuer("prosperr-sales-crm").subject("00000000-0000-0000-0000-000000000000").issueTime(new java.util.Date(System.currentTimeMillis()-60000)).expirationTime(new java.util.Date(System.currentTimeMillis()-1000)).build();
        var signed=new com.nimbusds.jwt.SignedJWT(new com.nimbusds.jose.JWSHeader(com.nimbusds.jose.JWSAlgorithm.HS256),claims);signed.sign(new com.nimbusds.jose.crypto.MACSigner(secret));return signed.serialize();
    }
}
