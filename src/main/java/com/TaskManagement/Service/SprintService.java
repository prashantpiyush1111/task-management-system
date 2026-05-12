package com.TaskManagement.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Entity.Sprint;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.SprintState;
import com.TaskManagement.Repository.IssueRepository;
import com.TaskManagement.Repository.SprintRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;

@Service
public class SprintService {

	@Autowired
	private SprintRepository sprintRepo;

	@Autowired
	private IssueRepository issueRepo;

	public Sprint createSprint(Sprint sprint) {
		sprint.setSprintState(SprintState.PLANNED);
		return sprintRepo.save(sprint);
	}

	public List<Sprint> getSprintsByProject(Long projectId) {
		return sprintRepo.findByProjectId(projectId);
	}

	public List<Sprint> getAllSprints() {
		return sprintRepo.findAll();
	}

	@Transactional
	public Issue assignIssueToSprint(Long sprintId, Long issueId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("Sprint not found"));
		Issue issue = issueRepo.findById(issueId).orElseThrow(() -> new RuntimeException("Issue not found"));
		if (sprint.getSprintState() == SprintState.COMPLETED) {
			throw new RuntimeException("Cannot add task to completed sprint");
		}
		issue.setSprintId(sprintId);
		return issueRepo.save(issue);
	}

	@Transactional
	public Sprint startSprint(Long sprintId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("Sprint not found"));
		if (sprint.getSprintState() != SprintState.PLANNED) {
			throw new RuntimeException("Sprint cannot be started");
		}
		sprint.setSprintState(SprintState.ACTIVE);
		if (sprint.getStartDate() == null) {
			sprint.setStartDate(LocalDate.now());
		}
		return sprintRepo.save(sprint);
	}

	@Transactional
	public Sprint endSprint(Long sprintId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("Sprint not found"));
		sprint.setSprintState(SprintState.COMPLETED);
		if (sprint.getEndDate() == null) {
			sprint.setEndDate(LocalDate.now());
		}
		List<Issue> issues = issueRepo.findBySprintId(sprintId);
		for (Issue i : issues) {
			if (i.getIssueStatus() != IssueStatus.DONE) {
				i.setSprintId(null);
				issueRepo.save(i);
			}
		}
		return sprintRepo.save(sprint);
	}

	public Map<String, Object> getBurnDownData(Long sprintId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("Sprint not found"));
		LocalDate start = sprint.getStartDate();
		LocalDate end = sprint.getEndDate() != null ? sprint.getEndDate() : LocalDate.now();
		List<Issue> issues = issueRepo.findBySprintId(sprintId);
		int totalTask = issues.size();
		Map<String, Integer> chart = new LinkedHashMap<>();
		for (LocalDate cursor = start; !cursor.isAfter(end); cursor = cursor.plusDays(1)) {

			LocalDate currentDate = cursor;

			int completed = (int) issues.stream()
					.filter(i -> i.getIssueStatus() == IssueStatus.DONE)
					.filter(i -> i.getUpdatedAt() != null)
					.filter(i -> !i.getUpdatedAt().toLocalDate().isAfter(currentDate))
					.count();

			int remaining = totalTask - completed;

			chart.put(cursor.toString(), remaining);
		}
		Map<String, Object> response = new HashMap<>();
		response.put("sprintId", sprintId);
		response.put("startDate", start);
		response.put("endDate", end);
		response.put("burnDownData", chart);
		return response;
	}
}