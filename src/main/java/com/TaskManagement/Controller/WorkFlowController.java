package com.TaskManagement.Controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.Entity.WorkFlow;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.Role;
import com.TaskManagement.Service.WorkFlowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/workflows")
@RequiredArgsConstructor
public class WorkFlowController {

	@Autowired
	private WorkFlowService workFlowService;

	@PostMapping("/create")
	public ResponseEntity<WorkFlow> createWork(@RequestBody WorkFlow workFlow) {
		return ResponseEntity.ok(workFlowService.createWorkFlow(workFlow));
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<WorkFlow> updateWork(@PathVariable Long id, @RequestBody WorkFlow workFlow) {
		return ResponseEntity.ok(workFlowService.updateWork(id, workFlow));
	}

	@GetMapping("/all")
	public ResponseEntity<List<WorkFlow>> getAll() {
		return ResponseEntity.ok(workFlowService.listAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<WorkFlow> getById(@PathVariable Long id) {
		return ResponseEntity.ok(workFlowService.getWorkById(id));
	}

	@GetMapping("/{id}/transactions")
	public ResponseEntity<Boolean> allowedTransaction(@PathVariable Long id, @RequestParam IssueStatus from,
			@RequestParam IssueStatus to, @RequestParam Set<Role> userRole) {
		return ResponseEntity.ok(workFlowService.isTransactionsAllowed(id, from, to, userRole));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
		workFlowService.deleteWork(id); // deleteWorkFlow → deleteWork
		return ResponseEntity.ok().build();
	}
}