package com.TaskManagement.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaskManagement.DTO.AuthResponseDTO;
import com.TaskManagement.DTO.OrganizationRequestDTO;
import com.TaskManagement.Entity.Organization;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Enum.OrganizationStatus;
import com.TaskManagement.Enum.Role;
import com.TaskManagement.Enum.UserStatus;
import com.TaskManagement.Repository.OrganizationRepository;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Security.JWTUtil;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	private UserAuthRepository userAuthRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTUtil jwtUtil;

	/**
	 * Creates a new Organization AND its first ADMIN user in a single
	 * transaction. This avoids the chicken-and-egg problem where an
	 * organization exists but has no admin to approve subsequent employees.
	 */
	@Transactional
	public AuthResponseDTO createOrganizationWithAdmin(OrganizationRequestDTO request) {

		if (organizationRepository.existsByName(request.getOrganizationName())) {
			throw new RuntimeException("Organization already exists");
		}

		if (request.getOrganizationEmail() != null
				&& organizationRepository.existsByEmail(request.getOrganizationEmail())) {
			throw new RuntimeException("Organization email already exists");
		}

		if (userAuthRepository.findByUserOfficialEmailIgnoreCase(request.getAdminEmail()).isPresent()) {
			throw new RuntimeException("User already exists");
		}

		// Self-serve model: organization is ACTIVE immediately, the first
		// user becomes its ADMIN and is ACTIVE immediately (no one else
		// exists yet to approve them).
		Organization organization = Organization.builder()
				.name(request.getOrganizationName())
				.email(request.getOrganizationEmail())
				.status(OrganizationStatus.ACTIVE)
				.build();

		organization = organizationRepository.save(organization);

		UserAuth admin = UserAuth.builder()
				.userName(request.getAdminUserName())
				.userOfficialEmail(request.getAdminEmail())
				.password(passwordEncoder.encode(request.getAdminPassword()))
				.role(Role.ADMIN)
				.organization(organization)
				.status(UserStatus.ACTIVE)
				.build();

		userAuthRepository.save(admin);

		String token = jwtUtil.generateToken(admin);

		return new AuthResponseDTO(token, "Organization and admin account created successfully");
	}

	public List<Organization> listActiveOrganizations() {
		return organizationRepository.findAll().stream()
				.filter(org -> org.getStatus() == OrganizationStatus.ACTIVE)
				.toList();
	}
}