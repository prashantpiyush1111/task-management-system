package com.TaskManagement.Controller;

import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.TaskManagement.Entity.FileAttachment;
import com.TaskManagement.Service.AttachmentService;

@RestController
@RequestMapping("/api/attachments")
public class FileAttachmentController {

	@Autowired
	private AttachmentService attachmentService;

	@PostMapping("/upload/{issueId}")
	public ResponseEntity<FileAttachment> upload(@PathVariable Long issueId, @RequestParam("file") MultipartFile file,
			@RequestParam String uploadedBy) {
		return ResponseEntity.ok(attachmentService.upload(issueId, file, uploadedBy));
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Void> download(@PathVariable Long id) {
		FileAttachment attachment = attachmentService.getFileById(id);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, attachment.getStoragePath())
				.build();
	}

	@GetMapping("/download/stream/{id}")
	public ResponseEntity<Resource> stream(@PathVariable long id) throws java.io.IOException {

		FileAttachment attachment = attachmentService.getFileById(id);
		URI uri = URI.create(attachment.getStoragePath());
		URL url = uri.toURL();
		InputStream inputStream = url.openStream();
		InputStreamResource resource = new InputStreamResource(inputStream);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
				.contentType(MediaType.parseMediaType(attachment.getFileContentType())).body(resource);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		attachmentService.delete(id);
		return ResponseEntity.ok("File deleted successfully");
	}
}