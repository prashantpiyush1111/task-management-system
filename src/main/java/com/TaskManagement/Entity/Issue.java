package com.TaskManagement.Entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.TaskManagement.Enum.IssuePriority;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.IssueType;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "issues", indexes = { @Index(name = "idx_issue_key", columnList = "issueKey"),
		@Index(name = "idx_issue_assignee", columnList = "assigneeEmail") })
public class Issue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false)
	private String issueKey;
	@Column(nullable = false)
	private String issueTitle;
	@Column(length = 5000)
	private String issueDescription;
	@Enumerated(EnumType.STRING)
	private IssueType issueType;
	@Enumerated(EnumType.STRING)
	private IssuePriority priority;
	@Enumerated(EnumType.STRING)
	private IssueStatus issueStatus;
	private String assigneeEmail;
	private String reporterEmail;
	private Long projectId;
	private Long sprintId;
	private Long epicId;
	private Long parentIssueId;
	private Integer backLogPosition;
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();
	private LocalDateTime dueDate;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "issue_label", joinColumns = @JoinColumn(name = "issue_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
	private Set<Label> labels = new HashSet<>();
}