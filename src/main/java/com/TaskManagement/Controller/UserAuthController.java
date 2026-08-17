package com.TaskManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.AuthResponseDTO;
import com.TaskManagement.DTO.RegisterRequestDTO;
import com.TaskManagement.DTO.LoginRequestDTO;
import com.TaskManagement.Service.UserAuthService;

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