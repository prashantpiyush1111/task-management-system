package com.TaskManagement.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TaskManagement.Entity.Board;
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	Optional<Board> findByProjectKey(String projectKey);
	List<Board> findByOrganizationId(Long organizationId);
}