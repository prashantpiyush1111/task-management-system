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
			@RequestBody Map<String, Object> payload) {
		integrationService.processGithubEvent(event.toUpperCase(), payload);
		return ResponseEntity.ok("GitHub Event Processed");
	}

	@PostMapping("/jenkins")
	public ResponseEntity<?> processJenkinsEvent(@RequestBody Map<String, Object> payload) {
		integrationService.processJenkinEvents(payload);
		return ResponseEntity.ok("Jenkins Event Processed");
	}

	@PostMapping("/docker")
	public ResponseEntity<?> processDockerEvent(@RequestBody Map<String, Object> payload) {
		integrationService.proceesDockerEvent(payload);
		return ResponseEntity.ok("Docker Event Processed");
	}

	@PostMapping("/commit")
	public ResponseEntity<?> handleCommit(@RequestParam String message, @RequestParam String author) {
		integrationService.handelCommitMessage(message, author);
		return ResponseEntity.ok("Commit processed");
	}

	@PostMapping("/pullRequest")
	public ResponseEntity<?> handlePR(@RequestParam String title, @RequestParam String author) {
		integrationService.handlePullingRequest(title, author);
		return ResponseEntity.ok("PR Processed");
	}
}