package com.TaskManagement.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TaskManagement.Enum.IssueStatus;

@FeignClient(name = "issue-service", url = "${issue-service.url}")
public interface IssueClient {
	@PutMapping("/{id}/status")
	void updateStatus(@PathVariable("id") Long id, @RequestParam("issueStatus") IssueStatus issueStatus,
			@RequestParam("performedBy") String performedBy);

	@PostMapping("/{id}/commit")
	void addCommit(@PathVariable("id") Long id, @RequestParam("author") String author,
			@RequestParam("body") String body);
}