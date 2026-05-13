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

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueService {

	private final IssueRepository issueRepo;
	private final LabelRepository labelRepo;
	private final SprintRepository sprintRepo;
	private final IssueCommentRepository commentRepo;

	public IssueService(IssueRepository issueRepo, LabelRepository labelRepo, SprintRepository sprintRepo,
			IssueCommentRepository commentRepo) {
		this.issueRepo = issueRepo;
		this.labelRepo = labelRepo;
		this.sprintRepo = sprintRepo;
		this.commentRepo = commentRepo;
	}

	private String generateKey(Long id) {
		return "PROJ-" + id;
	}

	@Transactional
	public IssueDTO createIssue(IssueDTO dto) {

		Issue issue = new Issue();
		issue.setIssueTitle(dto.getIssueTitle());
		issue.setIssueDescription(dto.getIssueDescription());
		issue.setIssueType(dto.getIssueType() != null ? dto.getIssueType() : IssueType.TASK);
		issue.setIssueStatus(IssueStatus.OPEN);
		issue.setAssigneeEmail(dto.getAssigneeEmail());
		issue.setReporterEmail(dto.getReporterEmail());
		issue.setPriority(dto.getPriority());
		issue.setDueDate(dto.getDueDate());
		issue = issueRepo.save(issue);
		issue.setIssueKey(generateKey(issue.getId()));  
		issueRepo.save(issue);
		

		if (dto.getLabels() != null) {
			Set<Label> labels = new HashSet<>();
			for (String name : dto.getLabels()) {
				Label label = labelRepo.findByName(name).orElseGet(() -> {
					Label l = new Label();
					l.setName(name);
					return labelRepo.save(l);
				});
				labels.add(label);
			}
			issue.setLabels(labels);
		}

		issue = issueRepo.save(issue);
		

		return toDTO(issue);
	}

	@Transactional
	public IssueComment addComment(Long issueId, String authorEmail, String body) {

		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new RuntimeException("Issue not found"));

		IssueComment comment = IssueComment.builder().issueId(issue.getId()).authorEmail(authorEmail).body(body)
				.build();

		return commentRepo.save(comment);
	}

	@Transactional
	public IssueDTO updateIssueStatus(Long id, IssueStatus status, String performedBy) {

		Issue issue = issueRepo.findById(id).orElseThrow(() -> new RuntimeException("Issue not found"));

		if (status == null) {
			throw new RuntimeException("Status cannot be null");
		}

		issue.setIssueStatus(status);
		issueRepo.save(issue);
		return toDTO(issue);
	}

	public List<IssueDTO> search(Map<String, String> filters) {

		if (filters.containsKey("assignee")) {
			return issueRepo.findByAssigneeEmail(filters.get("assignee")).stream().map(this::toDTO)
					.collect(Collectors.toList());
		}
		if (filters.containsKey("sprint")) {
			return issueRepo.findBySprintId(Long.valueOf(filters.get("sprint"))).stream().map(this::toDTO)
					.collect(Collectors.toList());
		}
		if (filters.containsKey("status")) {
			IssueStatus status = IssueStatus.valueOf(filters.get("status").toUpperCase());
			return issueRepo.findByIssueStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
		}
		return issueRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	public Sprint createSprint(Sprint sprint) {
		return sprintRepo.save(sprint);
	}

	public IssueDTO getById(Long id) {
		Issue issue = issueRepo.findById(id).orElseThrow(() -> new RuntimeException("Issue not found"));
		return toDTO(issue);
	}

	public List<IssueDTO> getByAssigneeEmail(String email) {
		return issueRepo.findByAssigneeEmail(email).stream().map(this::toDTO).collect(Collectors.toList());
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
		if (issue.getLabels() != null) {
			dto.setLabels(issue.getLabels().stream().map(Label::getName).collect(Collectors.toSet()));
		}
		return dto;
	}
}