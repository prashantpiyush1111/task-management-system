package com.TaskManagement.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TaskManagement.Entity.FileAttachment;
import java.util.List;
@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {
	List<FileAttachment> findByIssueId(Long issueId);
	List<FileAttachment> findByOrganizationId(Long organizationId);
}