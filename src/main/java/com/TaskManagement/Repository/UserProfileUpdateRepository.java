package com.TaskManagement.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.Entity.UserProfileUpdate;

@Repository
public interface UserProfileUpdateRepository extends JpaRepository<UserProfileUpdate, Long> {

	Optional<UserProfileUpdate> findByUserOfficialEmail(String userOfficialEmail);

}