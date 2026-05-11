package com.TaskManagement.Controller;

import com.TaskManagement.Entity.Sprint;
import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

	@Autowired
	private SprintService sprintService;

	@PostMapping("/create")
	public ResponseEntity<Sprint> createSprint(@RequestBody Sprint sprint) {
		return ResponseEntity.ok(sprintService.createSprint(sprint));
	}

	@GetMapping("/all")
	public ResponseEntity<List<Sprint>> getAll() {
		return ResponseEntity.ok(sprintService.getAllSprints());
	}

	@GetMapping("/project/{projectId}")
	public ResponseEntity<List<Sprint>> getByProject(@PathVariable Long projectId) {
		return ResponseEntity.ok(sprintService.getSprintsByProject(projectId));
	}

	@PostMapping("/{sprintId}/assign/{issueId}")
	public ResponseEntity<Issue> assignIssue(@PathVariable Long sprintId, @PathVariable Long issueId) {
		return ResponseEntity.ok(sprintService.assignIssueToSprint(sprintId, issueId));
	}

	@PutMapping("/{sprintId}/start")
	public ResponseEntity<Sprint> startSprint(@PathVariable Long sprintId) {
		return ResponseEntity.ok(sprintService.startSprint(sprintId));
	}

	@PutMapping("/{sprintId}/end")
	public ResponseEntity<Sprint> endSprint(@PathVariable Long sprintId) {
		return ResponseEntity.ok(sprintService.endSprint(sprintId));
	}

	@GetMapping("/{sprintId}/burndown")
	public ResponseEntity<Map<String, Object>> getBurndown(@PathVariable Long sprintId) {
		return ResponseEntity.ok(sprintService.getBurnDownData(sprintId));
	}
}