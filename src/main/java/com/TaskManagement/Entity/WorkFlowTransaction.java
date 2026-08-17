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
	private IssueStatus fromStatus;
	private IssueStatus toStatus;
	private String actionName;
	@Builder.Default
	private Set<Role> allowedRole = new HashSet<>();
	@ManyToOne()
	@JoinColumn(name = "workflow_id")
	@JsonBackReference
	private WorkFlow workFlow;
}