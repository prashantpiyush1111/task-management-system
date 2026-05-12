package com.TaskManagement.Client;
 
import java.util.Set;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

 
import com.TaskManagement.Enum.Role;
 
@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
 
	@GetMapping("/api/users/{email}/roles")
	Set<Role> getRoles(@PathVariable("email") String officialEmail);
}