package com.TaskManagement.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.Entity.WorkFlowTransaction;
import com.TaskManagement.Enum.IssueStatus;

@Repository
public interface WorkFlowTransactionRepository extends JpaRepository<WorkFlowTransaction, Long> {

	List<WorkFlowTransaction> findByWorkFlowId(Long workFlowId);

	List<WorkFlowTransaction> findByWorkFlowIdAndFromStatus(Long workFlowId, IssueStatus fromStatus);
}