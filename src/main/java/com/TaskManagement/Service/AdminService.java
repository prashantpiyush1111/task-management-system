package com.TaskManagement.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.DTO.EmployeeStatusDTO;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Enum.Role;
import com.TaskManagement.Enum.UserStatus;
import com.TaskManagement.Repository.UserAuthRepository;

@Service
public class AdminService {

	@Autowired
	private UserAuthRepository userAuthRepository;

	public List<EmployeeStatusDTO> getPendingUsers(String adminEmail) {

		Long organizationId = resolveAdminOrganizationId(adminEmail);

		return userAuthRepository
				.findByOrganizationIdAndStatus(organizationId, UserStatus.PENDING)
				.stream()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}

	public void approveUser(Long userId, String adminEmail) {

		Long organizationId = resolveAdminOrganizationId(adminEmail);

		UserAuth user = userAuthRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getOrganization() == null
				|| !user.getOrganization().getId().equals(organizationId)) {
			throw new RuntimeException("User does not belong to your organization");
		}

		user.setStatus(UserStatus.ACTIVE);
		userAuthRepository.save(user);
	}

	public void rejectUser(Long userId, String adminEmail) {

		Long organizationId = resolveAdminOrganizationId(adminEmail);

		UserAuth user = userAuthRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getOrganization() == null
				|| !user.getOrganization().getId().equals(organizationId)) {
			throw new RuntimeException("User does not belong to your organization");
		}

		user.setStatus(UserStatus.REJECTED);
		userAuthRepository.save(user);
	}

	public void removeUser(Long userId, String adminEmail) {

		Long organizationId = resolveAdminOrganizationId(adminEmail);

		UserAuth user = userAuthRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (user.getOrganization() == null
				|| !user.getOrganization().getId().equals(organizationId)) {
			throw new RuntimeException("User does not belong to your organization");
		}

		if (user.getRole() == Role.ADMIN) {
			throw new RuntimeException("Admin cannot be removed");
		}

		user.setStatus(UserStatus.INACTIVE);
		userAuthRepository.save(user);
	}

	/**
	 * Resolves the organizationId of the currently authenticated admin,
	 * based on their email from the JWT — never trust an organizationId
	 * supplied by the client.
	 */
	private Long resolveAdminOrganizationId(String adminEmail) {

		UserAuth admin = userAuthRepository.findByUserOfficialEmailIgnoreCase(adminEmail)
				.orElseThrow(() -> new RuntimeException("Admin account not found"));

		if (admin.getRole() != Role.ADMIN) {
			throw new RuntimeException("Only an organization admin can perform this action");
		}

		if (admin.getOrganization() == null) {
			throw new RuntimeException("Admin is not linked to any organization");
		}

		return admin.getOrganization().getId();
	}

	private EmployeeStatusDTO toDTO(UserAuth user) {

		return EmployeeStatusDTO.builder()
				.id(user.getId())
				.userName(user.getUserName())
				.userOfficialEmail(user.getUserOfficialEmail())
				.role(user.getRole())
				.status(user.getStatus())
				.build();
	}
	public List<EmployeeStatusDTO> getEmployees(String adminEmail) {

		Long organizationId = resolveAdminOrganizationId(adminEmail);

		return userAuthRepository
				 .findByOrganizationIdAndStatus(organizationId, UserStatus.ACTIVE)
				.stream()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}
}