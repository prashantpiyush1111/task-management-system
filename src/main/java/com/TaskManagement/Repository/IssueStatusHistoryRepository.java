package com.TaskManagement.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TaskManagement.Entity.IssueStatusHistory;

public interface IssueStatusHistoryRepository
        extends JpaRepository<IssueStatusHistory, Long> {

    List<IssueStatusHistory> findByIssueIdOrderByChangedAtDesc(Long issueId);
}