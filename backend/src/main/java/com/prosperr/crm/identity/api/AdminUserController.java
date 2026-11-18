package com.prosperr.crm.identity.api;
import com.prosperr.crm.identity.domain.RoleCode;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.identity.service.AdminUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin") public class AdminUserController {
 private final AdminUserService service; private final UserRepository users;
 public AdminUserController(AdminUserService s,UserRepository u){service=s;users=u;}
 public record StatusInput(Boolean active){} public record ResetInput(@Size(min=12,max=72) String temporaryPassword){}
 @GetMapping("/users") public List<AdminUserService.UserDto> users(){return service.list();}
 @GetMapping("/users/{id}") public AdminUserService.UserDto get(@PathVariable UUID id){return service.get(id);}
 @PostMapping("/users") public AdminUserService.UserDto create(@Valid @RequestBody AdminUserService.UserInput i){return service.create(i);}
 @PutMapping("/users/{id}") public AdminUserService.UserDto update(@PathVariable UUID id,@Valid @RequestBody AdminUserService.UserInput i){return service.update(id,i);}
 @PatchMapping("/users/{id}/status") public AdminUserService.UserDto status(@PathVariable UUID id,@RequestBody StatusInput i){if(i==null||i.active()==null)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Active status is required");return service.status(id,i.active());}
 @PostMapping("/users/{id}/password-reset") public Map<String,String> reset(@PathVariable UUID id,@Valid @RequestBody ResetInput i){if(i==null)throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST,"Temporary password is required");service.reset(id,i.temporaryPassword());return Map.of("message","Temporary password set. User must change it at next login.");}
 @GetMapping("/dashboard") public Map<String,Object> dashboard(){return Map.of("totalUsers",users.count(),"activeUsers",users.countByRole_CodeAndActiveTrue(RoleCode.ADMIN)+users.countByRole_CodeAndActiveTrue(RoleCode.BDR)+users.countByRole_CodeAndActiveTrue(RoleCode.SALES_MANAGER)+users.countByRole_CodeAndActiveTrue(RoleCode.AE)+users.countByRole_CodeAndActiveTrue(RoleCode.LEADERSHIP),"roles",Arrays.stream(RoleCode.values()).map(Enum::name).toList());}
}
