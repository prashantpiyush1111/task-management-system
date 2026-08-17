package com.TaskManagement.Controller;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.BackLogService;
@RestController
@RequestMapping("/api/backlog")
public class BackLogController {
	@Autowired
	private BackLogService backLogService;
	@Autowired
	private UserAuthRepository userAuthRepository;
	private Long resolveOrganizationId(Authentication authentication) {
		UserAuth user = userAuthRepository.findByUserOfficialEmail(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getOrganization().getId();
	}
	@GetMapping("/{projectId}")
	public ResponseEntity<List<Issue>> getBackLog(@PathVariable Long projectId, Authentication authentication) {
		return ResponseEntity.ok(backLogService.getBackLog(projectId, resolveOrganizationId(authentication)));
	}
	@PostMapping("/{projectId}/reorder")
	public ResponseEntity<String> reorder(@PathVariable Long projectId, @RequestBody List<Long> orderedIssueId, Authentication authentication) {
		backLogService.recorderBackLog(projectId, orderedIssueId, resolveOrganizationId(authentication));
		return ResponseEntity.ok("Backlog reordered");
	}
	@PutMapping("/add-to-sprint/{issueId}/{sprintId}")
	public ResponseEntity<Issue> addIssueToSprint(@PathVariable Long issueId, @PathVariable Long sprintId, Authentication authentication) {
		return ResponseEntity.ok(backLogService.addIssueToSprint(issueId, sprintId, resolveOrganizationId(authentication)));
	}
	@GetMapping("/{projectId}/hierarchy")
	public ResponseEntity<Map<String, Object>> getHierarchy(@PathVariable Long projectId, Authentication authentication) {
		return ResponseEntity.ok(backLogService.getBackLogHierArchy(projectId, resolveOrganizationId(authentication)));
	}
}