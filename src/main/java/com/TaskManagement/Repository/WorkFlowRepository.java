package com.TaskManagement.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TaskManagement.Entity.WorkFlow;

@Repository
public interface WorkFlowRepository extends JpaRepository<WorkFlow, Long> {

	Optional<WorkFlow> findByName(String name);

}
