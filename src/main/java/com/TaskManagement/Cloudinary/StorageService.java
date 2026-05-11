package com.TaskManagement.Cloudinary;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	CloudinaryUploadResponse store(MultipartFile file, String folder);

	byte[] read(String cloudURL);

	void delete(String cloudId);

}
