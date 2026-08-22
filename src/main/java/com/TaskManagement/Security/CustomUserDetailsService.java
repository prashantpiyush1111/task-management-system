package com.TaskManagement.Security;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Enum.Permission;
import com.TaskManagement.Repository.UserAuthRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserAuthRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String userOfficialEmail) throws UsernameNotFoundException {

		UserAuth user = userRepo.findByUserOfficialEmailIgnoreCase(userOfficialEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Set<Permission> permissions = RolebasedPermissionConfig.getrole_permission().get(user.getRole());

		List<GrantedAuthority> authorities = new ArrayList<>();

		if (permissions != null) {
			authorities = permissions.stream().map(p -> new SimpleGrantedAuthority(p.name()))
					.collect(Collectors.toList());
		}

		// Role bhi authority me add
		authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return new org.springframework.security.core.userdetails.User(user.getUserOfficialEmail(), user.getPassword(),
				authorities);
	}
}