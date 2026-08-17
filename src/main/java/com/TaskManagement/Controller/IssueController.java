package com.TaskManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.IssueDTO;
import com.TaskManagement.Entity.IssueComment;
import com.TaskManagement.Entity.Sprint;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.IssueService;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {
	private final IssueService issueService;

	@Autowired
	private UserAuthRepository userAuthRepository;

	private Long resolveOrganizationId(Authentication authentication) {
		UserAuth user = userAuthRepository.findByUserOfficialEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getOrganization().getId();
	}

	@PostMapping("/createIssue")
	public ResponseEntity<IssueDTO> createIssue(@RequestBody IssueDTO issues, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(issueService.createIssue(issues, organizationId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<IssueDTO> getById(@PathVariable Long id, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(issueService.getById(id, organizationId));
	}

	@GetMapping("/assignee/{email}")
	public ResponseEntity<List<IssueDTO>> getByAssigneeEmail(@PathVariable("email") String userOfficialEmail,
			Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(issueService.getByAssigneeEmail(userOfficialEmail, organizationId));
	}

	@PostMapping("/{id}/comment")
	public ResponseEntity<IssueComment> addComment(@PathVariable Long id, @RequestBody Map<String, String> body,
			Authentication authentication) {

		String commentBody = body.get("body");
		String author = authentication.getName();
		Long organizationId = resolveOrganizationId(authentication);

		return ResponseEntity.ok(issueService.addComment(id, author, commentBody, organizationId));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<IssueDTO> updateStatus(@PathVariable Long id, @RequestParam("status") IssueStatus issueStatus,
			Authentication authentication) {

		String user = authentication.getName();
		Long organizationId = resolveOrganizationId(authentication);

		return ResponseEntity.ok(issueService.updateIssueStatus(id, issueStatus, user, organizationId));
	}

	@PostMapping("/sprints")
	public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(issueService.createSprint(sprint, organizationId));
	}

	@GetMapping("/search")
	public ResponseEntity<List<IssueDTO>> search(@RequestParam(required = false) Map<String, String> allRequestParams,
			Authentication authentication) {
	    if (allRequestParams == null) allRequestParams = new HashMap<>();
	    Long organizationId = resolveOrganizationId(authentication);
	    return ResponseEntity.ok(issueService.search(allRequestParams, organizationId));
	}
}