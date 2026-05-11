
package com.TaskManagement.Client;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TaskManagement.Enum.Role;

public interface UserClient {
	@GetMapping("api/users/{email}/roles")
	Set<Role> getRoles(@RequestParam String officialEmail);

}
