package com.TaskManagement.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TaskManagement.Entity.WorkFlow;
import com.TaskManagement.Entity.WorkFlowTransaction;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.Role;
import com.TaskManagement.Repository.WorkFlowRepository;
import com.TaskManagement.Repository.WorkFlowTransactionRepository;

@Service
public class WorkFlowService {

	@Autowired
	private WorkFlowRepository workFlowRepo;

	@Autowired
	private WorkFlowTransactionRepository workflowTransactionRepo;

	@Transactional
	public WorkFlow createWorkFlow(WorkFlow workFlow) {
		for (WorkFlowTransaction t : workFlow.getTransaction())
			t.setWorkFlow(workFlow);
		return workFlowRepo.save(workFlow);
	}

	public List<WorkFlow> listAll() {
		return workFlowRepo.findAll();
	}

	public WorkFlow getWorkById(Long id) {
		return workFlowRepo.findById(id).orElseThrow(() -> new RuntimeException("WorkFlow not found"));
	}

	@Transactional
	public WorkFlow updateWork(Long id, WorkFlow update) {
		WorkFlow wf = getWorkById(id);
		wf.setName(update.getName());
		wf.setWorkDescription(update.getWorkDescription());
		wf.getTransaction().clear();
		if (update.getTransaction() != null) {
			for (WorkFlowTransaction t : update.getTransaction()) {
				t.setWorkFlow(wf);
				wf.getTransaction().add(t);
			}
		}
		return workFlowRepo.save(wf);
	}

	@Transactional
	public void deleteWork(Long id) {
		workFlowRepo.deleteById(id);
	}

	public List<WorkFlowTransaction> allowedTransactions(Long workFlowId, IssueStatus fromStatus) {

		return workflowTransactionRepo.findByWorkFlowIdAndFromStatus(workFlowId, fromStatus);
	}

	public boolean isTransactionsAllowed(Long workFlowId, IssueStatus fromStatus, IssueStatus toStatus,
			Set<Role> userRoles) {

		List<WorkFlowTransaction> transactions = workflowTransactionRepo.findByWorkFlowIdAndFromStatus(workFlowId,
				fromStatus);

		for (WorkFlowTransaction t : transactions) {
			if (!t.getToStatus().equals(toStatus))
				continue;
			if (t.getAllowedRole() == null || t.getAllowedRole().isEmpty())
				return true;
			for (Role role : userRoles) {
				if (t.getAllowedRole().contains(role))
					return true;
			}
			return false;
		}
		return false;
	}

	public Optional<WorkFlow> findByName(String workFlowName) {
		return workFlowRepo.findByName(workFlowName);
	}
}