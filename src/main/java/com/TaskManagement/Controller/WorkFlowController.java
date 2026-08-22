package com.TaskManagement.Controller;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Entity.WorkFlow;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.Role;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.WorkFlowService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkFlowController {
    private final WorkFlowService workFlowService;

    @Autowired
    private UserAuthRepository userAuthRepository;

    private Long resolveOrganizationId(Authentication authentication) {
        UserAuth user = userAuthRepository.findByUserOfficialEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getOrganization().getId();
    }

	@PostMapping("/create")
	public ResponseEntity<WorkFlow> createWork(@RequestBody WorkFlow workFlow, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(workFlowService.createWorkFlow(workFlow, organizationId));
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<WorkFlow> updateWork(@PathVariable Long id, @RequestBody WorkFlow workFlow,
			Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(workFlowService.updateWork(id, workFlow, organizationId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<WorkFlow>> getAll(Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(workFlowService.listAll(organizationId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<WorkFlow> getById(@PathVariable Long id, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(workFlowService.getWorkById(id, organizationId));
	}

	@GetMapping("/{id}/transactions")
	public ResponseEntity<Boolean> allowedTransaction(@PathVariable Long id, @RequestParam IssueStatus from,
			@RequestParam IssueStatus to, @RequestParam Set<Role> userRole, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(workFlowService.isTransactionsAllowed(id, from, to, userRole, organizationId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		workFlowService.deleteWork(id, organizationId);
		return ResponseEntity.ok().build();
	}
}