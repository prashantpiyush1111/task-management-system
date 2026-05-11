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
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Security.EmailService;
import com.TaskManagement.Security.JWTUtil;

@Service
public class UserAuthService {

	@Autowired
	private UserAuthRepository userAuthRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private EmailService emailService;

	public AuthResponseDTO register(RegisterRequestDTO register) {

		Optional<UserAuth> existing = userAuthRepo.findByUserOfficialEmail(register.getUserOfficialEmail());

		if (existing.isPresent()) {
			throw new RuntimeException("User already exists");
		}

		UserAuth user = new UserAuth();
		user.setUserName(register.getUserName());
		user.setUserOfficialEmail(register.getUserOfficialEmail());
		user.setPassword(passwordEncoder.encode(register.getPassword()));
		user.setRole(register.getRole());

		userAuthRepo.save(user);

		String token = jwtUtil.generateToken(user);

		return new AuthResponseDTO(token, "User Registered Successfully");
	}

	public AuthResponseDTO login(LoginRequestDTO login) {

		UserAuth user = userAuthRepo.findByUserOfficialEmail(login.getUserOfficialEmail())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!passwordEncoder.matches(login.getPassword(), user.getPassword())) {

			throw new RuntimeException("Invalid credentials");
		}

		String token = jwtUtil.generateToken(user);

		return new AuthResponseDTO(token, "Login Successful");
	}

	public void forgotPassword(String userOfficialEmail) {

		UserAuth user = userAuthRepo.findByUserOfficialEmail(userOfficialEmail)
				.orElseThrow(() -> new RuntimeException("User not found"));

		String token = UUID.randomUUID().toString();

		user.setResetToken(token);
		user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));

		userAuthRepo.save(user);

		emailService.sendResetPassword(userOfficialEmail, token);
	}

	public void resetPassword(String token, String newPassword) {

		UserAuth user = userAuthRepo.findByResetToken(token).orElseThrow(() -> new RuntimeException("Invalid token"));

		if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {

			throw new RuntimeException("Token expired");
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		user.setResetToken(null);
		user.setResetTokenExpiry(null);

		userAuthRepo.save(user);
	}
}