package com.TaskManagement.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.Entity.UserAuth;
import com.TaskManagement.Enum.UserStatus;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

	Optional<UserAuth> findByUserOfficialEmail(String userOfficialEmail);

	Optional<UserAuth> findByResetToken(String resetToken);

	List<UserAuth> findByOrganizationIdAndStatus(Long organizationId, UserStatus status);

	Optional<UserAuth> findByIdAndOrganizationId(Long id, Long organizationId);
	List<UserAuth> findByOrganizationId(Long organizationId);
}