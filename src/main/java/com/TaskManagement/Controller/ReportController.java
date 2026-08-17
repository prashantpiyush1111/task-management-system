package com.TaskManagement.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private UserAuthRepository userAuthRepository;

	private Long resolveOrganizationId(Authentication authentication) {
		UserAuth user = userAuthRepository.findByUserOfficialEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getOrganization().getId();
	}

	@GetMapping("/burndownReport/{sprintId}")
	public ResponseEntity<Map<String, Object>> getBurnDown(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(reportService.burnDownData(sprintId, organizationId));
	}

	@GetMapping("/velocityReport/{projectId}")
	public ResponseEntity<Map<String, Object>> getVelocity(@PathVariable Long projectId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(reportService.velocity(projectId, organizationId));
	}

	@GetMapping("/sprintReport/{sprintId}")
	public ResponseEntity<Map<String, Object>> getSprintRepot(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(reportService.sprintReport(sprintId, organizationId));
	}

	@GetMapping("/epicReport/{epicId}")
	public ResponseEntity<Map<String, Object>> getEpicRepot(@PathVariable Long epicId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(reportService.epicProgessReport(epicId, organizationId));
	}

	@GetMapping("/cumultaive/{sprintId}")
	public ResponseEntity<Map<String, Object>> getCumulativeFlowDataReport(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(reportService.cumulativeFlow(sprintId, organizationId));
	}

	@GetMapping("/workLoadReport/{sprintId}")
	public ResponseEntity<Map<String, Object>> getWorkLoadReport(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(reportService.workLodDistribution(sprintId, organizationId));
	}

}