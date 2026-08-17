package com.TaskManagement.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.Service.IntegrationService;

@RestController
@RequestMapping("/api/integration")
public class IntegrationController {

	@Autowired
	private IntegrationService integrationService;

	@PostMapping("/github")
	public ResponseEntity<?> processGithubEvent(@RequestHeader("GitHub_Event") String event,
			@RequestParam Long organizationId,
			@RequestBody Map<String, Object> payload) {
		integrationService.processGithubEvent(event.toUpperCase(), payload, organizationId);
		return ResponseEntity.ok("GitHub Event Processed");
	}

	@PostMapping("/jenkins")
	public ResponseEntity<?> processJenkinsEvent(@RequestParam Long organizationId,
			@RequestBody Map<String, Object> payload) {
		integrationService.processJenkinEvents(payload, organizationId);
		return ResponseEntity.ok("Jenkins Event Processed");
	}

	@PostMapping("/docker")
	public ResponseEntity<?> processDockerEvent(@RequestParam Long organizationId,
			@RequestBody Map<String, Object> payload) {
		integrationService.proceesDockerEvent(payload, organizationId);
		return ResponseEntity.ok("Docker Event Processed");
	}

	@PostMapping("/commit")
	public ResponseEntity<?> handleCommit(@RequestParam String message, @RequestParam String author,
			@RequestParam Long organizationId) {
		integrationService.handelCommitMessage(message, author, organizationId);
		return ResponseEntity.ok("Commit processed");
	}

	@PostMapping("/pullRequest")
	public ResponseEntity<?> handlePR(@RequestParam String title, @RequestParam String author,
			@RequestParam Long organizationId) {
		integrationService.handlePullingRequest(title, author, organizationId);
		return ResponseEntity.ok("PR Processed");
	}
}