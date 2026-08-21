package com.TaskManagement.Entity;

import java.time.LocalDateTime;
import com.TaskManagement.Enum.IssueStatus;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "issue_status_history")
public class IssueStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long issueId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus newStatus;

    @Column(nullable = false)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();
}