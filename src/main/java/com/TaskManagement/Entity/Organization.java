package com.TaskManagement.Entity;

import java.time.LocalDateTime;

import com.TaskManagement.Enum.OrganizationStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private OrganizationStatus status = OrganizationStatus.PENDING;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}