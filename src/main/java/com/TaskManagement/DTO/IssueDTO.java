package com.TaskManagement.DTO;

import java.time.LocalDateTime;
import java.util.Set;

import com.TaskManagement.Enum.IssuePriority;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.IssueType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueDTO {

	public String issueKey;
	public String issueTitle;
	public String issueDescription;

	public IssueType issueType;
	public IssuePriority priority;
	public IssueStatus issueStatus;

	public String assigneeEmail;
	public String reporterEmail;

	public Long epicId;
	public Long sprintId;

	public LocalDateTime createdAt;
	public LocalDateTime updatedAt;
	public LocalDateTime dueDate;

	public Set<String> labels;

}