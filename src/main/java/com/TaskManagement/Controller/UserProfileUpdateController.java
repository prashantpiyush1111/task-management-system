package com.TaskManagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.UserProfileUpdateDTO;
import com.TaskManagement.Entity.UserProfileUpdate;
import com.TaskManagement.Service.UserProfileUpdateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user_profile-update")
@RequiredArgsConstructor
public class UserProfileUpdateController {

	@Autowired
	private UserProfileUpdateService userProfileUpdateService;

	@PutMapping("/update/{userOfficialEmail}")
	public ResponseEntity<UserProfileUpdate> updateUserProfile(@PathVariable String userOfficialEmail,
			@RequestBody UserProfileUpdateDTO updateProfile) {
		return ResponseEntity.ok(userProfileUpdateService.updateUserProfile(updateProfile));
	}

	@PatchMapping("/edit/{userOfficialEmail}")
	public ResponseEntity<UserProfileUpdate> editUserProfile(@PathVariable String userOfficialEmail,
			@RequestBody UserProfileUpdateDTO editProfile) {
		return ResponseEntity.ok(userProfileUpdateService.updateUserProfile(editProfile));
	}

	@GetMapping("/all")
	public ResponseEntity<List<UserProfileUpdateDTO>> getAllUserProfile() {
		return ResponseEntity.ok(userProfileUpdateService.getAllUserProfile());
	}

	@GetMapping("/{userOfficialEmail}")
	public ResponseEntity<UserProfileUpdateDTO> getUserProfileByEmail(@PathVariable String userOfficialEmail) {
		return ResponseEntity.ok(userProfileUpdateService.getUserProfileByEmail(userOfficialEmail));
	}
}