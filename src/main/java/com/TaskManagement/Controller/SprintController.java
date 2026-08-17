package com.TaskManagement.Controller;

import com.TaskManagement.Entity.Sprint;
import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

	@Autowired
	private SprintService sprintService;

	@Autowired
	private UserAuthRepository userAuthRepository;

	private Long resolveOrganizationId(Authentication authentication) {
		UserAuth user = userAuthRepository.findByUserOfficialEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getOrganization().getId();
	}

	@PostMapping("/create")
	public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.createSprint(sprint, organizationId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<Sprint>> getAll(Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.getAllSprints(organizationId));
	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<Sprint>> getByProject(@PathVariable Long projectId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.getSprintsByProject(projectId, organizationId));
	}

	@PostMapping("/{sprintId}/assign/{issueId}")
	public ResponseEntity<Issue> assignIssue(@PathVariable Long sprintId, @PathVariable Long issueId,
			Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.assignIssueToSprint(sprintId, issueId, organizationId));
	}

	@PutMapping("/{sprintId}/start")
	public ResponseEntity<Sprint> startSprint(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.startSprint(sprintId, organizationId));
	}

	@PutMapping("/{sprintId}/end")
	public ResponseEntity<Sprint> endSprint(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.endSprint(sprintId, organizationId));
	}

	@GetMapping("/{sprintId}/burndown")
	public ResponseEntity<Map<String, Object>> getBurndown(@PathVariable Long sprintId, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(sprintService.getBurnDownData(sprintId, organizationId));
	}
}