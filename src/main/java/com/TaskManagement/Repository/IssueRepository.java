package com.TaskManagement.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.Entity.Issue;
import com.TaskManagement.Enum.IssueStatus;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

	Optional<Issue> findByIssueKey(String issueKey);

	List<Issue> findByAssigneeEmail(String userOfficialEmail);

	List<Issue> findBySprintId(Long sprintId);

	List<Issue> findByEpicId(Long epicId);

	List<Issue> findByIssueStatus(IssueStatus issueStatus);

	List<Issue> findByProjectIdAndSprintIdIsNullOrderByBackLogPosition(Long projectId);

	List<Issue> findByOrganizationId(Long organizationId);
}