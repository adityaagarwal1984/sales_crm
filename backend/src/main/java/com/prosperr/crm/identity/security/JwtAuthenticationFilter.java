package com.prosperr.crm.identity.security;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtDecoder decoder; private final UserRepository users;
    public JwtAuthenticationFilter(JwtDecoder decoder, UserRepository users) { this.decoder = decoder; this.users = users; }
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) try {
            Jwt jwt = decoder.decode(header.substring(7));
            Optional<UserEntity> found = users.findById(UUID.fromString(jwt.getSubject()));
            if (found.isPresent() && found.get().isActive()) {
                UserEntity user = found.get();
                var principal = new CrmPrincipal(user.getId(), user.getEmail(), user.getFullName(), user.getRole().getCode(), user.isMustChangePassword());
                var auth = new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getCode().name())));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (JwtException | IllegalArgumentException ignored) { SecurityContextHolder.clearContext(); }
        chain.doFilter(req, res);
    }
}
