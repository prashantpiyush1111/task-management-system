package com.TaskManagement.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.Client.IssueClient;
import com.TaskManagement.Enum.IssueStatus;

@Service
public class IntegrationService {

	@Autowired
	private IssueClient issueClient;

	public void handelCommitMessage(String msg, String author) {

		String regex = "([A-Z]+-\\d+)";
		Matcher matcher = Pattern.compile(regex).matcher(msg);

		if (matcher.find()) {
			String issueKey = matcher.group(1);
			Long issueId = Long.parseLong(issueKey.split("-")[1]);
			issueClient.updateStatus(issueId, IssueStatus.DONE, author);
			issueClient.addCommit(issueId, author, "Closed via commit: " + msg);
		}
	}

	public void handlePullingRequest(String title, String author) {

		String regex = "([A-Z]+-\\d+)";
		Matcher matcher = Pattern.compile(regex).matcher(title);

		if (matcher.find()) {
			String issueKey = matcher.group(1);

			Long issueId = Long.parseLong(issueKey.split("-")[1]);

			issueClient.updateStatus(issueId, IssueStatus.IN_PROGRESS, author);
			issueClient.addCommit(issueId, author, "Pull request opened: " + title);
		}
	}

	public void processGithubEvent(String event, Map<String, Object> payload) {
		if ("PUSH".equalsIgnoreCase(event))
			handlePushCode(payload);
		if ("PULL_REQUEST".equalsIgnoreCase(event))
			handlePullRequest(payload);
	}

	@SuppressWarnings("unchecked")
	private void handlePushCode(Map<String, Object> payload) {
		Object commitObj = payload.get("commits");
		if (!(commitObj instanceof List))
			return;
		List<?> commits = (List<?>) commitObj;
		for (Object obj : commits) {
			if (!(obj instanceof Map))
				continue;
			Map<String, Object> commit = (Map<String, Object>) obj;
			String message = (String) commit.get("message");
			Map<String, Object> authorMap = (Map<String, Object>) commit.get("author");
			String author = authorMap != null ? (String) authorMap.get("name") : "Unknown";
			handelCommitMessage(message, author);
		}
	}

	@SuppressWarnings("unchecked")
	private void handlePullRequest(Map<String, Object> payload) {
		Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
		if (pr == null)
			return;
		String title = (String) pr.get("title");
		Map<String, Object> user = (Map<String, Object>) pr.get("user");
		String author = user != null ? (String) user.get("login") : "unknown";
		handlePullingRequest(title, author);
	}

	public void processJenkinEvents(Map<String, Object> body) {
		String jobName = (String) body.get("name");
		String result = (String) body.get("result");
		String log = (String) body.get("log");

		String issueKey = extractIssueKey(jobName);
		if (issueKey == null)
			return;

		Long issueId = extractId(issueKey);

		if ("FAILURE".equalsIgnoreCase(result)) {
			issueClient.addCommit(issueId, "jenkins",
					"Build Failed\n------ LOG ------\n" + log + "\n------ END ------");
		}
		if ("SUCCESS".equalsIgnoreCase(result)) {
			issueClient.addCommit(issueId, "Jenkins", "Build Successful");
		}
	}

	@SuppressWarnings("unchecked")
	public void proceesDockerEvent(Map<String, Object> payload) {
		String status = (String) payload.get("status");
		String image = (String) payload.get("from");

		Map<String, Object> actor = (Map<String, Object>) payload.get("Actor");

		Map<String, Object> attributes = actor != null ? (Map<String, Object>) actor.get("Attributes") : null;

		String containerName = attributes != null ? (String) attributes.get("name") : "";
		String imageName = attributes != null ? (String) attributes.get("image") : image;

		String issueKey = extractIssueKey(containerName + " " + imageName);
		if (issueKey == null) {
			System.out.println("No issue key found in docker payload");
			return;
		}

		Long issueId = Long.parseLong(issueKey.split("-")[1]);

		switch (status.toLowerCase()) {
		case "start":
			issueClient.updateStatus(issueId, IssueStatus.DEPLOYMENT, "Docker");
			issueClient.addCommit(issueId, "Docker", "Container started | Image: " + imageName);
			break;
		case "die":
			issueClient.updateStatus(issueId, IssueStatus.BLOCKS, "Docker");

			issueClient.addCommit(issueId, "Docker", "Container crashed | Image: " + imageName);
			break;
		case "pull":
			issueClient.addCommit(issueId, "Docker", "Image pulled: " + imageName);
			break;
		case "build":
			issueClient.addCommit(issueId, "Docker", "Image built: " + imageName);
			break;
		default:
			issueClient.addCommit(issueId, "Docker", "Event: " + status + " | Image: " + imageName);
		}
	}

	private String extractIssueKey(String text) {
		if (text == null)
			return null;
		Matcher matcher = Pattern.compile("([A-Z]+-\\d+)").matcher(text);
		if (matcher.find())
			return matcher.group(1);
		return null;
	}

	private Long extractId(String issueKey) {

		return Long.parseLong(issueKey.split("-")[1]);
	}
}