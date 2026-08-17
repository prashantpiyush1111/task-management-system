package com.TaskManagement.Service;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.TaskManagement.Entity.FileAttachment;
import com.TaskManagement.Repository.FileAttachmentRepository;
import com.cloudinary.Cloudinary;
@Service
public class AttachmentService {
	@Autowired
	private Cloudinary cloudinary;
	@Autowired
	private FileAttachmentRepository attachmentRepo;
	public FileAttachment upload(Long issueId, MultipartFile file, String uploadedBy, Long organizationId) {
		validateFile(file);
		try {
			Map<String, Object> uploadOption = new HashMap<>();
			uploadOption.put("resource_type", "auto");
			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
					uploadOption);
			FileAttachment attach = new FileAttachment();
			attach.setIssueId(issueId);
			attach.setOrganizationId(organizationId);
			attach.setFileName(file.getOriginalFilename());
			attach.setFileContentType(file.getContentType());
			attach.setFileSize(file.getSize());
			attach.setStoragePath(uploadResult.get("secure_url").toString());
			attach.setCloudId(uploadResult.get("public_id").toString());
			attach.setUplodedBy(uploadedBy);
			return attachmentRepo.save(attach);
		} catch (Exception e) {
			throw new RuntimeException("Cloud upload failed");
		}
	}
	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new RuntimeException("file can not be empty");
		}
		long MAX = 5 * 1024 * 1024;
		if (file.getSize() > MAX) {
			throw new RuntimeException("Max file size is 5MB");
		}
		List<String> allowedFile = Arrays.asList("image/png", "image/jpeg", "application/pdf", "text/plain");
		if (!allowedFile.contains(file.getContentType())) {
			throw new RuntimeException("invalid file format");
		}
	}
	public List<FileAttachment> getFileByIssueId(Long issueId, Long organizationId) {
		return attachmentRepo.findByIssueId(issueId).stream()
				.filter(a -> organizationId.equals(a.getOrganizationId()))
				.toList();
	}
	public FileAttachment getFileById(Long id, Long organizationId) {
		FileAttachment attach = attachmentRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("file not found"));
		if (!organizationId.equals(attach.getOrganizationId())) {
			throw new RuntimeException("file not found");
		}
		return attach;
	}
	public void delete(Long id, Long organizationId) {
		FileAttachment atch = getFileById(id, organizationId);
		try {
			Map<String, Object> options = new HashMap<>();
			options.put("resource_type", "auto");
			cloudinary.uploader().destroy(atch.getCloudId(), options);
			attachmentRepo.delete(atch);
		} catch (Exception e) {
			throw new RuntimeException("Delete Failed", e);
		}
	}
}