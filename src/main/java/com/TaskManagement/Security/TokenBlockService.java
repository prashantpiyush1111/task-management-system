package com.TaskManagement.Security;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class TokenBlockService {

	private final Set<String> blockedTokens = ConcurrentHashMap.newKeySet();

	public void blockToken(String token) {
		blockedTokens.add(token);
	}

	public boolean isBlocked(String token) {
		return blockedTokens.contains(token);
	}
}