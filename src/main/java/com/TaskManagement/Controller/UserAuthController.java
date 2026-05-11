package com.TaskManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;

// Used to send HTTP responses
import org.springframework.http.ResponseEntity;

// Used for REST API mappings
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.AuthResponseDTO; // DTO for authentication response
import com.TaskManagement.DTO.RegisterRequestDTO; // DTO for register request
import com.TaskManagement.DTO.LoginRequestDTO; // DTO for login request
import com.TaskManagement.Service.UserAuthService; // Service layer for user authentication

@RestController

@RequestMapping("/api/user-auth")
public class UserAuthController {

	@Autowired
	private UserAuthService userAuthService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO register) {

		return ResponseEntity.ok(userAuthService.register(register));
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO login) {

		return ResponseEntity.ok(userAuthService.login(login));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestParam String userOfficialEmail) {

		userAuthService.forgotPassword(userOfficialEmail);

		return ResponseEntity.ok("Reset email sent successfully");
	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {

		userAuthService.resetPassword(token, newPassword);

		return ResponseEntity.ok("Password reset successfully");
	}
}