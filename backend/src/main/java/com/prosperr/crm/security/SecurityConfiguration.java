package com.prosperr.crm.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.identity.security.JwtAuthenticationFilter;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfiguration {
    @Bean JwtDecoder jwtDecoder(@Value("${app.security.jwt-secret-base64:}") String secret) {
        byte[] key = Base64.getDecoder().decode(secret); if (key.length < 32) throw new IllegalStateException("APP_JWT_SECRET_BASE64 must be at least 32 bytes");
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256")).macAlgorithm(MacAlgorithm.HS256).build();
    }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder decoder, UserRepository users) throws Exception {
        http.csrf(c -> c.disable()).cors(c -> {}).sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a.requestMatchers("/api/v1/auth/login", "/actuator/health", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/bdr/sql-submissions/**").hasAnyRole("BDR", "SALES_MANAGER")
                .requestMatchers("/api/v1/bdr/sql-submissions/**").hasRole("BDR")
                .requestMatchers("/api/v1/bdr/sql-qualifications/**").hasRole("BDR")
                .requestMatchers("/api/v1/sales-manager/sql-qualifications/**").hasRole("SALES_MANAGER")
                .requestMatchers("/api/v1/sales-manager/**").hasRole("SALES_MANAGER")
                .requestMatchers("/api/v1/leadership/**").hasRole("LEADERSHIP")
                .requestMatchers("/api/v1/ae/**").hasRole("AE")
                .requestMatchers("/api/v1/bdr/**").hasRole("BDR")
                .requestMatchers(HttpMethod.GET, "/api/v1/organisations/**", "/api/v1/contacts/**").hasAnyRole("ADMIN", "BDR", "SALES_MANAGER", "AE", "LEADERSHIP")
                .requestMatchers("/api/v1/organisations/**", "/api/v1/contacts/**").hasAnyRole("ADMIN", "BDR")
                .requestMatchers("/api/**").authenticated().anyRequest().permitAll())
            .exceptionHandling(e -> e.authenticationEntryPoint((request,response,exception) -> response.sendError(401)))
            .addFilterBefore(new JwtAuthenticationFilter(decoder, users), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origin:http://localhost:5173}") String origin) {
        CorsConfiguration c = new CorsConfiguration(); c.setAllowedOrigins(java.util.List.of(origin)); c.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS")); c.setAllowedHeaders(java.util.List.of("Authorization","Content-Type"));
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource(); s.registerCorsConfiguration("/**", c); return s;
    }
}
