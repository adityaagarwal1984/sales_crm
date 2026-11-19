package com.prosperr.crm.identity.service;

import com.prosperr.crm.identity.domain.*;
import com.prosperr.crm.identity.repository.*;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service public class AdminUserService {
    private final UserRepository users; private final RoleRepository roles; private final PasswordEncoder passwords;
    public AdminUserService(UserRepository users,RoleRepository roles,PasswordEncoder passwords){this.users=users;this.roles=roles;this.passwords=passwords;}
    public record UserDto(UUID id,String fullName,String email,String role,boolean active,boolean mustChangePassword,Instant createdAt,UUID managerId,String managerName){}
    public record UserInput(String fullName,String email,RoleCode role,String temporaryPassword,UUID managerId){}
    public List<UserDto> list(){return users.findAll().stream().map(this::dto).toList();}
    public UserDto get(UUID id){return dto(user(id));}
    @Transactional public UserDto create(UserInput in){validate(in);String email=AuthService.normalize(in.email());if(users.existsByEmailIgnoreCase(email))throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");UserEntity u=new UserEntity(in.fullName().trim(),email,passwords.encode(in.temporaryPassword()),role(in.role()),true);u.assignToManager(manager(in.role(),in.managerId()));return dto(users.save(u));}
    @Transactional public UserDto update(UUID id,UserInput in){validateProfile(in);UserEntity u=user(id);String email=AuthService.normalize(in.email());if(users.findByEmailIgnoreCase(email).filter(other->!other.getId().equals(id)).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");if(u.isActive()&&u.getRole().getCode()==RoleCode.ADMIN&&in.role()!=RoleCode.ADMIN&&users.countByRole_CodeAndActiveTrue(RoleCode.ADMIN)<=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Cannot change the role of the last active administrator");u.updateProfile(in.fullName().trim(),email,role(in.role()),manager(in.role(),in.managerId(),id));return dto(u);}
    @Transactional public UserDto status(UUID id,boolean active){UserEntity u=user(id);if(!active&&u.getRole().getCode()==RoleCode.ADMIN&&users.countByRole_CodeAndActiveTrue(RoleCode.ADMIN)<=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Cannot deactivate the last active administrator");u.setActive(active);return dto(u);}
    @Transactional public void reset(UUID id,String temporaryPassword){validatePassword(temporaryPassword);user(id).setTemporaryPassword(passwords.encode(temporaryPassword),Instant.now());}
    private UserEntity user(UUID id){return users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));}
    private RoleEntity role(RoleCode c){return roles.findById(c).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown role"));}
    private UserEntity manager(RoleCode userRole,UUID managerId){if(userRole!=RoleCode.BDR&&userRole!=RoleCode.AE){if(managerId!=null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Only BDR and AE accounts can be assigned to a Sales Manager");return null;}if(managerId==null)return null;UserEntity manager=user(managerId);if(!manager.isActive()||manager.getRole().getCode()!=RoleCode.SALES_MANAGER)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Select an active Sales Manager");return manager;}
    private UserEntity manager(RoleCode userRole,UUID managerId,UUID subjectId){UserEntity manager=manager(userRole,managerId);if(manager!=null&&manager.getId().equals(subjectId))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A user cannot manage their own account");return manager;}
    private void validate(UserInput in){validateProfile(in);validatePassword(in.temporaryPassword());}
    private void validatePassword(String password){if(password==null||password.length()<12||password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length>72)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Temporary password must be at least 12 characters and at most 72 UTF-8 bytes");}
    private void validateProfile(UserInput in){if(in==null||in.fullName()==null||in.fullName().isBlank()||in.fullName().length()>120||in.email()==null||in.email().length()>320||!AuthService.normalize(in.email()).matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")||in.role()==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A valid name, email and role are required");}
    private UserDto dto(UserEntity u){return new UserDto(u.getId(),u.getFullName(),u.getEmail(),u.getRole().getCode().name(),u.isActive(),u.isMustChangePassword(),u.getCreatedAt(),u.getSalesManager()==null?null:u.getSalesManager().getId(),u.getSalesManager()==null?null:u.getSalesManager().getFullName());}
}
