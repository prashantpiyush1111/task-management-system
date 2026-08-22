package com.TaskManagement.Controller;

import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.TaskManagement.Entity.FileAttachment;
import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Repository.UserAuthRepository;
import com.TaskManagement.Service.AttachmentService;

@RestController
@RequestMapping("/api/attachments")
public class FileAttachmentController {

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private UserAuthRepository userAuthRepository;

	private Long resolveOrganizationId(Authentication authentication) {
		UserAuth user = userAuthRepository.findByUserOfficialEmailIgnoreCase(authentication.getName())
				.orElseThrow(() -> new RuntimeException("User not found"));
		return user.getOrganization().getId();
	}

	@PostMapping("/upload/{issueId}")
	public ResponseEntity<FileAttachment> upload(@PathVariable Long issueId,
			@RequestParam("file") MultipartFile file,
			Authentication authentication) {
		String uploadedBy = authentication.getName();
		Long organizationId = resolveOrganizationId(authentication);
		return ResponseEntity.ok(attachmentService.upload(issueId, file, uploadedBy, organizationId));
	}
	@GetMapping("/issue/{issueId}")
	public ResponseEntity<List<FileAttachment>> getAttachments(
	        @PathVariable Long issueId,
	        Authentication authentication) {

	    Long organizationId = resolveOrganizationId(authentication);

	    return ResponseEntity.ok(
	            attachmentService.getFileByIssueId(issueId, organizationId)
	    );
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Void> download(@PathVariable Long id, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		FileAttachment attachment = attachmentService.getFileById(id, organizationId);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, attachment.getStoragePath())
				.build();
	}

	@GetMapping("/download/stream/{id}")
	public ResponseEntity<Resource> stream(@PathVariable long id, Authentication authentication) throws java.io.IOException {
		Long organizationId = resolveOrganizationId(authentication);
		FileAttachment attachment = attachmentService.getFileById(id, organizationId);
		URI uri = URI.create(attachment.getStoragePath());
		URL url = uri.toURL();
		InputStream inputStream = url.openStream();
		InputStreamResource resource = new InputStreamResource(inputStream);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
				.contentType(MediaType.parseMediaType(attachment.getFileContentType())).body(resource);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id, Authentication authentication) {
		Long organizationId = resolveOrganizationId(authentication);
		attachmentService.delete(id, organizationId);
		return ResponseEntity.ok("File deleted successfully");
	}
}