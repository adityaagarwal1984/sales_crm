package com.prosperr.crm.identity.service;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.identity.security.JwtService;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) { this.users=users; this.encoder=encoder; this.jwt=jwt; }
    public record AuthResult(String accessToken, long expiresIn, UserView user) {}
    public record UserView(java.util.UUID id, String fullName, String email, String role, boolean active, boolean mustChangePassword) {
        static UserView from(UserEntity u) { return new UserView(u.getId(),u.getFullName(),u.getEmail(),u.getRole().getCode().name(),u.isActive(),u.isMustChangePassword()); }
    }
    public AuthResult login(String email, String password) {
        String normalized=normalize(email);
        if (!normalized.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") || password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new BadCredentialsException("Invalid email or password");
        UserEntity u=users.findByEmailIgnoreCase(normalized).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!u.isActive() || !encoder.matches(password,u.getPasswordHash())) throw new BadCredentialsException("Invalid email or password");
        return new AuthResult(jwt.issue(u),900,UserView.from(u));
    }
    public UserView current(CrmPrincipal p) { return users.findById(p.id()).map(UserView::from).orElseThrow(() -> new BadCredentialsException("Invalid email or password")); }
    @Transactional public AuthResult changePassword(CrmPrincipal p, String current, String next) {
        if (current.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new BadCredentialsException("Invalid current password");
        if (next.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"New password cannot exceed 72 UTF-8 bytes");
        UserEntity u=users.findById(p.id()).orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!encoder.matches(current,u.getPasswordHash())) throw new BadCredentialsException("Invalid current password");
        u.changePassword(encoder.encode(next), Instant.now()); return new AuthResult(jwt.issue(u),900,UserView.from(u));
    }
    public static String normalize(String email) { return email == null ? "" : email.trim().toLowerCase(java.util.Locale.ROOT); }
}
