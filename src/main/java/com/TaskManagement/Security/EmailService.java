package com.TaskManagement.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.mail.from}")
	private String fromEmail;

	@Value("${app.base-url:http://localhost:9999}")
	private String baseUrl;

	public void sendResetPassword(String to, String token) {

		String resetLink = baseUrl + "/reset-password.html?token=" + token;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(to);
		message.setSubject("Reset your password");
		message.setText("Click the link below to reset your password:\n\n" + resetLink
				+ "\n\nThis link will expire in 10 minutes.");

		mailSender.send(message);
	}
}