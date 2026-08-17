package com.TaskManagement.DTO;

import com.TaskManagement.Enum.Role;
import com.TaskManagement.Enum.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeStatusDTO {

	private Long id;

	private String userName;

	private String userOfficialEmail;

	private Role role;

	private UserStatus status;
}