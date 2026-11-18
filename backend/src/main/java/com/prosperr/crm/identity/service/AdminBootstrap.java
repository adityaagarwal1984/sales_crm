package com.prosperr.crm.identity.service;
import com.prosperr.crm.identity.domain.*; import com.prosperr.crm.identity.repository.*; import java.util.Locale;
import org.springframework.beans.factory.annotation.Value; import org.springframework.boot.ApplicationArguments; import org.springframework.boot.ApplicationRunner; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Component;
@Component public class AdminBootstrap implements ApplicationRunner {
 private final UserRepository users; private final RoleRepository roles; private final PasswordEncoder encoder; private final String email,password;
 public AdminBootstrap(UserRepository u,RoleRepository r,PasswordEncoder p,@Value("${app.bootstrap.admin-email:}") String e,@Value("${app.bootstrap.admin-password:}") String pass){users=u;roles=r;encoder=p;email=e;password=pass;}
 public void run(ApplicationArguments args){if(users.existsByRole_Code(RoleCode.ADMIN))return;if(email.isBlank()||password.length()<12)throw new IllegalStateException("Set APP_BOOTSTRAP_ADMIN_EMAIL and APP_BOOTSTRAP_ADMIN_PASSWORD (at least 12 characters) for the first startup");String normalized=email.trim().toLowerCase(Locale.ROOT);if(users.existsByEmailIgnoreCase(normalized))throw new IllegalStateException("Bootstrap admin email already belongs to a non-admin account");users.save(new UserEntity("Administrator",normalized,encoder.encode(password),roles.findById(RoleCode.ADMIN).orElseThrow(),true));}
}
