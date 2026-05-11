package com.TaskManagement.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.DTO.UserProfileUpdateDTO;
import com.TaskManagement.Entity.UserProfileUpdate;
import com.TaskManagement.Repository.UserProfileUpdateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateService {

	@Autowired
	private UserProfileUpdateRepository userProfileRepo;

	public UserProfileUpdate updateUserProfile(UserProfileUpdateDTO updateProfile) {

		UserProfileUpdate user = userProfileRepo.findByUserOfficialEmail(updateProfile.userOfficialEmail)
				.orElseThrow(() -> new RuntimeException("User not ound"));

		user.setDepartment(updateProfile.department);
		user.setDesignation(updateProfile.designation);
		user.setOrganizationName(updateProfile.organizationName);
		user.setActive(updateProfile.active);

		return userProfileRepo.save(user);
	}

	public List<UserProfileUpdateDTO> getAllUserProfile() {
		return userProfileRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	public UserProfileUpdateDTO getUserProfileByEmail(String userOfficialEmail) {
		UserProfileUpdate user = userProfileRepo.findByUserOfficialEmail(userOfficialEmail)
				.orElseThrow(() -> new RuntimeException("User not ound"));
		return toDTO(user);

	}

	private UserProfileUpdateDTO toDTO(UserProfileUpdate profileUpdate) {

		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();
		dto.setUserOfficialEmail(profileUpdate.getUserOfficialEmail());
		dto.setDepartment(profileUpdate.getDepartment());
		dto.setDesignation(profileUpdate.getDesignation());
		dto.setOrganizationName(profileUpdate.getOrganizationName());
		dto.setActive(profileUpdate.isActive());

		return dto;

	}
}
