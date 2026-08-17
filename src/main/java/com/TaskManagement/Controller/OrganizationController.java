package com.TaskManagement.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.AuthResponseDTO;
import com.TaskManagement.DTO.OrganizationRequestDTO;
import com.TaskManagement.Entity.Organization;
import com.TaskManagement.Service.OrganizationService;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

	@Autowired
	private OrganizationService organizationService;

	@PostMapping("/register")
	public AuthResponseDTO registerOrganization(@RequestBody OrganizationRequestDTO request) {
		return organizationService.createOrganizationWithAdmin(request);
	}

	@GetMapping
	public List<Organization> listOrganizations() {
		return organizationService.listActiveOrganizations();
	}
}