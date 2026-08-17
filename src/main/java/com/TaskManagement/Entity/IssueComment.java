package com.TaskManagement.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "issue_comments", indexes = { @Index(name = "idx_comment_issue", columnList = "issue_id") })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "issue_id", nullable = false)
	private Long issueId;
	private String authorEmail;
	@Column(length = 5000)
	private String body;
	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}