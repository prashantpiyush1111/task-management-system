package com.TaskManagement.Entity;

import java.util.HashSet;
import java.util.Set;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.Role;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workFlow_Transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkFlowTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private IssueStatus fromStats;
	private IssueStatus toStatus;
	private String actionName;
	@Builder.Default
	private Set<Role> allowedRole = new HashSet<>();
	@ManyToOne()
	@JoinColumn(name = "workflow_id")
	@JsonBackReference
	private WorkFlow workFlow;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public IssueStatus getFromStats() {
		return fromStats;
	}

	public void setFromStats(IssueStatus fromStats) {
		this.fromStats = fromStats;
	}

	public IssueStatus getToStatus() {
		return toStatus;
	}

	public void setToStatus(IssueStatus toStatus) {
		this.toStatus = toStatus;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public Set<Role> getAllowedRole() {
		return allowedRole;
	}

	public void setAllowedRole(Set<Role> allowedRole) {
		this.allowedRole = allowedRole;
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}
}