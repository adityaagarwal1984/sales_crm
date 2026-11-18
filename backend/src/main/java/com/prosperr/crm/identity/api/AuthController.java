package com.prosperr.crm.identity.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.identity.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth") public class AuthController {
    private final AuthService auth; public AuthController(AuthService auth) { this.auth=auth; }
    public record LoginRequest(@NotBlank @Size(max=320) String email,@NotBlank @Size(max=72) String password) {}
    public record ChangePasswordRequest(@NotBlank @Size(max=72) String currentPassword,@NotBlank @Size(min=12,max=72) String newPassword) {}
    @PostMapping("/login") public AuthService.AuthResult login(@Valid @RequestBody LoginRequest r) { return auth.login(r.email(),r.password()); }
    @GetMapping("/me") public AuthService.UserView me(@AuthenticationPrincipal CrmPrincipal p) { return auth.current(p); }
    @PostMapping("/change-password") public AuthService.AuthResult change(@AuthenticationPrincipal CrmPrincipal p,@Valid @RequestBody ChangePasswordRequest r) { return auth.changePassword(p,r.currentPassword(),r.newPassword()); }
}
