package com.TaskManagement.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TaskManagement.Entity.Label;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

	Optional<Label> findByName(String name);

}