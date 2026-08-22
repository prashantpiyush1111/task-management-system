package com.TaskManagement.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.TaskManagement.DTO.AuthResponseDTO;
import com.TaskManagement.DTO.RegisterRequestDTO;
import com.TaskManagement.DTO.LoginRequestDTO;
import com.TaskManagement.Entity.Organization;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Entity.UserProfileUpdate;
import com.TaskManagement.Enum.OrganizationStatus;
import com.TaskManagement.Enum.Role;
import com.TaskManagement.Enum.UserStatus;
import com.TaskManagement.Repository.OrganizationRepository;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Repository.UserProfileUpdateRepository;
import com.TaskManagement.Security.EmailService;
import com.TaskManagement.Security.JWTUtil;

@Service
public class UserAuthService {

	@Autowired
	private UserAuthRepository userAuthRepo;

	@Autowired
	private OrganizationRepository organizationRepo;

	@Autowired
	private UserProfileUpdateRepository userProfileUpdateRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private EmailService emailService;

	public AuthResponseDTO register(RegisterRequestDTO register) {

		Optional<UserAuth> existing = userAuthRepo
				.findByUserOfficialEmailIgnoreCase(register.getUserOfficialEmail());

		if (existing.isPresent()) {
			throw new RuntimeException("User already exists");
		}

		Organization organization = organizationRepo
				.findById(register.getOrganizationId())
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		if (organization.getStatus() != OrganizationStatus.ACTIVE) {
			throw new RuntimeException("Organization is not active");
		}

		// ADMIN accounts are only created via organization creation
		// (first-admin bootstrap) — never through open self-registration.
		if (register.getRole() == Role.ADMIN) {
			throw new RuntimeException("Cannot self-register as ADMIN");
		}

		UserAuth user = new UserAuth();
		user.setUserName(register.getUserName());
		user.setUserOfficialEmail(register.getUserOfficialEmail());
		user.setPassword(passwordEncoder.encode(register.getPassword()));
		user.setRole(register.getRole());
		user.setOrganization(organization);
		user.setStatus(UserStatus.PENDING);

		userAuthRepo.save(user);

		// Create default profile for the newly registered user
		UserProfileUpdate profile = UserProfileUpdate.builder()
		        .userOfficialEmail(user.getUserOfficialEmail())
		        .organizationId(organization.getId())
		        .active(true)
		        .build();

		userProfileUpdateRepo.save(profile);

		// No token issued yet — account is PENDING until an organization admin approves it.
		return new AuthResponseDTO(null, "Registration successful. Awaiting admin approval.");
	}

	public AuthResponseDTO login(LoginRequestDTO login) {

		UserAuth user = userAuthRepo
				.findByUserOfficialEmailIgnoreCase(login.getUserOfficialEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		if (user.getStatus() == UserStatus.PENDING) {
			throw new RuntimeException("Your account is awaiting admin approval");
		}

		if (user.getStatus() == UserStatus.REJECTED) {
			throw new RuntimeException("Your registration was rejected. Contact your organization admin");
		}
		if (user.getStatus() == UserStatus.INACTIVE) {
		    throw new RuntimeException("Your account has been deactivated. Contact your organization admin");
		}

		String token = jwtUtil.generateToken(user);

		return new AuthResponseDTO(token, "Login Successful");
	}

	public void forgotPassword(String userOfficialEmail) {

	    userAuthRepo.findByUserOfficialEmailIgnoreCase(userOfficialEmail)
	        .ifPresent(user -> {
	            String token = UUID.randomUUID().toString();
	            user.setResetToken(token);
	            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
	            userAuthRepo.save(user);
	            emailService.sendResetPassword(userOfficialEmail, token);
	        });
	}

	public void resetPassword(String token, String newPassword) {

		UserAuth user = userAuthRepo
				.findByResetToken(token)
				.orElseThrow(() -> new RuntimeException("Invalid token"));

		if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Token expired");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetToken(null);
		user.setResetTokenExpiry(null);

		userAuthRepo.save(user);
	}
}