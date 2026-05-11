package com.TaskManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;

// Used to send HTTP responses
import org.springframework.http.ResponseEntity;

// Used for REST APIs and request mappings
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.DTO.IssueDTO; // DTO for transferring issue data
import com.TaskManagement.Entity.IssueComment; // Entity for issue comments
import com.TaskManagement.Entity.Sprint; // Entity for sprint
import com.TaskManagement.Enum.IssueStatus; // Enum_for issue status
import com.TaskManagement.Service.IssueService; // Service layer

import lombok.RequiredArgsConstructor; // Lombok_annotation for constructor

import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/api/issues")

@RequiredArgsConstructor
public class IssueController {

	@Autowired
	private IssueService issueService;

	@PostMapping("/createIssue")
	public ResponseEntity<IssueDTO> createIssue(@RequestBody IssueDTO issues) {
		return ResponseEntity.ok(issueService.createIssue(issues));
	}

	@GetMapping("/{id}")
	public ResponseEntity<IssueDTO> getById(@PathVariable Long id) {

		return ResponseEntity.ok(issueService.getById(id));
	}

	@GetMapping("/assignee/{email}")
	public ResponseEntity<List<IssueDTO>> getByAssigneeEmail(@PathVariable("email") String userOfficialEmail) {
		return ResponseEntity.ok(issueService.getByAssigneeEmail(userOfficialEmail));
	}

	@PostMapping("/{id}/comment")
	public ResponseEntity<IssueComment> addComment(@PathVariable Long id, @RequestBody Map<String, String> body,
			@RequestHeader(value = "X_User_Email", required = false) String user) {

		String commentBody = body.get("body");

		String author = (user == null) ? body.getOrDefault("authorEmail", "system@gmail") : user;

		return ResponseEntity.ok(issueService.addComment(id, author, commentBody));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<IssueDTO> updateStatus(@PathVariable Long id, @RequestParam IssueStatus issueStatus,
			@RequestHeader(value = "X_User_Email", required = false) String user) {

		return ResponseEntity.ok(issueService.updateIssueStatus(id, issueStatus, user));
	}

	@PostMapping("/sprints")
	public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint) {

		return ResponseEntity.ok(issueService.createSprint(sprint));
	}

	@GetMapping("/search")
	public ResponseEntity<List<IssueDTO>> search(@RequestParam Map<String, String> allRequestParams) {

		return ResponseEntity.ok(issueService.search(allRequestParams));
	}
}