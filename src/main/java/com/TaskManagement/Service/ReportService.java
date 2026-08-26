package com.TaskManagement.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Entity.Sprint;
import com.TaskManagement.Enum.IssueStatus;
import com.TaskManagement.Enum.SprintState;
import com.TaskManagement.Repository.IssueRepository;
import com.TaskManagement.Repository.SprintRepository;

@Service
public class ReportService {

	@Autowired
	private IssueRepository issueRepo;

	@Autowired
	private SprintRepository sprintRepo;

	private Sprint getOwnedSprint(Long sprintId, Long organizationId) {
		Sprint sprint = sprintRepo.findById(sprintId).orElseThrow(() -> new RuntimeException("Sprint not found"));
		if (!organizationId.equals(sprint.getOrganizationId())) {
			throw new RuntimeException("Sprint not found");
		}
		return sprint;
	}

	public Map<String, Object> burnDownData(Long sprintId, Long organizationId) {

		Sprint sprint = getOwnedSprint(sprintId, organizationId);

		List<Issue> issues = issueRepo.findBySprintId(sprintId);
		int total = issues.size();
		Map<String, Integer> chart = new LinkedHashMap<>();

		LocalDate start = sprint.getStartDate();
		LocalDate end = sprint.getEndDate() != null ? sprint.getEndDate() : LocalDate.now();

		int done = (int) issues.stream().filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();

		for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
			chart.put(d.toString(), total - done);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("sprintId", sprintId);
		result.put("burnDown", chart);
		return result;
	}

	public Map<String, Object> velocity(Long projectId, Long organizationId) {

		List<Sprint> completed = sprintRepo.findByProjectId(projectId).stream()
				.filter(s -> organizationId.equals(s.getOrganizationId()))
				.filter(s -> s.getSprintState() == SprintState.COMPLETED).collect(Collectors.toList());

		Map<String, Integer> velocity = new LinkedHashMap<>();
		for (Sprint s : completed) {
			int done = (int) issueRepo.findBySprintId(s.getId()).stream()
					.filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();
			velocity.put(s.getSprintName(), done);
		}

		Map<String, Object> result = new HashMap<>();
		result.put("projectId", projectId);
		result.put("velocity", velocity);
		return result;
	}

	public Map<String, Object> sprintReport(Long sprintId, Long organizationId) {

		getOwnedSprint(sprintId, organizationId);

		List<Issue> issues = issueRepo.findBySprintId(sprintId);

		long completedIssue = issues.stream().filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();
		long notCompletedIssue = issues.size() - completedIssue;

		Map<String, Object> result = new HashMap<>();
		result.put("TotalIssues", issues.size());
		result.put("CompletedIssue", completedIssue);
		result.put("NotCompletedIssue", notCompletedIssue);
		return result;
	}


	public Map<String, Object> epicProgressReport(Long epicId, Long organizationId) {

		List<Issue> stories = issueRepo.findByEpicId(epicId).stream()
				.filter(i -> organizationId.equals(i.getOrganizationId()))
				.collect(Collectors.toList());
		long done = stories.stream().filter(i -> i.getIssueStatus() == IssueStatus.DONE).count();

		Map<String, Object> result = new HashMap<>();
		result.put("epicId", epicId);
		result.put("TotalStories", stories.size());
		result.put("CompletedStories", done);
		result.put("ProgressPercent", stories.isEmpty() ? 0 : (done * 100 / stories.size()));
		return result;
	}

	public Map<String, Object> cumulativeFlow(Long sprintId, Long organizationId) {

		getOwnedSprint(sprintId, organizationId);

		List<Issue> issues = issueRepo.findBySprintId(sprintId);

		Map<String, Long> cfd = issues.stream().collect(Collectors.groupingBy(
				i -> i.getIssueStatus() != null ? i.getIssueStatus().name() : "UNKNOWN", Collectors.counting()));

		Map<String, Object> result = new HashMap<>();
		result.put("CumulativeFlow", cfd);
		return result;
	}

	public Map<String, Object> workLodDistribution(Long sprintId, Long organizationId) {

		getOwnedSprint(sprintId, organizationId);

		List<Issue> issues = issueRepo.findBySprintId(sprintId);

		Map<String, Long> load = issues.stream().collect(Collectors.groupingBy(
				i -> i.getAssigneeEmail() != null ? i.getAssigneeEmail() : "UNASSIGNED", Collectors.counting()));

		Map<String, Object> result = new HashMap<>();
		result.put("workload", load);
		return result;
	}
}