package com.TaskManagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.EmployeeStatusDTO;
import com.TaskManagement.Service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/pending-users")
	public ResponseEntity<List<EmployeeStatusDTO>> getPendingUsers(Authentication authentication) {
		return ResponseEntity.ok(
				adminService.getPendingUsers(authentication.getName())
		);
	}

	@PutMapping("/approve/{userId}")
	public ResponseEntity<String> approveUser(
			@PathVariable Long userId,
			Authentication authentication) {
		adminService.approveUser(userId, authentication.getName());
		return ResponseEntity.ok("User approved successfully");
	}

	@PutMapping("/reject/{userId}")
	public ResponseEntity<String> rejectUser(
			@PathVariable Long userId,
			Authentication authentication) {
		adminService.rejectUser(userId, authentication.getName());
		return ResponseEntity.ok("User rejected successfully");
	}
	@PutMapping("/remove/{userId}")
	public ResponseEntity<String> removeUser(
	        @PathVariable Long userId,
	        Authentication authentication) {

	    adminService.removeUser(userId, authentication.getName());

	    return ResponseEntity.ok("User removed from team successfully");
	}
	@GetMapping("/employees")
	public ResponseEntity<List<EmployeeStatusDTO>> getEmployees(
	        Authentication authentication) {

	    return ResponseEntity.ok(
	            adminService.getEmployees(authentication.getName())
	    );
	}
}