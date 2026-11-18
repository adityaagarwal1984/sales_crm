package com.prosperr.crm.identity.security;

import com.prosperr.crm.identity.domain.UserEntity;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final JwtEncoder encoder;
    public JwtService(@Value("${app.security.jwt-secret-base64:}") String secret) {
        if (secret == null || secret.isBlank()) throw new IllegalStateException("APP_JWT_SECRET_BASE64 must contain a base64 encoded secret of at least 32 bytes");
        byte[] key = Base64.getDecoder().decode(secret);
        if (key.length < 32) throw new IllegalStateException("APP_JWT_SECRET_BASE64 must decode to at least 32 bytes");
        SecretKey secretKey = new SecretKeySpec(key, "HmacSHA256");
        this.encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }
    public String issue(UserEntity user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("prosperr-sales-crm").issuedAt(now).expiresAt(now.plusSeconds(900))
            .subject(user.getId().toString()).claim("role", user.getRole().getCode().name()).claim("email", user.getEmail()).build();
        return encoder.encode(JwtEncoderParameters.from(org.springframework.security.oauth2.jwt.JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }
}
