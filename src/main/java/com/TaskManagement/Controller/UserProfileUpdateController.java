package com.TaskManagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.UserProfileUpdateDTO;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Entity.UserProfileUpdate;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.UserProfileUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-profile-update")
@RequiredArgsConstructor
public class UserProfileUpdateController {

	@Autowired
	private UserProfileUpdateService userProfileUpdateService;

	@Autowired
	private UserAuthRepository userAuthRepository;

	private Long resolveOrganizationId(Authentication authentication) {
		UserAuth user = userAuthRepository.findByUserOfficialEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));

		return user.getOrganization().getId();
	}

	@PutMapping("/update/{userOfficialEmail}")
	public ResponseEntity<UserProfileUpdate> updateUserProfile(
			@PathVariable String userOfficialEmail,
			@RequestBody UserProfileUpdateDTO updateProfile,
			Authentication authentication) {

		Long organizationId = resolveOrganizationId(authentication);

		return ResponseEntity.ok(
				userProfileUpdateService.updateUserProfile(
						userOfficialEmail,
						updateProfile,
						organizationId));
	}

	@PatchMapping("/edit/{userOfficialEmail}")
	public ResponseEntity<UserProfileUpdate> editUserProfile(
			@PathVariable String userOfficialEmail,
			@RequestBody UserProfileUpdateDTO editProfile,
			Authentication authentication) {

		Long organizationId = resolveOrganizationId(authentication);

		return ResponseEntity.ok(
				userProfileUpdateService.updateUserProfile(
						userOfficialEmail,
						editProfile,
						organizationId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<UserProfileUpdateDTO>> getAllUserProfile(
			Authentication authentication) {

		Long organizationId = resolveOrganizationId(authentication);

		return ResponseEntity.ok(
				userProfileUpdateService.getAllUserProfile(organizationId));
	}

	@GetMapping("/{userOfficialEmail}")
	public ResponseEntity<UserProfileUpdateDTO> getUserProfileByEmail(
			@PathVariable String userOfficialEmail,
			Authentication authentication) {

		Long organizationId = resolveOrganizationId(authentication);

		return ResponseEntity.ok(
				userProfileUpdateService.getUserProfileByEmail(
						userOfficialEmail,
						organizationId));
	}
}