package com.TaskManagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationRequestDTO {

	// Organization details
	private String organizationName;
	private String organizationEmail;

	// First admin user details
	private String adminUserName;
	private String adminEmail;
	private String adminPassword;
}