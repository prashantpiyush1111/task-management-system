package com.TaskManagement.Service;

import org.springframework.stereotype.Service;

import com.TaskManagement.DTO.IssueDTO;
import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Entity.IssueComment;
import com.TaskManagement.Entity.Label;
import com.TaskManagement.Entity.Sprint;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.IssueType;
import com.TaskManagement.Repository.IssueCommentRepository;
import com.TaskManagement.Repository.IssueRepository;
import com.TaskManagement.Repository.LabelRepository;
import com.TaskManagement.Repository.SprintRepository;
import com.TaskManagement.Entity.IssueStatusHistory;
import com.TaskManagement.Repository.IssueStatusHistoryRepository;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {

	private final IssueRepository issueRepo;
	private final LabelRepository labelRepo;
	private final SprintRepository sprintRepo;
	private final IssueCommentRepository commentRepo;
	private final IssueStatusHistoryRepository statusHistoryRepo;

	public IssueService(
	        IssueRepository issueRepo,
	        LabelRepository labelRepo,
	        SprintRepository sprintRepo,
	        IssueCommentRepository commentRepo,
	        IssueStatusHistoryRepository statusHistoryRepo) {

	    this.issueRepo = issueRepo;
	    this.labelRepo = labelRepo;
	    this.sprintRepo = sprintRepo;
	    this.commentRepo = commentRepo;
	    this.statusHistoryRepo = statusHistoryRepo;
	}

	private String generateKey(Long id) {
		return "PROJ-" + id;
	}

	@Transactional
	public IssueDTO createIssue(IssueDTO dto, Long organizationId) {

		Issue issue = new Issue();
		issue.setOrganizationId(organizationId);
		issue.setIssueTitle(dto.getIssueTitle());
		issue.setIssueDescription(dto.getIssueDescription());
		issue.setIssueType(dto.getIssueType() != null ? dto.getIssueType() : IssueType.TASK);
		issue.setIssueStatus(IssueStatus.OPEN);
		issue.setAssigneeEmail(dto.getAssigneeEmail());
		issue.setReporterEmail(dto.getReporterEmail());
		issue.setPriority(dto.getPriority());
		issue.setDueDate(dto.getDueDate());
		issue.setProjectId(dto.getProjectId());

		issue.setIssueKey("TEMP-" + UUID.randomUUID());

		issue = issueRepo.save(issue);

		issue.setIssueKey(generateKey(issue.getId()));
		issueRepo.save(issue);

		if (dto.getLabels() != null) {
			Set<Label> labels = new HashSet<>();
			for (String name : dto.getLabels()) {
				Label label = labelRepo.findByName(name).orElseGet(() -> {
					Label l = new Label();
					l.setName(name);
					l.setOrganizationId(organizationId);
					return labelRepo.save(l);
				});
				labels.add(label);
			}
			issue.setLabels(labels);
		}

		issue = issueRepo.save(issue);
		

		return toDTO(issue);
	}

	private Issue getOwnedIssue(Long id, Long organizationId) {
		Issue issue = issueRepo.findById(id).orElseThrow(() -> new RuntimeException("Issue not found"));
		if (!organizationId.equals(issue.getOrganizationId())) {
			throw new RuntimeException("Issue not found");
		}
		return issue;
	}

	@Transactional
	public IssueComment addComment(Long issueId, String authorEmail, String body, Long organizationId) {

		Issue issue = getOwnedIssue(issueId, organizationId);

		IssueComment comment = IssueComment.builder().issueId(issue.getId()).authorEmail(authorEmail).body(body)
				.build();

		return commentRepo.save(comment);
	}

	@Transactional
	public IssueDTO updateIssueStatus(
	        Long id,
	        IssueStatus status,
	        String performedBy,
	        Long organizationId) {

	    Issue issue = getOwnedIssue(id, organizationId);

	    if (status == null) {
	        throw new RuntimeException("Status cannot be null");
	    }

	    IssueStatus oldStatus = issue.getIssueStatus();

	    // Status history save only when status actually changes
	    if (oldStatus != status) {

	        IssueStatusHistory history = new IssueStatusHistory();

	        history.setIssueId(issue.getId());
	        history.setOldStatus(oldStatus);
	        history.setNewStatus(status);
	        history.setChangedBy(performedBy);
	        history.setChangedAt(java.time.LocalDateTime.now());

	        statusHistoryRepo.save(history);
	    }

	    issue.setIssueStatus(status);
	    issue.setUpdatedAt(java.time.LocalDateTime.now());

	    issueRepo.save(issue);

	    return toDTO(issue);
	}

	public List<IssueDTO> search(Map<String, String> filters, Long organizationId) {

		if (filters.containsKey("assignee")) {
			return issueRepo.findByAssigneeEmail(filters.get("assignee")).stream()
					.filter(i -> organizationId.equals(i.getOrganizationId()))
					.map(this::toDTO)
					.collect(Collectors.toList());
		}
		if (filters.containsKey("sprint")) {
			return issueRepo.findBySprintId(Long.valueOf(filters.get("sprint"))).stream()
					.filter(i -> organizationId.equals(i.getOrganizationId()))
					.map(this::toDTO)
					.collect(Collectors.toList());
		}
		if (filters.containsKey("status")) {
			IssueStatus status = IssueStatus.valueOf(filters.get("status").toUpperCase());
			return issueRepo.findByIssueStatus(status).stream()
					.filter(i -> organizationId.equals(i.getOrganizationId()))
					.map(this::toDTO)
					.collect(Collectors.toList());
		}
		return issueRepo.findByOrganizationId(organizationId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	public Sprint createSprint(Sprint sprint, Long organizationId) {
		sprint.setOrganizationId(organizationId);
		return sprintRepo.save(sprint);
	}

	public IssueDTO getById(Long id, Long organizationId) {
		Issue issue = getOwnedIssue(id, organizationId);
		return toDTO(issue);
	}

	public List<IssueDTO> getByAssigneeEmail(String email, Long organizationId) {
		return issueRepo.findByAssigneeEmail(email).stream()
				.filter(i -> organizationId.equals(i.getOrganizationId()))
				.map(this::toDTO).collect(Collectors.toList());
	}

	private IssueDTO toDTO(Issue issue) {
		IssueDTO dto = new IssueDTO();
		 dto.setId(issue.getId());
		dto.setIssueKey(issue.getIssueKey());
		dto.setIssueTitle(issue.getIssueTitle());
		dto.setIssueDescription(issue.getIssueDescription());
		dto.setIssueType(issue.getIssueType());
		dto.setIssueStatus(issue.getIssueStatus());
		dto.setAssigneeEmail(issue.getAssigneeEmail());
		dto.setReporterEmail(issue.getReporterEmail());
		dto.setPriority(issue.getPriority());
		dto.setDueDate(issue.getDueDate());
		dto.setCreatedAt(issue.getCreatedAt());
		dto.setUpdatedAt(issue.getUpdatedAt());
		dto.setProjectId(issue.getProjectId());
		dto.setSprintId(issue.getSprintId());
		dto.setEpicId(issue.getEpicId());
		if (issue.getLabels() != null) {
			dto.setLabels(issue.getLabels().stream().map(Label::getName).collect(Collectors.toSet()));
		}
		return dto;
	}
}