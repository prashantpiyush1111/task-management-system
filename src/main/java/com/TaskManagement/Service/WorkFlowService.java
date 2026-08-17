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
	public WorkFlow createWorkFlow(WorkFlow workFlow, Long organizationId) {
		workFlow.setOrganizationId(organizationId);
		for (WorkFlowTransaction t : workFlow.getTransaction())
			t.setWorkFlow(workFlow);
		return workFlowRepo.save(workFlow);
	}

	public List<WorkFlow> listAll(Long organizationId) {
		return workFlowRepo.findByOrganizationId(organizationId);
	}

	private WorkFlow getOwnedWorkFlow(Long id, Long organizationId) {
		WorkFlow wf = workFlowRepo.findById(id).orElseThrow(() -> new RuntimeException("WorkFlow not found"));
		if (!organizationId.equals(wf.getOrganizationId())) {
			throw new RuntimeException("WorkFlow not found");
		}
		return wf;
	}

	public WorkFlow getWorkById(Long id, Long organizationId) {
		return getOwnedWorkFlow(id, organizationId);
	}

	@Transactional
	public WorkFlow updateWork(Long id, WorkFlow update, Long organizationId) {
		WorkFlow wf = getOwnedWorkFlow(id, organizationId);
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
	public void deleteWork(Long id, Long organizationId) {
		getOwnedWorkFlow(id, organizationId);
		workFlowRepo.deleteById(id);
	}

	public List<WorkFlowTransaction> allowedTransactions(Long workFlowId, IssueStatus fromStatus) {

		return workflowTransactionRepo.findByWorkFlowIdAndFromStatus(workFlowId, fromStatus);
	}

	public boolean isTransactionsAllowed(Long workFlowId, IssueStatus fromStatus, IssueStatus toStatus,
			Set<Role> userRoles, Long organizationId) {

		getOwnedWorkFlow(workFlowId, organizationId);

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