package com.TaskManagement.Entity;

import com.TaskManagement.Enum.Role;
import com.TaskManagement.Enum.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserAuth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String userName;

	@Column(unique = true, nullable = false)
	private String userOfficialEmail;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id", nullable = false)
	private Organization organization;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private UserStatus status = UserStatus.PENDING;

	private String resetToken;

	private LocalDateTime resetTokenExpiry;
}