package com.TaskManagement.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.TaskManagement.DTO.UserProfileUpdateDTO;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Entity.UserProfileUpdate;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Repository.UserProfileUpdateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileUpdateService {

	private final UserProfileUpdateRepository userProfileRepo;
	private final UserAuthRepository userAuthRepo;

	public UserProfileUpdate updateUserProfile(
			String userOfficialEmail,
			UserProfileUpdateDTO updateProfile,
			Long organizationId) {

		UserProfileUpdate user = userProfileRepo
				.findByUserOfficialEmail(userOfficialEmail)
				.orElseGet(() -> createMissingProfile(
						userOfficialEmail,
						organizationId));

		if (!organizationId.equals(user.getOrganizationId())) {
			throw new RuntimeException("User not Found");
		}

		user.setDepartment(updateProfile.department);
		user.setDesignation(updateProfile.designation);
		user.setActive(updateProfile.active);

		return userProfileRepo.save(user);
	}

	public List<UserProfileUpdateDTO> getAllUserProfile(Long organizationId) {

		return userProfileRepo.findAll().stream()
				.filter(u -> organizationId.equals(u.getOrganizationId()))
				.map(this::toDTO)
				.collect(Collectors.toList());
	}

	public UserProfileUpdateDTO getUserProfileByEmail(
			String userOfficialEmail,
			Long organizationId) {

		UserProfileUpdate user = userProfileRepo
				.findByUserOfficialEmail(userOfficialEmail)
				.orElseGet(() -> createMissingProfile(
						userOfficialEmail,
						organizationId));

		if (!organizationId.equals(user.getOrganizationId())) {
			throw new RuntimeException("User not Found");
		}

		return toDTO(user);
	}

	private UserProfileUpdate createMissingProfile(
			String userOfficialEmail,
			Long organizationId) {

		UserAuth userAuth = userAuthRepo
				.findByUserOfficialEmail(userOfficialEmail)
				.orElseThrow(() -> new RuntimeException("User not Found"));

		if (userAuth.getOrganization() == null
				|| !organizationId.equals(userAuth.getOrganization().getId())) {
			throw new RuntimeException("User not Found");
		}

		UserProfileUpdate profile = UserProfileUpdate.builder()
				.userOfficialEmail(userOfficialEmail)
				.organizationId(organizationId)
				.active(true)
				.build();

		return userProfileRepo.save(profile);
	}

	private UserProfileUpdateDTO toDTO(
			UserProfileUpdate profileUpdate) {

		UserProfileUpdateDTO dto = new UserProfileUpdateDTO();

		dto.setUserOfficialEmail(
				profileUpdate.getUserOfficialEmail());

		dto.setDepartment(
				profileUpdate.getDepartment());

		dto.setDesignation(
				profileUpdate.getDesignation());

		dto.setActive(
				profileUpdate.isActive());

		return dto;
	}
}