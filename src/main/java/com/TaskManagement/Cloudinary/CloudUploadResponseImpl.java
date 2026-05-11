package com.TaskManagement.Cloudinary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.net.URI;

import java.util.Map;

@Service
public class CloudUploadResponseImpl implements StorageService {

	@Autowired
	private Cloudinary cloudinary;

	@Override
	public CloudinaryUploadResponse store(MultipartFile file, String folder) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> upload = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("folder", folder, "resource_type", "auto"));

			String url = (String) upload.get("secure_url");
			String cloudId = (String) upload.get("public_id");

			return new CloudinaryUploadResponse(url, cloudId);

		} catch (Exception e) {
			throw new RuntimeException("Cloudinary upload failed", e);
		}
	}

	@Override
	public byte[] read(String cloudURL) {
		try {
			URI uri = URI.create(cloudURL);
			return uri.toURL().openStream().readAllBytes();
		} catch (Exception e) {
			throw new RuntimeException("File read failed", e);
		}
	}

	@Override
	public void delete(String cloudId) {
		try {
			cloudinary.uploader().destroy(cloudId, ObjectUtils.emptyMap());
		} catch (Exception e) {
			throw new RuntimeException("Cloudinary delete failed", e);
		}
	}
}